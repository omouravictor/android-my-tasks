package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final AlertDialog.Builder builder;
    private final Intent updateActivityIntent;
    private final ActivityResultLauncher<Intent> activityResult;
    private final SQLiteHelper myDB;
    private final ArrayList<TaskModel> allTasks;
    private final DateTimeFormatter dtf;
    private final LocalDate currentDate;

    public TaskAdapter(
            Context context,
            ActivityResultLauncher<Intent> activityResult,
            SQLiteHelper myDB,
            ArrayList<TaskModel> items,
            DateTimeFormatter dtf,
            LocalDate currentDate
    ) {
        this.myDB = myDB;
        this.activityResult = activityResult;
        this.context = context;
        this.allTasks = items;
        this.dtf = dtf;
        this.currentDate = currentDate;
        this.updateActivityIntent = new Intent(context, UpdateActivity.class);
        this.builder = new AlertDialog.Builder(context);
        sortTaskArrayBySlaDate();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);
        int days = Days.daysBetween(currentDate, LocalDate.parse(task.getSlaDate(), dtf)).getDays();

        holder.tvTaskName.setText(task.getName());

        setExpirationTime(holder.myRow, holder.tvExpirationTime, days);

        holder.itemView.setOnClickListener(v -> {
            updateActivityIntent.putExtra("task", task);
            updateActivityIntent.putExtra("position", position);
            activityResult.launch(updateActivityIntent);
        });

        builder.setMessage("Confirmar conclusão?");
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        holder.btnComplete.setOnClickListener(v -> {
            builder.setTitle(task.getName());
            builder.setPositiveButton("Sim", (dialog, which) -> {
                if (myDB.deleteTask(task) != 0)
                    deleteTask(position);
                else
                    Toast.makeText(context, "Falha ao deletar a tarefa.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.show();
        });
    }

    public void setExpirationTime(LinearLayout myRow, TextView tvExpirationTime, int days) {
        if (days > 0) {
            myRow.setBackgroundColor(context.getColor(R.color.white));
            tvExpirationTime.setText(context.getString(R.string.expirationDaysText, days));
        } else if (days == 0) {
            myRow.setBackgroundColor(context.getColor(R.color.lightYellow));
            tvExpirationTime.setText(R.string.expirationTodayText);
        } else {
            myRow.setBackgroundColor(context.getColor(R.color.lightRed));
            tvExpirationTime.setText(R.string.expiredText);
        }
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskName, tvExpirationTime;
        Button btnComplete;
        LinearLayout myRow;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvExpirationTime = itemView.findViewById(R.id.expirationTime);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            myRow = itemView.findViewById(R.id.myRow);
        }
    }

    public void sortTaskArrayBySlaDate() {
        Collections.sort(allTasks, (task1, task2) -> {
            int days1 = Days.daysBetween(currentDate, LocalDate.parse(task1.getSlaDate(), dtf)).getDays();
            int days2 = Days.daysBetween(currentDate, LocalDate.parse(task2.getSlaDate(), dtf)).getDays();
            return Integer.compare(days1, days2);
        });
        notifyItemRangeChanged(0, getItemCount());
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
    }

    public void updateTask(int position, TaskModel updatedTask) {
        allTasks.set(position, updatedTask);
        notifyItemChanged(position);
    }

    public void deleteTask(int position) {
        allTasks.remove(position);
        notifyDataSetChanged();
    }

    public void deleteAllTasks() {
        allTasks.clear();
        notifyDataSetChanged();
    }

}
