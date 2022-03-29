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

public class CreateTaskActivity extends AppCompatActivity {

    DateTimeFormatter dtf;
    MyFunctions myFunctions;
    EditText etTask, etExpirationTime;
    Button btnClear, btnAdd;
    TaskModel task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        init();
    }

    void init() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        myFunctions = new MyFunctions();
        etTask = findViewById(R.id.etTaskAdd);
        etExpirationTime = findViewById(R.id.etExpirationTime);
        btnClear = findViewById(R.id.btnClearAdd);
        btnAdd = findViewById(R.id.btnAdd);

        myFunctions.setActionDoneButton(etTask);
        myFunctions.setOnClickEtDateListener(this, etExpirationTime);
        myFunctions.setOnClickTaskBtnClearListener(btnClear, etTask, etExpirationTime);

        btnAdd.setOnClickListener(v -> {
            if (etTask.getText().toString().equals("") || etExpirationTime.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnAdd.setClickable(false);

                Intent intent = new Intent();
                SQLiteHelper myDB = new SQLiteHelper(this);
                LocalDate date = LocalDate.parse(etExpirationTime.getText().toString(), dtf);
                task = new TaskModel(etTask.getText().toString(), date.toString());
                long result = myDB.createTask(task);

                startResultAction(result, intent);
            }
        });
    }

    void startResultAction(long result, Intent intent) {
        if (result == -1) {
            Toast.makeText(this, "Falha ao criar a tarefa.", Toast.LENGTH_SHORT).show();
        } else {
            task.setId(result);
            intent.putExtra("task", task);
            setResult(1, intent);
            finish();
        }
    }
}