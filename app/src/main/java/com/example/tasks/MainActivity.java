package com.example.tasks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> mainActivityResult;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setAdapterOnClickListener();
        setBtnAddOnClickListener();
    }

    private void initView() {
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        myDB = new SQLiteHelper(this);
        adapter = new TaskAdapter(myDB.getAllTasks());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        startMainActivityResult();
    }

    public void startMainActivityResult() {
        mainActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_CANCELED) {
                        TaskModel taskIntentData = result.getData().getParcelableExtra("task");
                        if (result.getResultCode() == 1) {
                            adapter.addTask(taskIntentData);
                        } else if (result.getResultCode() == 2) {
                            int updatedPosition = result.getData().getIntExtra("position", 0);
                            adapter.updateTask(updatedPosition, taskIntentData);
                        }
                    }
                }
        );
    }

    public void setAdapterOnClickListener() {
        adapter.setOnClickListenerInterface(position -> v -> {
            Intent intent = new Intent(this, UpdateActivity.class);
            TaskModel task = adapter.getTask(position);
            intent.putExtra("task", task);
            intent.putExtra("position", position);
            mainActivityResult.launch(intent);
        });
    }

    public void setBtnAddOnClickListener() {
        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddActivity.class);
            mainActivityResult.launch(intent);
        });
    }

}