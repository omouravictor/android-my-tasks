package com.example.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpdateActivity extends AppCompatActivity {

    DateTimeFormatter dtf;
    MyFunctions myFunctions;
    EditText etTask, etExpirationTime;
    TextInputLayout laySlaDateUpdate;
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
        etExpirationTime = findViewById(R.id.etExpirationTimeUpdate);
        laySlaDateUpdate = findViewById(R.id.laySlaDateUpdate);
        btnClear = findViewById(R.id.btnClearUpdate);
        btnUpdate = findViewById(R.id.btnUpdate);

        myFunctions.setActionDoneButton(etTask);
        myFunctions.setOnClickEtDateListener(this, etExpirationTime);
        myFunctions.setOnClickBtnClearListener(btnClear, etTask, etExpirationTime);

        btnUpdate.setOnClickListener((v) -> {
            if (etTask.getText().toString().equals("") || etExpirationTime.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnUpdate.setClickable(false);

                Intent intent = new Intent();
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result;

                setTaskAttributes(intent);
                result = myDB.updateTask(task);
                startResultAction(result, intent);
            }
        });

        getIntentData();
        setIntentData();
    }


    public void getIntentData() {
        Intent intent = getIntent();

        position = intent.getIntExtra("position", 0);
        task = intent.getParcelableExtra("task");
    }

    public void setIntentData() {
        LocalDate date;

        if (!task.isFinished()) {
            date = LocalDate.parse(task.getExpirationDate());
        } else {
            date = LocalDate.parse(task.getFinishedDate());
            laySlaDateUpdate.setHint("Concluída em");
        }

        etTask.setText(task.getName());
        etTask.setSelection(etTask.getText().length());
        etExpirationTime.setText(date.toString(dtf));
    }

    public void setTaskAttributes(Intent intent) {
        LocalDate date = LocalDate.parse(etExpirationTime.getText().toString(), dtf);

        task.setName(etTask.getText().toString());

        if (!task.isFinished()) {
            setResult(2, intent);
            task.setExpirationDate(date.toString());
        } else {
            setResult(3, intent);
            task.setFinishedDate(date.toString());
        }
    }

    public void startResultAction(long result, Intent intent) {
        if (result == 0) {
            Toast.makeText(this, "Falha ao atualizar a tarefa.", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("task", task);
            intent.putExtra("position", position);
            finish();
        }
    }
}