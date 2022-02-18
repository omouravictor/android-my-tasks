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

    TasksOnHoldFragment fragOnHoldTasks;
    FinishedTasksFragment fragFinishedTasks;
    AlertDialog.Builder builder;
    ActivityResultLauncher<Intent> actResult;
    TaskAdapter adaptOnHoldTasks, adaptFinishedTasks;
    SQLiteHelper myDB;
    TabLayout tabLayout;
    ViewPager2 vp2;
    ViewPagerAdapter vpAdapter;
    FloatingActionButton btnAdd;
    Intent addActivityIntent;
    Menu myMenu;

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
                        TaskModel task = result.getData().getParcelableExtra("task");
                        if (resultCode == 1) {
                            adaptOnHoldTasks.addTask(task);
                        } else if (resultCode == 2) {
                            int position = result.getData().getIntExtra("position", 0);
                            adaptOnHoldTasks.updateTask(position, task);
                        }
                    }
                }
        );
    }

    public void startAdaptersAndFragments() {
        LocalDate currentDate = new LocalDate();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

        /*
        myDB.deleteAllTasks();
        myDB.createTask(new TaskModel("TESTE1", "10/02/2022", 1, "10/02/2022"));
        myDB.createTask(new TaskModel("TESTE2", currentDate.toString(dtf), 1, currentDate.toString(dtf)));
        myDB.createTask(new TaskModel("TESTE3", currentDate.toString(dtf)));
        myDB.createTask(new TaskModel("TESTE4", "23/04/2022"));
         */

        adaptOnHoldTasks = new TaskAdapter(this, actResult, myDB, myDB.getAllTasksOnHold(), dtf, currentDate);
        adaptFinishedTasks = new TaskAdapter(this, actResult, myDB, myDB.getAllFinishedTasks(), dtf, currentDate);

        adaptOnHoldTasks.setFinishedTasksAdapter(adaptFinishedTasks);
        adaptFinishedTasks.setOnHoldTaskAdapter(adaptOnHoldTasks);

        fragOnHoldTasks = new TasksOnHoldFragment(adaptOnHoldTasks);
        fragFinishedTasks = new FinishedTasksFragment(adaptFinishedTasks);
    }

    public void startLayoutTab() {
        vp2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        vpAdapter = new ViewPagerAdapter(this);
        vpAdapter.addFragment(fragOnHoldTasks, "Em espera");
        vpAdapter.addFragment(fragFinishedTasks, "Concluídas");
        vp2.setAdapter(vpAdapter);
        new TabLayoutMediator(
                tabLayout, vp2, (tab, position) -> tab.setText(vpAdapter.getTitles().get(position))
        ).attach();
    }

    private void startSortBuilder() {
        if (tabLayout.getTabAt(0).isSelected()) {
            builder.setMessage("Deseja ordenar por tempo de expiração?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                adaptOnHoldTasks.sortTasksArrayBySlaDate();
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        } else {
            builder.setMessage("Deseja ordenar por tempo de conclusão?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                adaptFinishedTasks.sortTasksArrayByFinishedDate();
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        }
        builder.show();
    }

    private void startDeleteAllBuilder() {
        if (tabLayout.getTabAt(0).isSelected()) {
            builder.setMessage("Deseja excluir todas as tarefas em espera?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                myDB.deleteOnHoldTasks();
                adaptOnHoldTasks.deleteAllTasks();
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        } else {
            builder.setMessage("Deseja excluir todas as tarefas concluídas?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                myDB.deleteFinishedTasks();
                adaptFinishedTasks.deleteAllTasks();
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        }
        builder.show();
    }

    private void startFinishAllBuilder() {
        builder.setMessage("Deseja concluir todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            ArrayList<TaskModel> tasks = adaptOnHoldTasks.getAllTasks();
            adaptFinishedTasks.putTasksAsFinished(tasks);
            adaptFinishedTasks.addAllTasks(tasks);
            adaptOnHoldTasks.deleteAllTasks();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startUndoAllBuilder() {
        builder.setMessage("Deseja desfazer todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            ArrayList<TaskModel> tasks = adaptFinishedTasks.getAllTasks();
            adaptOnHoldTasks.putTasksAsOnHold(tasks);
            adaptOnHoldTasks.addAllTasks(tasks);
            adaptFinishedTasks.deleteAllTasks();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void myOnTabSelected(TabLayout.Tab tab) {
        MenuItem finishAll = myMenu.getItem(2);
        MenuItem undoAll = myMenu.getItem(3);
        if (tab.getPosition() == 0) {
            finishAll.setVisible(true);
            undoAll.setVisible(false);
        } else {
            finishAll.setVisible(false);
            undoAll.setVisible(true);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuBuilder m = (MenuBuilder) menu;
        myMenu = menu;

        m.setOptionalIconsVisible(true);
        getMenuInflater().inflate(R.menu.my_menu, menu);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                myOnTabSelected(tab);
            }
        });

        tabLayout.getTabAt(0).select();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortBySlaDate)
            startSortBuilder();
        else if (id == R.id.deleteAll)
            startDeleteAllBuilder();
        else if (id == R.id.finishAll)
            startFinishAllBuilder();
        else if (id == R.id.undoAll)
            startUndoAllBuilder();
        return super.onOptionsItemSelected(item);
    }
}