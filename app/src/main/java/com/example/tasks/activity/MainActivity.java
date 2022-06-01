package com.example.tasks.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
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
        setTitle(R.string.categories);
        init();
    }

    void init() {
        initView();
        initVariables();
        startBtnAdd();
        startActivityResult();
        startAdapterAndRecyclerView();
    }

    void initView() {
        btnAdd = findViewById(R.id.btnAdd);
        rvCategory = findViewById(R.id.rvCategory);
    }

    void initVariables() {
        myDB = new SQLiteHelper(this);
        createCategoryActIntent = new Intent(this, CreateCategoryActivity.class);
    }

    void startBtnAdd() {
        btnAdd.setOnClickListener(v -> actResult.launch(createCategoryActIntent));
    }

    void startAdapterAndRecyclerView() {
        adaptCategory = new CategoryAdapter(this, myDB, actResult);

        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(adaptCategory);
    }

    void startActivityResult() {
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuBuilder m = (MenuBuilder) menu;

        m.setOptionalIconsVisible(true);
        getMenuInflater().inflate(R.menu.my_category_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sortByName)
            adaptCategory.sortCategoryByName();
        else if (id == R.id.deleteAll)
            startDeleteAllBuilder();

        return super.onOptionsItemSelected(item);
    }

    void startDeleteAllBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Deseja excluir todas as categorias e suas tarefas?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            try {
                myDB.deleteAllCategories();
                adaptCategory.deleteAll();
            } catch (Exception e) {
                Toast.makeText(this, "Houve um erro", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}