package com.example.tasks;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Activity mainActivity;
    private final ArrayList<TaskModel> allTasks;

    public TaskAdapter(Activity activity, ArrayList<TaskModel> items) {
        this.mainActivity = activity;
        this.allTasks = items;
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(mainActivity).inflate(R.layout.my_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);
        holder.tvId.setText(String.valueOf(task.getId()));
        holder.tvTask.setText(task.getName());
        holder.tvSlaDate.setText(task.getSlaDate());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, UpdateActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("name", task.getName());
            intent.putExtra("slaDate", task.getSlaDate());
            mainActivity.startActivityForResult(intent, 1);
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
