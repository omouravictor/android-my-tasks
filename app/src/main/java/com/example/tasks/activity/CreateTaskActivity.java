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
    EditText etTittle, etDescription, etExpirationTime;
    Button btnClear, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.task);
        setContentView(R.layout.activity_create_task);
        init();
    }

    void init() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        myFunctions = new MyFunctions();
        etTittle = findViewById(R.id.etTittleAdd);
        etDescription = findViewById(R.id.etDescriptionAdd);
        etExpirationTime = findViewById(R.id.etExpirationTimeAdd);
        btnClear = findViewById(R.id.btnClearAdd);
        btnAdd = findViewById(R.id.btnAdd);

        myFunctions.setActionDoneButton(etTittle);
        myFunctions.setActionDoneButton(etDescription);
        myFunctions.setOnClickEtDateListener(this, etExpirationTime);
        myFunctions.clearEditTexts(btnClear, etTittle, etDescription, etExpirationTime);

        SQLiteHelper myDB = new SQLiteHelper(this);
        Intent intent = new Intent();

        btnAdd.setOnClickListener(v -> {
            if (!myFunctions.isEmpty(this, etTittle, etExpirationTime))
                createTask(myDB, intent);
        });
    }

    void createTask(SQLiteHelper myDB, Intent intent) {
        try {
            btnAdd.setClickable(false);

            LocalDate date = LocalDate.parse(etExpirationTime.getText().toString(), dtf);
            long categoryId = getIntent().getLongExtra("categoryId", -1);
            TaskModel task = new TaskModel(
                    etTittle.getText().toString(),
                    etDescription.getText().toString(),
                    date.toString(),
                    categoryId);
            long resultID = myDB.createTask(task);

            task.setId(resultID);
            intent.putExtra("task", task);
            setResult(1, intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnAdd.setClickable(true);
        }
    }

}