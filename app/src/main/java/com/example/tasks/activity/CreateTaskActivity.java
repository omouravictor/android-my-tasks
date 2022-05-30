package com.example.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity {

    List<Integer> requiredIDs;
    EditText etTittle, etDescription, etExpirationDate;
    TextView tvQtdRequirements;
    Button btnRequirements, btnClear, btnCreate;
    ActivityResultLauncher<Intent> actResult;
    Intent taskRequirementsIntent;
    DateTimeFormatter dtf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle(R.string.task);
        init();
    }

    void init() {
        Intent intent = getIntent();
        Integer categoryID = intent.getIntExtra("categoryId", -1);

        TaskModel newTask = new TaskModel(categoryID);

        initView();
        initVariables();
        initActResult();
        initMyFunctions();

        btnRequirements.setOnClickListener(v -> {
            newTask.setRequiredIDs(requiredIDs);
            taskRequirementsIntent.putExtra("task", newTask);
            actResult.launch(taskRequirementsIntent);
        });

        btnCreate.setOnClickListener(v -> {
            btnCreate.setClickable(false);
            if (!MyFunctions.isEmpty(this, etTittle, etExpirationDate)) {
                try {
                    SQLiteHelper myDB = new SQLiteHelper(this);
                    fillNewTask(myDB, newTask);
                    if (newTask.hasRequirements())
                        myDB.createRequirement(newTask);
                    finishCreate(newTask);
                } catch (Exception e) {
                    Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
                }
            }
            btnCreate.setClickable(true);
        });
    }

    void initView() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        etTittle = findViewById(R.id.etTittle);
        etDescription = findViewById(R.id.etDescription);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        tvQtdRequirements = findViewById(R.id.tvQtdRequirements);
        btnRequirements = findViewById(R.id.btnRequirements);
        btnClear = findViewById(R.id.btnClear);
        btnCreate = findViewById(R.id.btnCreate);
    }

    void initVariables() {
        requiredIDs = new ArrayList<>();
        taskRequirementsIntent = new Intent(this, RequirementsActivity.class);
    }

    void initActResult() {
        actResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != Activity.RESULT_CANCELED) {
                        requiredIDs = result.getData().getIntegerArrayListExtra("requirements");
                        tvQtdRequirements.setText(String.valueOf(requiredIDs.size()));
                    }
                }
        );
    }

    void initMyFunctions() {
        MyFunctions.setActionDoneButton(etTittle);
        MyFunctions.setActionDoneButton(etDescription);
        MyFunctions.setOnClickEtDateListener(this, etExpirationDate);
        MyFunctions.clearEditTexts(btnClear, etTittle, etDescription, etExpirationDate);
    }

    void setAttributes(TaskModel task) {
        LocalDate date = LocalDate.parse(etExpirationDate.getText().toString(), dtf);

        task.setTittle(etTittle.getText().toString());
        task.setExpirationDate(date.toString());
        task.setDescription(etDescription.getText().toString());
        task.setStatus(0);
        task.setRequiredIDs(requiredIDs);
    }

    void fillNewTask(SQLiteHelper myDB, TaskModel newTask) {
        Integer id;

        setAttributes(newTask);
        id = myDB.createTask(newTask);
        newTask.setId(id);
    }

    void finishCreate(TaskModel task) {
        Intent intent = new Intent();

        intent.putExtra("task", task);
        setResult(1, intent);
        finish();
    }

}