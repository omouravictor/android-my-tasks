package com.example.tasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);
        if (getIntent().hasExtra("taskTittle"))
            setTitle("Requisitos: " + getIntent().getStringExtra("taskTittle"));
        else
            setTitle(R.string.requirements);
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
        TaskModel task = getIntent().getParcelableExtra("task");

        requirementsAdapter = new RequirementsAdapter(myDB, task);

        rvTasksOnHold.setLayoutManager(new LinearLayoutManager(this));
        rvTasksOnHold.setAdapter(requirementsAdapter);
    }
}