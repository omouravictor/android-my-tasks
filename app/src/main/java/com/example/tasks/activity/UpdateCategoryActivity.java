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

public class UpdateCategoryActivity extends AppCompatActivity {

    MyFunctions myFunctions;
    EditText etCategory;
    Button btnClear, btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.category);
        setContentView(R.layout.activity_update_category);
        init();
    }

    void init() {
        Intent intent = getIntent();
        CategoryModel category = intent.getParcelableExtra("category");
        int catAdaptPosition = intent.getIntExtra("catAdaptPosition", -1);

        initView();
        setMyFunctions();
        setCategoryData(category);

        btnUpdate.setOnClickListener(v -> {
            if (!myFunctions.isEmpty(this, etCategory))
                updateCategory(category, catAdaptPosition);
        });

    }

    void initView() {
        etCategory = findViewById(R.id.etUpdateCategoryName);
        btnClear = findViewById(R.id.btnClearUpdateCategory);
        btnUpdate = findViewById(R.id.btnUpdateCategory);
    }

    void setMyFunctions() {
        myFunctions = new MyFunctions();

        myFunctions.setActionDoneButton(etCategory);
        myFunctions.clearEditTexts(btnClear, etCategory);
    }

    void setCategoryData(CategoryModel category) {
        etCategory.setText(category.getName());
    }

    void setAttributes(CategoryModel category) {
        category.setName(etCategory.getText().toString());
    }

    CategoryModel getUpdatedCategory(CategoryModel category) {
        SQLiteHelper myDB = new SQLiteHelper(this);

        setAttributes(category);
        myDB.updateCategory(category);

        return category;
    }

    void finishUpdate(CategoryModel category, int catAdaptPosition) {
        Intent intent = new Intent();

        intent.putExtra("category", category);
        intent.putExtra("catAdaptPosition", catAdaptPosition);
        setResult(2, intent);
        finish();
    }

    void updateCategory(CategoryModel category, int catAdaptPosition) {
        btnUpdate.setClickable(false);

        try {
            CategoryModel updatedCategory = getUpdatedCategory(category);
            finishUpdate(updatedCategory, catAdaptPosition);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Essa categoria já existe", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnUpdate.setClickable(true);
        }
    }

}