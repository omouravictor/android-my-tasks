package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etTask, etSlaDate;
    Button btnClear, btnUpdate;
    TaskModel task;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        init();
    }

    private void init() {
        myFunctions = new MyFunctions();
        etTask = findViewById(R.id.etTaskUpdate);
        etSlaDate = findViewById(R.id.etSlaDateUpdate);
        btnClear = findViewById(R.id.btnClearUpdate);
        btnUpdate = findViewById(R.id.btnUpdate);

        myFunctions.setActionDoneButton(etTask);
        myFunctions.setOnClickEtDateListener(this, etSlaDate);
        myFunctions.setOnClickBtnClearListener(btnClear, etTask, etSlaDate);

        getAndSetIntentData();
        setOnClickBtnUpdateListener();
    }

    private void getAndSetIntentData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        task = intent.getParcelableExtra("task");

        etTask.setText(task.getName());
        etTask.setSelection(etTask.getText().length());
        etSlaDate.setText(task.getSlaDate());
    }

    private void setOnClickBtnUpdateListener() {
        btnUpdate.setOnClickListener((v) -> {
            if (etTask.getText().toString().equals("") || etSlaDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnUpdate.setClickable(false);
                task.setName(etTask.getText().toString());
                task.setSlaDate(etSlaDate.getText().toString());

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