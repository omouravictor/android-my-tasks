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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TasksOnHoldFragment onHoldTasksFragment;
    FinishedTasksFragment finishedTasksFragment;
    AlertDialog.Builder builder;
    ActivityResultLauncher<Intent> actResult;
    TaskAdapter onHoldTaskAdapter, finishedTaskAdapter;
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
        builder = new AlertDialog.Builder(this);
        startBtnAdd();
        startActivityResult();
        startAdaptersAndFragments();
        startLayoutTab();
    }

    private void startBtnAdd() {
        btnAdd = findViewById(R.id.btnAdd);
        addActivityIntent = new Intent(this, AddActivity.class);
        btnAdd.setOnClickListener(v -> actResult.launch(addActivityIntent));
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
                            onHoldTaskAdapter.addTask(intentTask);
                        } else if (resultCode == 2) {
                            int position = result.getData().getIntExtra("position", 0);
                            onHoldTaskAdapter.updateTask(position, intentTask);
                        }
                    }
                }
        );
    }

    public void startAdaptersAndFragments() {
        LocalDate currentDate = new LocalDate();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

        myDB.deleteAllTasks();
        TaskModel teste = new TaskModel();
        teste.setName("TESTE1");
        teste.setSlaDate(currentDate.toString(dtf));
        teste.finish(currentDate.toString(dtf));
        myDB.createTask(new TaskModel("A", currentDate.toString(dtf)));
        myDB.createTask(new TaskModel("B", "23/04/2022"));
        myDB.createTask(teste);

        onHoldTaskAdapter = new TaskAdapter(this, actResult, myDB, myDB.getAllTasksOnHold(), dtf, currentDate);
        finishedTaskAdapter = new TaskAdapter(this, actResult, myDB, myDB.getAllFinishedTasks(), dtf, currentDate);

        onHoldTaskAdapter.setFinishedTasksAdapter(finishedTaskAdapter);
        finishedTaskAdapter.setOnHoldTaskAdapter(onHoldTaskAdapter);

        onHoldTasksFragment = new TasksOnHoldFragment(onHoldTaskAdapter);
        finishedTasksFragment = new FinishedTasksFragment(finishedTaskAdapter);
    }

    public void startLayoutTab() {
        vp2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        vpAdapter = new ViewPagerAdapter(this);
        vpAdapter.addFragment(onHoldTasksFragment, "Aguardando");
        vpAdapter.addFragment(finishedTasksFragment, "Concluídas");
        vp2.setAdapter(vpAdapter);
        new TabLayoutMediator(
                tabLayout, vp2, (tab, position) -> tab.setText(vpAdapter.getTitles().get(position))
        ).attach();
    }

    private void startSortBuilder() {
        builder.setMessage("Deseja ordenar por tempo de expiração?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            onHoldTaskAdapter.sortTaskArrayBySlaDate();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startFinishAllBuilder() {
        builder.setMessage("Deseja concluir todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            ArrayList<TaskModel> tasks = onHoldTaskAdapter.getAllTasks();
            finishedTaskAdapter.addFinishedTasks(tasks);
            onHoldTaskAdapter.deleteAllTasks();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startDeleteAllBuilder() {
        builder.setMessage("Deseja excluir todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            myDB.deleteAllTasks();
            finishedTaskAdapter.deleteAllTasks();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
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
        if (id == R.id.sortBySlaDate)
            startSortBuilder();
        else if (id == R.id.finishAll)
            startFinishAllBuilder();
        else if (id == R.id.deleteAll)
            startDeleteAllBuilder();
        return super.onOptionsItemSelected(item);
    }
}