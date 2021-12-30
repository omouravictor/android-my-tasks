package com.example.tasks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> mainActivityResult;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    SQLiteHelper myDB;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setBtnAddOnClickListener();
        setAdapterOnClickListenerS();
    }

    private void initView() {
        context = this;
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        myDB = new SQLiteHelper(this);
        adapter = new TaskAdapter(myDB.getAllTasks());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        startMainActivityResult();
    }

    public void startMainActivityResult() {
        mainActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_CANCELED) {
                        TaskModel taskIntentData = result.getData().getParcelableExtra("task");
                        if (result.getResultCode() == 1) {
                            adapter.addTask(taskIntentData);
                        } else if (result.getResultCode() == 2) {
                            int updatedPosition = result.getData().getIntExtra("position", 0);
                            adapter.updateTask(updatedPosition, taskIntentData);
                        }
                    }
                }
        );
    }

    public void setAdapterOnClickListenerS() {
        adapter.setOnClickListenerInterface(new AdapterInterface() {
            @Override
            public View.OnClickListener getOnClickListener(int position) {
                return v -> {
                    Intent intent = new Intent(context, UpdateActivity.class);
                    TaskModel task = adapter.getTask(position);
                    intent.putExtra("task", task);
                    intent.putExtra("position", position);
                    mainActivityResult.launch(intent);
                };
            }

            @Override
            public View.OnClickListener getBtnCompleteOnClickListener(int position) {
                return v -> {
                    TaskModel task = adapter.getTask(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(task.getName());
                    builder.setMessage("Confirmar conclusão?");

                    builder.setPositiveButton("Sim", (dialog, which) -> {
                        long result = myDB.deleteTask(task);
                        if (result != 0)
                            adapter.deleteTask(position);
                        else
                            Toast.makeText(context, "Falha ao deletar a tarefa.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                    builder.setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    builder.show();
                };
            }
        });
    }

    public void setBtnAddOnClickListener() {
        btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddActivity.class);
            mainActivityResult.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAll) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Tem certeza que deseja excluir todas as tarefas?");

            builder.setPositiveButton("Sim", (dialog, which) -> {
                myDB.deleteAllTasks();
                adapter.deleteAllTasks();
                dialog.dismiss();
            });
            builder.setNegativeButton("Não", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}