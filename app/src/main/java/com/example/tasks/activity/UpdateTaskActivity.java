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
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpdateTaskActivity extends AppCompatActivity {

    DateTimeFormatter dtf;
    MyFunctions myFunctions;
    EditText etTittle, etDescription, etExpirationTime;
    TextInputLayout laySlaDateUpdate;
    Button btnClear, btnUpdate;
    TaskModel task;
    int taskAdaptPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        init();
    }

    public void init() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        myFunctions = new MyFunctions();
        etTittle = findViewById(R.id.etTittleUpdate);
        etDescription = findViewById(R.id.etDescriptionUpdate);
        etExpirationTime = findViewById(R.id.etExpirationTimeUpdate);
        laySlaDateUpdate = findViewById(R.id.laySlaDateUpdate);
        btnClear = findViewById(R.id.btnClearUpdate);
        btnUpdate = findViewById(R.id.btnUpdate);

        myFunctions.setActionDoneButton(etTittle);
        myFunctions.setActionDoneButton(etDescription);
        myFunctions.setOnClickEtDateListener(this, etExpirationTime);
        myFunctions.setOnClickTaskBtnClearListener(btnClear, etTittle, etDescription, etExpirationTime);

        SQLiteHelper myDB = new SQLiteHelper(this);
        Intent intent = new Intent();

        getAndSetIntentData();

        btnUpdate.setOnClickListener((v) -> {
            if (!myFunctions.taskRequiredFieldsEmpty(this, etTittle, etExpirationTime))
                updateTask(myDB, intent);
        });

    }


    public void getAndSetIntentData() {
        LocalDate date;
        Intent intent = getIntent();

        task = intent.getParcelableExtra("task");
        taskAdaptPosition = intent.getIntExtra("taskAdaptPosition", -1);

        if (!task.isFinished()) {
            date = LocalDate.parse(task.getExpirationDate());
        } else {
            date = LocalDate.parse(task.getFinishedDate());
            laySlaDateUpdate.setHint("Concluída em");
        }

        etTittle.setText(task.getTittle());
        etTittle.setSelection(etTittle.getText().length());

        etDescription.setText(task.getDescription());

        etExpirationTime.setText(date.toString(dtf));
    }

    public void setTaskAttributes(Intent intent) {
        LocalDate date = LocalDate.parse(etExpirationTime.getText().toString(), dtf);

        task.setTittle(etTittle.getText().toString());
        task.setDescription(etDescription.getText().toString());

        if (!task.isFinished()) {
            setResult(2, intent);
            task.setExpirationDate(date.toString());
        } else {
            setResult(3, intent);
            task.setFinishedDate(date.toString());
        }
    }

    void updateTask(SQLiteHelper myDB, Intent intent) {
        try {
            btnUpdate.setClickable(false);

            setTaskAttributes(intent);

            myDB.updateTask(task);

            intent.putExtra("task", task);
            intent.putExtra("taskAdaptPosition", taskAdaptPosition);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnUpdate.setClickable(true);
        }
    }

}