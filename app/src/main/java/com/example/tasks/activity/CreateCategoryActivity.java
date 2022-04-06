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

public class CreateCategoryActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etCategory;
    Button btnClear, btnCreate;
    CategoryModel category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        init();
    }

    void init() {
        myFunctions = new MyFunctions();
        etCategory = findViewById(R.id.etCategoryName);
        btnClear = findViewById(R.id.btnClearCategory);
        btnCreate = findViewById(R.id.btnCreateCategory);

        myFunctions.setActionDoneButton(etCategory);
        myFunctions.setOnClickCategoryBtnClearListener(btnClear, etCategory);

        btnCreate.setOnClickListener(v -> {
            if (etCategory.getText().toString().equals("")) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                btnCreate.setClickable(false);

                Intent intent = new Intent();
                SQLiteHelper myDB = new SQLiteHelper(this);
                category = new CategoryModel(etCategory.getText().toString());
                long result = myDB.createCategory(category);

                startResultAction(result, intent);
            }
        });
    }

    void startResultAction(long result, Intent intent) {
        if (result == -1) {
            Toast.makeText(this, "Falha ao criar a categoria.", Toast.LENGTH_SHORT).show();
        } else {
            category.setId(result);
            intent.putExtra("category", category);
            setResult(1, intent);
            finish();
        }
    }
}