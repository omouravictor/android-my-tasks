package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText etTask, etDate;
    Button clearBtn;

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
        clearBtn = findViewById(R.id.clearBtn);

        etDate.setOnClickListener((v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddActivity.this, (view, year, month, day) -> {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etDate.setText(date);
            }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
        });

        clearBtn.setOnClickListener((v) -> {
            Toast.makeText(getApplicationContext(), "Exemplo Toast", Toast.LENGTH_SHORT).show();
//            etTask.setText("");
//            etDate.setText("");
        });
    }
}