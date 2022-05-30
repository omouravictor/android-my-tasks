package com.example.tasks.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
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

    EditText etCategory;
    Button btnClear, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        setTitle(R.string.category);
        init();
    }

    void init() {
        initView();
        initMyFunctions();

        btnCreate.setOnClickListener(v -> {
            if (!MyFunctions.isEmpty(this, etCategory))
                createCategory();
        });
    }

    void initView() {
        etCategory = findViewById(R.id.etCategoryName);
        btnClear = findViewById(R.id.btnClearCategory);
        btnCreate = findViewById(R.id.btnCreateCategory);
    }

    void initMyFunctions() {
        MyFunctions.setActionDoneButton(etCategory);
        MyFunctions.clearEditTexts(btnClear, etCategory);
    }

    void setAttributes(CategoryModel category) {
        category.setName(etCategory.getText().toString());
    }

    CategoryModel getNewCategory() {
        SQLiteHelper myDB = new SQLiteHelper(this);
        CategoryModel category = new CategoryModel();
        Integer id;

        setAttributes(category);
        id = myDB.createCategory(category);
        category.setId(id);

        return category;
    }

    void finishCreate(CategoryModel category) {
        Intent intent = new Intent();

        intent.putExtra("category", category);
        setResult(1, intent);
        finish();
    }

    void createCategory() {
        btnCreate.setClickable(false);

        try {
            CategoryModel newCategory = getNewCategory();
            finishCreate(newCategory);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Essa categoria já existe", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnCreate.setClickable(true);
        }
    }

}