package com.example.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.adapter.CategoryAdapter;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.CategoryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> actResult;
    RecyclerView rvCategory;
    Intent createCategoryActIntent;
    CategoryAdapter adaptCategory;
    FloatingActionButton btnAdd;
    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        myDB = new SQLiteHelper(this);
        startBtnAdd();
        startActivityResult();
        startAdapterAndRecyclerView();
    }

    public void startBtnAdd() {
        btnAdd = findViewById(R.id.btnAdd);
        createCategoryActIntent = new Intent(this, CreateCategoryActivity.class);
        btnAdd.setOnClickListener(v -> actResult.launch(createCategoryActIntent));
    }

    public void startAdapterAndRecyclerView() {
        adaptCategory = new CategoryAdapter(this, myDB, actResult);

        rvCategory = findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(adaptCategory);
    }

    public void startActivityResult() {
        actResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode != Activity.RESULT_CANCELED) {
                        CategoryModel category = result.getData().getParcelableExtra("category");
                        if (resultCode == 1) {
                            adaptCategory.addCategory(category);
                        } else if (resultCode == 2) {
                            int catAdaptPosition = result.getData().getIntExtra("catAdaptPosition", 0);
                            adaptCategory.updateCategory(catAdaptPosition, category);
                        } else if (resultCode == 3) {
                            int position = result.getData().getIntExtra("catAdaptPosition", -1);
                            adaptCategory.refreshCategory(position);
                        }
                    }
                }
        );
    }

}