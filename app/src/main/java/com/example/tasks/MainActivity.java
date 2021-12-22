package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

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
            startActivity(intent);
        });
    }

    private void startVariables() {
        myDB = new SQLiteHelper(this);
        adapter = new TaskAdapter(myDB.getAllTasks());
        btnAdd = findViewById(R.id.btnAdd);
    }

    private void startRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}