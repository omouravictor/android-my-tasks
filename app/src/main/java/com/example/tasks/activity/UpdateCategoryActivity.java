package com.example.tasks.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.CategoryModel;

public class UpdateCategoryActivity extends AppCompatActivity {

    EditText etCategory;
    LinearLayout layUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);
        setTitle(R.string.category);
        init();
    }

    void init() {
        Intent intent = getIntent();
        CategoryModel category = intent.getParcelableExtra("category");
        int catAdaptPosition = intent.getIntExtra("catAdaptPosition", -1);

        initView();
        initMyFunctions();
        setCategoryData(category);

        layUpdate.setOnClickListener(v -> {
            if (MyFunctions.isNotEmpty(this, etCategory))
                updateCategory(category, catAdaptPosition);
        });

    }

    void initView() {
        etCategory = findViewById(R.id.etCategoryName);
        layUpdate = findViewById(R.id.layUpdate);
    }

    void initMyFunctions() {
        MyFunctions.setActionDoneButton(etCategory);
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
        layUpdate.setClickable(false);

        try {
            CategoryModel updatedCategory = getUpdatedCategory(category);
            finishUpdate(updatedCategory, catAdaptPosition);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Essa categoria já existe", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            layUpdate.setClickable(true);
        }
    }

}