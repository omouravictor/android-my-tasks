package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText etTask, etDate, etFocus;
    Button btnClear, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        startVariables();
        setListeners();
    }

    private void startVariables() {
        etTask = findViewById(R.id.inputEditTextTask);
        etDate = findViewById(R.id.inputEditTextDate);
        etFocus = findViewById(R.id.etFocus);
        btnClear = findViewById(R.id.btnClear);
        btnAdd = findViewById(R.id.btnAdd);

        // Enables textMultiLine EditText with ActionDone button (without Enter button)
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    private void setListeners() {
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setOnClickListener((v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddActivity.this, (view, year, month, day) -> {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etDate.setText(date);
            }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
            etFocus.requestFocus();
        });

        btnClear.setOnClickListener((v) -> {
            etTask.setText("");
            etDate.setText("");
            etFocus.requestFocus();
        });

        btnAdd.setOnClickListener((v) -> {
            SQLiteHelper myDB = new SQLiteHelper (AddActivity.this);
            TaskModel task = new TaskModel(etTask.getText().toString(), etDate.getText().toString());
            myDB.addTask(task);
        });
    }
}