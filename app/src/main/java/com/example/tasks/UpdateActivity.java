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

public class UpdateActivity extends AppCompatActivity {

    EditText etTask, etDate, etFocus;
    Button btnClear, btnUpdate;
    TaskModel task;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();
        getAndSetIntentData();
        setOnClickEtDateListener();
        setOnClickBtnClearListener();
        setOnClickBtnUpdateListener();
    }

    private void initView() {
        etTask = findViewById(R.id.inputEditTextTask2);
        etDate = findViewById(R.id.inputEditTextDate2);
        etFocus = findViewById(R.id.etFocus2);
        btnClear = findViewById(R.id.btnClear2);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Enables textMultiLine EditText with ActionDone button (without Enter button)
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    private void getAndSetIntentData() {
        position = getIntent().getIntExtra("position", 0);
        task = getIntent().getParcelableExtra("task");

        etTask.setText(task.getName());
        etTask.setSelection(etTask.getText().length());
        etDate.setText(task.getSlaDate());
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

    public void setOnClickBtnUpdateListener() {
        btnUpdate.setOnClickListener((v) -> {
            btnUpdate.setClickable(false);
            if (etTask.getText().toString().equals("") || etDate.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                task.setName(etTask.getText().toString());
                task.setSlaDate(etDate.getText().toString());
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result = myDB.updateTask(task);

                if (result == -1)
                    Toast.makeText(this, "Falha ao atualizar a tarefa.", Toast.LENGTH_SHORT).show();
                else {
                    Intent taskData = new Intent();
                    taskData.putExtra("task", task);
                    taskData.putExtra("position", position);
                    setResult(2, taskData);
                    Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}