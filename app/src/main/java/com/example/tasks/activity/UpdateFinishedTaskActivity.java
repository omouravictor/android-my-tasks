package com.example.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.List;

public class UpdateFinishedTaskActivity extends AppCompatActivity {

    List<Integer> requiredIDs;
    EditText etTittle, etDescription, etExpirationDate, etFinishedDate;
    TextView tvQtdRequirements;
    Button btnRequirements;
    LinearLayout layUpdate;
    ActivityResultLauncher<Intent> actResult;
    Intent taskRequirementsIntent;
    DateTimeFormatter dtf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_finished_task);
        setTitle(getIntent().getStringExtra("categoryName"));
        init();
    }

    void init() {
        Intent intent = getIntent();
        TaskModel task = intent.getParcelableExtra("task");
        int taskAdaptPosition = intent.getIntExtra("taskAdaptPosition", -1);
        SQLiteHelper myDB = new SQLiteHelper(this);

        initView();
        initRequiredIDs(myDB, task);
        initVariables();
        initActResult();
        initMyFunctions();
        setTaskData(task);

        btnRequirements.setOnClickListener(v -> {
            task.setRequiredIDs(requiredIDs);
            taskRequirementsIntent.putExtra("taskTittle", task.getTittle());
            taskRequirementsIntent.putExtra("task", task);
            actResult.launch(taskRequirementsIntent);
        });

        layUpdate.setOnClickListener((v) -> {
            layUpdate.setClickable(false);
            if (!MyFunctions.isEmpty(this, etTittle, etExpirationDate, etFinishedDate)) {
                try {
                    TaskModel updatedTask = updateTask(myDB, task);
                    if (updatedTask.hasRequirements()) {
                        myDB.deleteRequirementsOfTask(updatedTask);
                        myDB.createRequirement(updatedTask);
                    } else {
                        myDB.deleteRequirementsOfTask(updatedTask);
                    }
                    finishUpdate(updatedTask, taskAdaptPosition);
                } catch (Exception e) {
                    Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
                }
            }
            layUpdate.setClickable(true);
        });

    }

    void initView() {
        dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        etTittle = findViewById(R.id.etTittle);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etFinishedDate = findViewById(R.id.etFinishedDate);
        etDescription = findViewById(R.id.etDescription);
        tvQtdRequirements = findViewById(R.id.tvQtdRequirements);
        btnRequirements = findViewById(R.id.btnRequirements);
        layUpdate = findViewById(R.id.layUpdate);
    }

    void initRequiredIDs(SQLiteHelper myDB, TaskModel task) {
        requiredIDs = myDB.getAllRequiredIDs(task.getId());
    }

    void initVariables() {
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
        MyFunctions.setOnClickEtDateListener(this, etFinishedDate);
    }

    void setTaskData(TaskModel task) {
        LocalDate expirationDate = LocalDate.parse(task.getExpirationDate());
        LocalDate finishedDate = LocalDate.parse(task.getFinishedDate());

        etTittle.setText(task.getTittle());
        etExpirationDate.setText(expirationDate.toString(dtf));
        etFinishedDate.setText(finishedDate.toString(dtf));
        etDescription.setText(task.getDescription());
        tvQtdRequirements.setText(String.valueOf(requiredIDs.size()));
    }

    void setAttributes(TaskModel task) {
        LocalDate expirationDate = LocalDate.parse(etExpirationDate.getText().toString(), dtf);
        LocalDate finishedDate = LocalDate.parse(etFinishedDate.getText().toString(), dtf);

        task.setTittle(etTittle.getText().toString());
        task.setExpirationDate(expirationDate.toString());
        task.setFinishedDate(finishedDate.toString());
        task.setDescription(etDescription.getText().toString());
        task.setRequiredIDs(requiredIDs);
    }

    TaskModel updateTask(SQLiteHelper myDB, TaskModel task) {

        setAttributes(task);
        myDB.updateTask(task);

        return task;
    }

    void finishUpdate(TaskModel task, int taskAdaptPosition) {
        Intent intent = new Intent();

        intent.putExtra("task", task);
        intent.putExtra("taskAdaptPosition", taskAdaptPosition);
        setResult(2, intent);
        finish();
    }
}