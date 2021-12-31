package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    MyActivityFunctions myFunctions;
    EditText etTask, etDate, etFocus;
    Button btnClear, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        setOnClickBtnAddListener();
        myFunctions.setOnClickEtDateListener(this, etDate);
        myFunctions.setOnClickBtnClearListener(btnClear, etTask, etDate);
    }

    private void init() {
        etTask = findViewById(R.id.inputEditTextTask);
        etDate = findViewById(R.id.inputEditTextDate);
        etFocus = findViewById(R.id.etFocus);
        myFunctions = new MyActivityFunctions(etFocus);
        btnClear = findViewById(R.id.btnClear);
        btnAdd = findViewById(R.id.btnAdd);
        myFunctions.setActionDoneButton(etTask);
    }

    private void setOnClickBtnAddListener() {
        btnAdd.setOnClickListener(v -> {
            btnAdd.setClickable(false);
            if (etTask.getText().toString().equals("") || etDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                TaskModel task = new TaskModel(etTask.getText().toString(), etDate.getText().toString());
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result = myDB.createTask(task);

                if (result == -1)
                    Toast.makeText(this, "Falha ao criar a tarefa.", Toast.LENGTH_SHORT).show();
                else {
                    task.setId(result);
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    setResult(1, taskData);
                    Toast.makeText(this, "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}