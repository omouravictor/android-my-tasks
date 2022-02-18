package com.example.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpdateActivity extends AppCompatActivity {

    DateTimeFormatter dtf;
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
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
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

        LocalDate expirationDate = LocalDate.parse(task.getExpirationDate());
        etSlaDate.setText(expirationDate.toString(dtf));
    }

    private void setOnClickBtnUpdateListener() {
        btnUpdate.setOnClickListener((v) -> {
            if (etTask.getText().toString().equals("") || etSlaDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnUpdate.setClickable(false);

                SQLiteHelper myDB = new SQLiteHelper(this);
                LocalDate expirationDate = LocalDate.parse(etSlaDate.getText().toString(), dtf);

                task.setName(etTask.getText().toString());
                task.setExpirationDate(expirationDate.toString());

                long result = myDB.updateTask(task);

                if (result == 0) {
                    Toast.makeText(this, "Falha ao atualizar a tarefa.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    taskData.putExtra("position", position);
                    setResult(2, taskData);
                    finish();
                }
            }
        });
    }
}