package com.example.tasks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.joda.time.LocalDate;

public class MyFunctions {

    public MyFunctions() {
    }

    public void hideKeyboard(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDateText(int day, int month, int year) {
        String dateText = "";

        if (day < 10) dateText += "0" + day + "/";
        else dateText += day + "/";

        if (month < 10) dateText += "0" + month + "/" + year;
        else dateText += month + "/" + year;

        return dateText;
    }

    public void setOnClickEtDateListener(Context context, @NonNull EditText etDate) {
        LocalDate currentDate = LocalDate.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, day) -> {
                    month += 1;
                    etDate.setText(getDateText(day, month, year));
                },
                currentDate.getYear(),
                currentDate.getMonthOfYear() - 1,
                currentDate.getDayOfMonth()
        );

        etDate.setOnClickListener(v -> {
            datePickerDialog.show();
            hideKeyboard(context, v);
        });
    }

    public void setOnClickTaskBtnClearListener(@NonNull Button btnClear, EditText etTask, EditText etDate) {
        btnClear.setOnClickListener(v -> {
            etTask.setText("");
            etDate.setText("");
        });
    }

    public void setOnClickCategoryBtnClearListener(@NonNull Button btnClear, EditText etCategory) {
        btnClear.setOnClickListener(v -> {
            etCategory.setText("");
        });
    }

    public void setActionDoneButton(@NonNull EditText etTask) {
        // Enables textMultiLine EditText with ActionDone button (without Enter button)
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTask.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

}
