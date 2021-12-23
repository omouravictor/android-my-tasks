package com.example.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    Activity activity;
    private final ArrayList<TaskModel> allTasks;

    public TaskAdapter(Activity activity, Context context, ArrayList<TaskModel> items) {
        this.activity = activity;
        this.context = context;
        this.allTasks = items;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(context).inflate(R.layout.my_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);
        holder.tvId.setText(String.valueOf(task.getId()));
        holder.tvTask.setText(task.getName());
        holder.tvSlaDate.setText(task.getSlaDate());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("name", task.getName());
            intent.putExtra("slaDate", task.getSlaDate());
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvTask, tvSlaDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvSlaDate = itemView.findViewById(R.id.tvSlaDate);
        }
    }
}
