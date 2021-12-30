package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText etTask, etDate, etFocus;
    Button btnClear, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        setOnClickEtDateListener();
        setOnClickBtnClearListener();
        setOnClickBtnAddListener();
    }

    private void initView() {
        etTask = findViewById(R.id.inputEditTextTask);
        etDate = findViewById(R.id.inputEditTextDate);
        etFocus = findViewById(R.id.etFocus);
        btnClear = findViewById(R.id.btnClear);
        btnAdd = findViewById(R.id.btnAdd);

        // Enables textMultiLine EditText with ActionDone button (without Enter button)
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    public void setOnClickEtDateListener() {
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, (view, year, month, day) -> {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etDate.setText(date);
            }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etTask.getWindowToken(), 0);
            etFocus.requestFocus();
        });
    }

    public void setOnClickBtnClearListener() {
        btnClear.setOnClickListener(v -> {
            etTask.setText("");
            etDate.setText("");
            etFocus.requestFocus();
        });
    }

    private void setOnClickBtnAddListener() {
        btnAdd.setOnClickListener(v -> {
            btnAdd.setClickable(false);
            if (etTask.getText().toString().equals("") || etDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                TaskModel task = new TaskModel(etTask.getText().toString(), etDate.getText().toString());
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result = myDB.createTask(task);

                if (result == -1)
                    Toast.makeText(this, "Falha ao criar a tarefa.", Toast.LENGTH_SHORT).show();
                else {
                    task.setId(result);
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    setResult(1, taskData);
                    Toast.makeText(this, "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}