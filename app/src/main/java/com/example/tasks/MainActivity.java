package com.example.tasks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> addActivityResultLauncher;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startVariables();
        startRecyclerView();

        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddActivity.class);
            addActivityResultLauncher.launch(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            recreate();
    }

    private void startVariables() {
        myDB = new SQLiteHelper(this);
        adapter = new TaskAdapter(this, myDB.getAllTasks());
        btnAdd = findViewById(R.id.btnAdd);
        addActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    recreate();
                }
        );
    }

    private void startRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}