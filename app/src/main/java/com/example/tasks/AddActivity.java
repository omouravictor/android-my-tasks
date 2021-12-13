package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etDate = findViewById(R.id.etDate);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setOnClickListener((v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddActivity.this, (view, year1, month1, day1) -> {
                month1 = month1 + 1;
                String date = day1 + "/" + month1 + "/" + year1;
                etDate.setText(date);
            }, year, month, day);
            datePickerDialog.show();
        });
    }
}