package com.example.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etTask, etSlaDate;
    Button btnClear, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
    }

    private void init() {
        myFunctions = new MyFunctions();
        etTask = findViewById(R.id.etTaskAdd);
        etSlaDate = findViewById(R.id.etSlaDateAdd);
        btnClear = findViewById(R.id.btnClearAdd);
        btnAdd = findViewById(R.id.btnAdd);

        myFunctions.setActionDoneButton(etTask);
        myFunctions.setOnClickEtDateListener(this, etSlaDate);
        myFunctions.setOnClickBtnClearListener(btnClear, etTask, etSlaDate);

        setOnClickBtnAddListener();
    }

    private void setOnClickBtnAddListener() {
        btnAdd.setOnClickListener(v -> {
            if (etTask.getText().toString().equals("") || etSlaDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnAdd.setClickable(false);
                TaskModel task = new TaskModel(etTask.getText().toString(), etSlaDate.getText().toString());
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result = myDB.createTask(task);

                if (result == -1)
                    Toast.makeText(this, "Falha ao criar a tarefa.", Toast.LENGTH_SHORT).show();
                else {
                    task.setId(result);
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    setResult(1, taskData);
                    finish();
                }
            }
        });
    }
}