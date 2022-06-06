package com.example.tasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.adapter.RequirementsAdapter;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class RequirementsActivity extends AppCompatActivity {

    RequirementsAdapter requirementsAdapter;
    RecyclerView rvTasksOnHold;
    LinearLayout laySave;
    TaskModel task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirements);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = getIntent().getParcelableExtra("task");

        setTitle(task.getTittle());
        init();
    }

    void init() {
        initView();
        initAdapterAndRecyclerView();

        laySave.setOnClickListener(v -> {
            Intent intent = new Intent();
            List<Integer> requirementsID = requirementsAdapter.getRequirements();
            intent.putExtra("requirements", (ArrayList<Integer>) requirementsID);
            setResult(1, intent);
            finish();
        });
    }

    void initView() {
        rvTasksOnHold = findViewById(R.id.rvTasksOnHold);
        laySave = findViewById(R.id.laySave);
    }

    void initAdapterAndRecyclerView() {
        SQLiteHelper myDB = new SQLiteHelper(this);

        requirementsAdapter = new RequirementsAdapter(myDB, task);

        rvTasksOnHold.setLayoutManager(new LinearLayoutManager(this));
        rvTasksOnHold.setAdapter(requirementsAdapter);
    }

    void setSearchView(MenuItem menuItemSearch) {
        SearchView searchView = (SearchView) menuItemSearch.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Digite aqui para pesquisar");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MyFunctions.hideKeyboard(getApplicationContext(), getCurrentFocus());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                requirementsAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemSearch;

        getMenuInflater().inflate(R.menu.my_requirement_menu, menu);
        menuItemSearch = menu.findItem(R.id.searchRequirement);

        setSearchView(menuItemSearch);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();

        return true;
    }
}