package com.example.tasks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MyFunctions {

    EditText etFocus;

    public MyFunctions() {
    }

    public MyFunctions(EditText etFocus) {
        this.etFocus = etFocus;
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setOnClickEtDateListener(Context context, EditText etDate) {
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context, (view, year, month, day) -> {
                month = month + 1;
                etDate.setText(day + "/" + month + "/" + year);
            }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
            hideKeyboard(context, v);
            etFocus.requestFocus();
        });
    }

    public void setOnClickBtnClearListener(Button btnClear, EditText etTask, EditText etDate) {
        btnClear.setOnClickListener(v -> {
            etTask.setText("");
            etDate.setText("");
            etFocus.requestFocus();
        });
    }

    public void setActionDoneButton(EditText etTask) {
        // Enables textMultiLine EditText with ActionDone button (without Enter button)
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    public void sortTaskArrayBySlaDate(ArrayList<TaskModel> tasks, DateTimeFormatter dtf, LocalDate currentDate) {
        Collections.sort(tasks, (task1, task2) -> {
            int days1 = Days.daysBetween(currentDate, LocalDate.parse(task1.getSlaDate(), dtf)).getDays();
            int days2 = Days.daysBetween(currentDate, LocalDate.parse(task2.getSlaDate(), dtf)).getDays();
            return Integer.compare(days1, days2);
        });
    }

}
