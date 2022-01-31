package com.example.tasks;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResult;
    AlertDialog.Builder finishAllBuilder, sortBuilder;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    Intent addActivityIntent;
    TaskAdapter adapter;
    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        addActivityIntent = new Intent(this, AddActivity.class);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        myDB = new SQLiteHelper(this);

        startSortBuilder();
        startFinishAllBuilder();
        startActivityResult();
        startAdapterAndRecyclerView();

        setBtnAddOnClickListener();
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

    private void startActivityResult() {
        activityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != RESULT_CANCELED) {
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

    public void startAdapterAndRecyclerView() {
        LocalDate currentDate = new LocalDate();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        ArrayList<TaskModel> allTasks = myDB.getAllTasks();

        adapter = new TaskAdapter(this, activityResult, myDB, allTasks, dtf, currentDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setBtnAddOnClickListener() {
        btnAdd.setOnClickListener(v -> {
            activityResult.launch(addActivityIntent);
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
        if (item.getItemId() == R.id.finishAll)
            finishAllBuilder.show();
        else if (item.getItemId() == R.id.sortBySlaDate)
            sortBuilder.show();
        return super.onOptionsItemSelected(item);
    }
}