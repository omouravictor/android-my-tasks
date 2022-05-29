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

public class CreateTaskActivity extends AppCompatActivity {

    ArrayList<Integer> requirementsID;
    EditText etTittle, etDescription, etExpirationTime;
    TextView tvQtdRequirements;
    Button btnRequirements, btnClear, btnCreate;
    ActivityResultLauncher<Intent> actResult;
    Intent taskRequirementsIntent;
    MyFunctions myFunctions;
    DateTimeFormatter dtf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle(R.string.task);
        init();
    }

    void init() {
        Integer categoryID = getIntent().getIntExtra("categoryId", -1);

        initView();
        initVariables();
        initActResult();
        initMyFunctions();

        btnRequirements.setOnClickListener(v -> {
            taskRequirementsIntent.putExtra("categoryID", categoryID);
            taskRequirementsIntent.putExtra("requirements", requirementsID);
            actResult.launch(taskRequirementsIntent);
        });

        btnCreate.setOnClickListener(v -> {
            if (!myFunctions.isEmpty(this, etTittle, etExpirationTime)) {
                try {
                    SQLiteHelper myDB = new SQLiteHelper(this);
                    TaskModel newTask = createTask(myDB, categoryID);
                    Integer requirementID = null;
                    if (newTask.hasRequirements())
                        requirementID = createRequirement(myDB, newTask);
                    finishCreate(newTask, requirementID);
                } catch (Exception e) {
                    Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initView() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        etTittle = findViewById(R.id.etTittle);
        etDescription = findViewById(R.id.etDescription);
        etExpirationTime = findViewById(R.id.etExpirationTime);
        tvQtdRequirements = findViewById(R.id.tvQtdRequirements);
        btnRequirements = findViewById(R.id.btnRequirements);
        btnClear = findViewById(R.id.btnClear);
        btnCreate = findViewById(R.id.btnCreate);
    }

    void initVariables() {
        requirementsID = new ArrayList<>();
        taskRequirementsIntent = new Intent(this, RequirementsActivity.class);
    }

    void initActResult() {
        actResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != Activity.RESULT_CANCELED) {
                        requirementsID = result.getData().getIntegerArrayListExtra("requirements");
                        tvQtdRequirements.setText(String.valueOf(requirementsID.size()));
                    }
                }
        );
    }

    void initMyFunctions() {
        myFunctions = new MyFunctions();

        myFunctions.setActionDoneButton(etTittle);
        myFunctions.setActionDoneButton(etDescription);
        myFunctions.setOnClickEtDateListener(this, etExpirationTime);
        myFunctions.clearEditTexts(btnClear, etTittle, etDescription, etExpirationTime);
    }

    void setAttributes(TaskModel task, Integer categoryID) {
        LocalDate date = LocalDate.parse(etExpirationTime.getText().toString(), dtf);

        task.setTittle(etTittle.getText().toString());
        task.setExpirationDate(date.toString());
        task.setDescription(etDescription.getText().toString());
        task.setStatus(0);
        task.setCategoryId(categoryID);
        task.setRequirementsID(requirementsID);
    }

    TaskModel createTask(SQLiteHelper myDB, Integer categoryID) {
        TaskModel task = new TaskModel();
        Integer id;

        setAttributes(task, categoryID);
        id = myDB.createTask(task);
        task.setId(id);

        return task;
    }

    Integer createRequirement(SQLiteHelper myDB, TaskModel newTask) {
        Integer id;

        id = myDB.createRequirement(newTask);

        return id;
    }

    void finishCreate(TaskModel task, Integer requirementID) {
        Intent intent = new Intent();

        intent.putExtra("task", task);
        intent.putExtra("requirementID", requirementID);
        setResult(1, intent);
        finish();
    }

}