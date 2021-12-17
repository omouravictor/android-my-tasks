package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText etTask, etDate, etFocus;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        etTask = findViewById(R.id.inputEditTextTask);
        etDate = findViewById(R.id.inputEditTextDate);
        etFocus = findViewById(R.id.etFocus);
        btnClear = findViewById(R.id.btnClear);

        etDate.setOnClickListener((v) -> {
            etFocus.requestFocus();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddActivity.this, (view, year, month, day) -> {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etDate.setText(date);
            }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
        });

        btnClear.setOnClickListener((v) -> {
            etTask.setText("");
            etDate.setText("");
            etFocus.requestFocus();
        });
    }
}