package com.example.tasks.activity;

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

import com.example.tasks.R;
import com.example.tasks.adapter.FinishedTaskAdapter;
import com.example.tasks.adapter.OnHoldTaskAdapter;
import com.example.tasks.adapter.ViewPagerAdapter;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.fragment.FinishedTaskFragment;
import com.example.tasks.fragment.TasksOnHoldFragment;
import com.example.tasks.model.CategoryModel;
import com.example.tasks.model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class CategoryTasksActivity extends AppCompatActivity {

    CategoryModel category;
    int catAdaptPosition;
    TasksOnHoldFragment fragOnHoldTasks;
    FinishedTaskFragment fragFinishedTasks;
    AlertDialog.Builder builder;
    ActivityResultLauncher<Intent> actResult;
    OnHoldTaskAdapter adaptOnHoldTasks;
    FinishedTaskAdapter adaptFinishedTasks;
    FloatingActionButton btnAdd;
    SQLiteHelper myDB;
    TabLayout tabLayout;
    ViewPager2 vp2;
    Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tasks);
        getSupportActionBar().setElevation(0);
        category = getIntent().getParcelableExtra("category");
        setTitle(category.getName());
        init();
    }

    void init() {
        initView();
        initVariables();
        startBtnAdd();
        startActivityResult();
        startAdaptersAndFragments();
        startTabLayout();
        startViewPager();
    }

    void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        vp2 = findViewById(R.id.viewPager2);
        btnAdd = findViewById(R.id.btnAdd);
    }

    void initVariables() {
        catAdaptPosition = getIntent().getIntExtra("catAdaptPosition", -1);
        myDB = new SQLiteHelper(this);
        builder = new AlertDialog.Builder(this);
    }

    void startBtnAdd() {
        Intent createTaskIntent = new Intent(this, CreateTaskActivity.class);

        btnAdd.setOnClickListener(v -> {
            createTaskIntent.putExtra("category", category);
            actResult.launch(createTaskIntent);
        });
    }

    void startActivityResult() {
        actResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != Activity.RESULT_CANCELED) {
                        TaskModel task = result.getData().getParcelableExtra("task");
                        if (resultCode == 1) {
                            adaptOnHoldTasks.addRow(task);
                        } else if (resultCode == 2) {
                            int position = result.getData().getIntExtra("taskAdaptPosition", 0);
                            if (task.isFinished()) adaptFinishedTasks.updateRow(position, task);
                            else adaptOnHoldTasks.updateRow(position, task);
                        }
                    }
                }
        );
    }

    void startAdaptersAndFragments() {
        adaptOnHoldTasks = new OnHoldTaskAdapter(this, catAdaptPosition, actResult, myDB, category);
        adaptFinishedTasks = new FinishedTaskAdapter(this, catAdaptPosition, actResult, myDB, category);

        adaptOnHoldTasks.setFinishedTasksAdapter(adaptFinishedTasks);
        adaptFinishedTasks.setOnHoldTaskAdapter(adaptOnHoldTasks);

        fragOnHoldTasks = new TasksOnHoldFragment(adaptOnHoldTasks);
        fragFinishedTasks = new FinishedTaskFragment(adaptFinishedTasks);
    }

    void startTabLayout() {
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
            }
        });
    }

    void startViewPager() {
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(this);

        vpAdapter.addFragment(fragOnHoldTasks, "Em espera");
        vpAdapter.addFragment(fragFinishedTasks, "Concluídas");
        vp2.setAdapter(vpAdapter);

        new TabLayoutMediator(
                tabLayout, vp2, (tab, position) -> tab.setText(vpAdapter.getTitles().get(position))
        ).attach();
    }

    void startSortBuilder() {
        if (isFirstTabSelected()) {
            builder.setMessage("Deseja ordenar por data de expiração?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                adaptOnHoldTasks.setAllTasks(myDB.getAllOnHoldTasksOfCategory(category.getId()));
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        } else {
            builder.setMessage("Deseja ordenar por data de conclusão?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                adaptFinishedTasks.setAllTasks(myDB.getAllFinishedTasksOfCategory(category.getId()));
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        }
        builder.show();
    }

    void startDeleteAllBuilder() {
        if (isFirstTabSelected()) {
            builder.setMessage("Deseja excluir todas as tarefas em espera?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                myDB.deleteOnHoldTasks(category.getId());
                adaptOnHoldTasks.deleteAllRowS();
                setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
                dialog.dismiss();
            });
        } else {
            builder.setMessage("Deseja excluir todas as tarefas concluídas?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                myDB.deleteFinishedTasks(category.getId());
                adaptFinishedTasks.deleteAllRowS();
                setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
                dialog.dismiss();
            });
        }
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    void startFinishAllBuilder() {
        builder.setMessage("Deseja concluir todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            List<TaskModel> tasks = adaptOnHoldTasks.getAllTasks();
            adaptFinishedTasks.putTasksAsFinished(tasks);
            adaptFinishedTasks.addRowS(tasks);
            adaptOnHoldTasks.deleteAllRowS();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    void startUndoAllBuilder() {
        builder.setMessage("Deseja desfazer todas as tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            List<TaskModel> tasks = adaptFinishedTasks.getAllTasks();
            adaptOnHoldTasks.putTasksAsOnHold(tasks);
            adaptOnHoldTasks.addRowS(tasks);
            adaptFinishedTasks.deleteAllRowS();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    void myOnTabSelected(TabLayout.Tab tab) {
        if (myMenu != null) {
            if (tab.getPosition() == 0) {
                myMenu.getItem(2).setVisible(true);
                myMenu.getItem(3).setVisible(false);
            } else {
                myMenu.getItem(2).setVisible(false);
                myMenu.getItem(3).setVisible(true);
            }
        }
    }

    boolean isFirstTabSelected() {
        return tabLayout.getTabAt(0).isSelected();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuBuilder m = (MenuBuilder) menu;
        myMenu = menu;

        m.setOptionalIconsVisible(true);
        getMenuInflater().inflate(R.menu.my_task_menu, menu);
        menu.getItem(3).setVisible(false);

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