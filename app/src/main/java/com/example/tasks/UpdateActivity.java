package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etTask, etDate, etFocus;
    Button btnClear, btnUpdate;
    TaskModel task;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        init();
        getAndSetIntentData();
        setOnClickBtnUpdateListener();
        myFunctions.setOnClickEtDateListener(this, etDate);
        myFunctions.setOnClickBtnClearListener(btnClear, etTask, etDate);
    }

    private void init() {
        etTask = findViewById(R.id.inputEditTextTask2);
        etDate = findViewById(R.id.inputEditTextDate2);
        etFocus = findViewById(R.id.etFocus2);
        myFunctions = new MyFunctions(etFocus);
        btnClear = findViewById(R.id.btnClear2);
        btnUpdate = findViewById(R.id.btnUpdate);
        myFunctions.setActionDoneButton(etTask);
    }

    private void getAndSetIntentData() {
        position = getIntent().getIntExtra("position", 0);
        task = getIntent().getParcelableExtra("task");
        etTask.setText(task.getName());
        etTask.setSelection(etTask.getText().length());
        etDate.setText(task.getSlaDate());
    }

    private void setOnClickBtnUpdateListener() {
        btnUpdate.setOnClickListener((v) -> {
            btnUpdate.setClickable(false);
            if (etTask.getText().toString().equals("") || etDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                task.setName(etTask.getText().toString());
                task.setSlaDate(etDate.getText().toString());
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result = myDB.updateTask(task);

                if (result == -1)
                    Toast.makeText(this, "Falha ao atualizar a tarefa.", Toast.LENGTH_SHORT).show();
                else {
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    taskData.putExtra("position", position);
                    setResult(2, taskData);
                    Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}