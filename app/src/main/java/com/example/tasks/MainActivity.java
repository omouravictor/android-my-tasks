package com.example.tasks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> mainActivityResult;
    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    SQLiteHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setBtnAddOnClickListener();
        setAdapterOnClickListenerS();
    }

    private void initView() {
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
                    Intent intent = new Intent(v.getContext(), UpdateActivity.class);
                    TaskModel task = adapter.getTask(position);
                    intent.putExtra("task", task);
                    intent.putExtra("position", position);
                    mainActivityResult.launch(intent);
                };
            }

            @Override
            public View.OnClickListener getBtnCompleteOnClickListener(int position) {
                return v -> {
                    Context context = v.getContext();
                    TaskModel task = adapter.getTask(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            Intent intent = new Intent(this, AddActivity.class);
            mainActivityResult.launch(intent);
        });
    }

}