package com.example.tasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpdateOnHoldTaskActivity extends AppCompatActivity {

    DateTimeFormatter dtf;
    MyFunctions myFunctions;
    EditText etTittle, etExpirationDate, etDescription;
    Button btnClear, btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.task);
        setContentView(R.layout.activity_update_on_hold_task);
        init();
    }

    void init() {
        Intent intent = getIntent();
        TaskModel task = intent.getParcelableExtra("task");
        int taskAdaptPosition = intent.getIntExtra("taskAdaptPosition", -1);

        initView();
        initMyFunctions();
        setTaskData(task);

        btnUpdate.setOnClickListener((v) -> {
            if (!myFunctions.isEmpty(this, etTittle, etExpirationDate))
                updateTask(task, taskAdaptPosition);
        });

    }

    void initView() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        etTittle = findViewById(R.id.etTittle);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etDescription = findViewById(R.id.etDescription);
        btnClear = findViewById(R.id.btnClear);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    void initMyFunctions() {
        myFunctions = new MyFunctions();

        myFunctions.setActionDoneButton(etTittle);
        myFunctions.setActionDoneButton(etDescription);
        myFunctions.setOnClickEtDateListener(this, etExpirationDate);
        myFunctions.clearEditTexts(btnClear, etTittle, etDescription, etExpirationDate);
    }

    void setTaskData(TaskModel task) {
        LocalDate date = LocalDate.parse(task.getExpirationDate());

        etTittle.setText(task.getTittle());
        etExpirationDate.setText(date.toString(dtf));
        etDescription.setText(task.getDescription());
    }

    void setAttributes(TaskModel task) {
        LocalDate expirationDate = LocalDate.parse(etExpirationDate.getText().toString(), dtf);

        task.setTittle(etTittle.getText().toString());
        task.setExpirationDate(expirationDate.toString());
        task.setDescription(etDescription.getText().toString());
    }

    TaskModel getUpdatedTask(TaskModel task) {
        SQLiteHelper myDB = new SQLiteHelper(this);

        setAttributes(task);
        myDB.updateTask(task);

        return task;
    }

    void finishUpdate(TaskModel task, int taskAdaptPosition) {
        Intent intent = new Intent();

        intent.putExtra("task", task);
        intent.putExtra("taskAdaptPosition", taskAdaptPosition);
        setResult(2, intent);
        finish();
    }

    void updateTask(TaskModel task, int taskAdaptPosition) {
        btnUpdate.setClickable(false);

        try {
            TaskModel updatedTask = getUpdatedTask(task);
            finishUpdate(updatedTask, taskAdaptPosition);
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnUpdate.setClickable(true);
        }
    }
}