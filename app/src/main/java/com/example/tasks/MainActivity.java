package com.example.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    TasksOnHoldFragment tasksOnHoldFragment;
    FinishedTasksFragment finishedTasksFragment;
    AlertDialog.Builder finishAllBuilder, sortBuilder;
    ActivityResultLauncher<Intent> actResult;
    TaskAdapter adapter;
    SQLiteHelper myDB;
    TabLayout tabLayout;
    ViewPager2 vp2;
    ViewPagerAdapter vpAdapter;
    FloatingActionButton btnAdd;
    Intent addActivityIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        myDB = new SQLiteHelper(this);
        startBtnAdd();
        startFinishAllBuilder();
        startActivityResult();
        startSortBuilder();
        startAdapter();
        startFragments();
        startLayoutTab();
    }

    public void startLayoutTab() {
        vp2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        vpAdapter = new ViewPagerAdapter(this);
        vpAdapter.addFragment(tasksOnHoldFragment, "Aguardando");
        vpAdapter.addFragment(finishedTasksFragment, "Concluídas");
        vp2.setAdapter(vpAdapter);
        new TabLayoutMediator(
                tabLayout, vp2, (tab, position) -> tab.setText(vpAdapter.getTitles().get(position))
        ).attach();
    }

    public void startFragments() {
        tasksOnHoldFragment = new TasksOnHoldFragment(adapter);
        finishedTasksFragment = new FinishedTasksFragment(adapter);
        adapter.setFinishedTasksAdapter(finishedTasksFragment.getFinishedTasksAdapter());
    }

    public void startAdapter() {
        LocalDate currentDate = new LocalDate();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

        myDB.deleteAllTasks();
        TaskModel teste = new TaskModel();
        teste.setName("TESTE1");
        teste.setSlaDate("07/02/2022");
        teste.setIsFinished(1);
        TaskModel teste2 = new TaskModel();
        teste2.setName("TESTE2");
        teste2.setSlaDate("07/02/2022");
        teste2.setIsFinished(1);
        TaskModel teste3 = new TaskModel();
        teste3.setName("TESTE3");
        teste3.setSlaDate("07/02/2022");
        teste3.setIsFinished(1);
        myDB.createTask(new TaskModel("A", "26/03/2022"));
        myDB.createTask(new TaskModel("B", "23/02/2022"));
        myDB.createTask(new TaskModel("C", "31/01/2022"));
        myDB.createTask(teste);
        myDB.createTask(teste2);
        myDB.createTask(teste3);

        adapter = new TaskAdapter(this, actResult, myDB, dtf, currentDate);
    }

    private void startActivityResult() {
        actResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != Activity.RESULT_CANCELED) {
                        assert result.getData() != null;
                        TaskModel intentTask = result.getData().getParcelableExtra("task");
                        if (resultCode == 1) {
                            adapter.addTask(intentTask);
                        } else if (resultCode == 2) {
                            int position = result.getData().getIntExtra("position", 0);
                            adapter.updateTask(position, intentTask);
                        }
                    }
                }
        );
    }

    private void startSortBuilder() {
        sortBuilder = new AlertDialog.Builder(this);
        sortBuilder.setMessage("Deseja ordenar por tempo de expiração?");
        sortBuilder.setPositiveButton("Sim", (dialog, which) -> {
            adapter.sortTaskArrayBySlaDate();
            dialog.dismiss();
        });
        sortBuilder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
    }

    private void startFinishAllBuilder() {
        finishAllBuilder = new AlertDialog.Builder(this);
        finishAllBuilder.setMessage("Deseja concluir todas as tarefas?");
        finishAllBuilder.setPositiveButton("Sim", (dialog, which) -> {
            myDB.deleteAllTasks();
            adapter.deleteAllTasks();
            dialog.dismiss();
        });
        finishAllBuilder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
    }

    private void startBtnAdd() {
        btnAdd = findViewById(R.id.btnAdd);
        addActivityIntent = new Intent(this, AddActivity.class);
        btnAdd.setOnClickListener(v -> {
            actResult.launch(addActivityIntent);
            if (adapter.getActionMode() != null)
                adapter.getActionMode().finish();
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuBuilder m = (MenuBuilder) menu;
        m.setOptionalIconsVisible(true);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.finishAll)
            finishAllBuilder.show();
        else if (id == R.id.sortBySlaDate)
            sortBuilder.show();
        return super.onOptionsItemSelected(item);
    }
}