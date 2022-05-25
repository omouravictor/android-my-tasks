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
    CategoryModel category;
    int catAdaptPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.category);
        setContentView(R.layout.activity_update_category);
        init();
    }

    void init() {
        myFunctions = new MyFunctions();
        etCategory = findViewById(R.id.etUpdateCategoryName);
        btnClear = findViewById(R.id.btnClearUpdateCategory);
        btnUpdate = findViewById(R.id.btnUpdateCategory);

        myFunctions.setActionDoneButton(etCategory);
        myFunctions.clearEditTexts(btnClear, etCategory);

        SQLiteHelper myDB = new SQLiteHelper(this);
        Intent intent = new Intent();

        getAndSetIntentData();

        btnUpdate.setOnClickListener(v -> {
            if (!myFunctions.isEmpty(this, etCategory))
                updateCategory(myDB, intent);
        });

    }

    void getAndSetIntentData() {
        Intent intent = getIntent();

        category = intent.getParcelableExtra("category");
        catAdaptPosition = intent.getIntExtra("catAdaptPosition", -1);

        etCategory.setText(category.getName());
    }

    void updateCategory(SQLiteHelper myDB, Intent intent) {
        try {
            btnUpdate.setClickable(false);

            category.setName(etCategory.getText().toString());

            myDB.updateCategory(category);

            intent.putExtra("category", category);
            intent.putExtra("catAdaptPosition", catAdaptPosition);
            setResult(2, intent);
            finish();

        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Essa categoria já existe", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
        } finally {
            btnUpdate.setClickable(true);
        }
    }

}