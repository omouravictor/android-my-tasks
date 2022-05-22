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

    MyFunctions myFunctions;
    EditText etCategory;
    Button btnClear, btnCreate;

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

        SQLiteHelper myDB = new SQLiteHelper(this);
        Intent intent = new Intent();

        btnCreate.setOnClickListener(v -> {
            if (!myFunctions.categoryRequiredFieldsEmpty(this, etCategory))
                createCategory(myDB, intent);
        });
    }

    void createCategory(SQLiteHelper myDB, Intent intent) {
        try {
            btnCreate.setClickable(false);

            CategoryModel category = new CategoryModel(etCategory.getText().toString());
            long resultID = myDB.createCategory(category);

            category.setId(resultID);
            intent.putExtra("category", category);
            setResult(1, intent);
            finish();

        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Essa categoria já existe", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnCreate.setClickable(true);
        }
    }

}