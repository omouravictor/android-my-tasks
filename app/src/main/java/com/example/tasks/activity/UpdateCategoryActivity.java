package com.example.tasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.CategoryModel;

import org.joda.time.LocalDate;

public class UpdateCategoryActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etCategory;
    Button btnClear, btnUpdate;
    CategoryModel category;
    int catAdaptPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);
        init();
    }

    void init() {
        myFunctions = new MyFunctions();
        etCategory = findViewById(R.id.etUpdateCategoryName);
        btnClear = findViewById(R.id.btnClearUpdateCategory);
        btnUpdate = findViewById(R.id.btnUpdateCategory);

        myFunctions.setActionDoneButton(etCategory);
        myFunctions.setOnClickCategoryBtnClearListener(btnClear, etCategory);

        btnUpdate.setOnClickListener(v -> {
            if (etCategory.getText().length() == 0) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnUpdate.setClickable(false);

                Intent intent = new Intent();
                SQLiteHelper myDB = new SQLiteHelper(this);
                long result;

                updateCategory();
                result = myDB.updateCategory(category);
                startResultAction(result, intent);
            }
        });

        getIntentData();
        setIntentData();
    }

    public void updateCategory() {
        category.setName(etCategory.getText().toString());
    }

    public void getIntentData() {
        Intent intent = getIntent();

        category = intent.getParcelableExtra("category");
        catAdaptPosition = intent.getIntExtra("catAdaptPosition", 0);
    }

    public void setIntentData() {
        etCategory.setText(category.getName());
    }

    void startResultAction(long result, Intent intent) {
        if (result == -1) {
            Toast.makeText(this, "Falha ao atualizar a categoria.", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("category", category);
            intent.putExtra("catAdaptPosition", catAdaptPosition);
            setResult(2, intent);
            finish();
        }
    }
}