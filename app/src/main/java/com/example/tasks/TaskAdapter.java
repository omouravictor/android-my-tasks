package com.example.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<TaskModel> allTasks;
    private AdapterInterface adapterInterface;

    public TaskAdapter(ArrayList<TaskModel> items) {
        this.allTasks = items;
    }

    public void setOnClickListenerInterface(AdapterInterface adapterInterface) {
        this.adapterInterface = adapterInterface;
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
    }

    public void updateTask(int position, TaskModel updatedTask) {
        allTasks.set(position, updatedTask);
        notifyItemChanged(position);
    }

    public TaskModel getTask(int position) {
        return allTasks.get(position);
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
        holder.tvId.setText(String.valueOf(task.getId()));
        holder.tvTask.setText(task.getName());
        holder.tvSlaDate.setText(task.getSlaDate());
        holder.itemView.setOnClickListener(adapterInterface.getOnClickListener(position));
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
