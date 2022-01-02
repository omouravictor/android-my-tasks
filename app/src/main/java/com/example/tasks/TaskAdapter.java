package com.example.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    MyFunctions myFunctions = new MyFunctions();
    private final ArrayList<TaskModel> allTasks;
    private AdapterInterface adapterInterface;
    private final DateTimeFormatter dtf;
    private final LocalDate currentDate;

    public TaskAdapter(ArrayList<TaskModel> items, DateTimeFormatter dtf, LocalDate currentDate) {
        this.allTasks = items;
        this.dtf = dtf;
        this.currentDate = currentDate;
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

        holder.tvId.setText(String.valueOf(task.getId()));
        holder.tvTask.setText(task.getName());

        setSlaDateText(holder.tvSlaDate, days);

        holder.itemView.setOnClickListener(adapterInterface.getOnClickListener(position));
        holder.btnComplete.setOnClickListener(adapterInterface.getBtnCompleteOnClickListener(position));
    }

    public void setSlaDateText(TextView tvSlaDate, int days) {
        if (days > 0)
            tvSlaDate.setText("Expira em " + days + " dia (s)");
        else if (days == 0)
            tvSlaDate.setText("Expira HOJE!");
        else
            tvSlaDate.setText("Expirada");
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvTask, tvSlaDate;
        Button btnComplete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvSlaDate = itemView.findViewById(R.id.tvSlaDate);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
    }

    public void setOnClickListenerInterface(AdapterInterface adapterInterface) {
        this.adapterInterface = adapterInterface;
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        myFunctions.sortTaskArrayBySlaDate(allTasks, dtf, currentDate);
        notifyDataSetChanged();
    }

    public void updateTask(int position, TaskModel updatedTask) {
        allTasks.set(position, updatedTask);
        myFunctions.sortTaskArrayBySlaDate(allTasks, dtf, currentDate);
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        allTasks.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteAllTasks() {
        allTasks.clear();
        notifyDataSetChanged();
    }

    public TaskModel getTask(int position) {
        return allTasks.get(position);
    }
}
