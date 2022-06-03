package com.example.tasks.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.adapter.RequirementsAdapter;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class RequirementsActivity extends AppCompatActivity {

    RequirementsAdapter requirementsAdapter;
    RecyclerView rvTasksOnHold;
    LinearLayout saveLayout;
    TaskModel task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = getIntent().getParcelableExtra("task");

        setTitle(task.getTittle());
        init();
    }

    void init() {
        initView();
        initAdapterAndRecyclerView();

        saveLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            List<Integer> requirementsID = requirementsAdapter.getRequirements();
            intent.putExtra("requirements", (ArrayList<Integer>) requirementsID);
            setResult(1, intent);
            finish();
        });
    }

    void initView() {
        rvTasksOnHold = findViewById(R.id.rvTasksOnHold);
        saveLayout = findViewById(R.id.saveLayout);
    }

    void initAdapterAndRecyclerView() {
        SQLiteHelper myDB = new SQLiteHelper(this);

        requirementsAdapter = new RequirementsAdapter(myDB, task);

        rvTasksOnHold.setLayoutManager(new LinearLayoutManager(this));
        rvTasksOnHold.setAdapter(requirementsAdapter);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_requirement_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        else if (id == R.id.search) {
        }

        return true;
    }
}