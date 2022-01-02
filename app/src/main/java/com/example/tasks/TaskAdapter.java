package com.example.tasks;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<TaskModel> allTasks;
    private AdapterInterface adapterInterface;
    private final DateTimeFormatter dtf;
    private final LocalDate currentDate;

    public TaskAdapter(ArrayList<TaskModel> items, DateTimeFormatter dtf, LocalDate currentDate) {
        this.allTasks = items;
        this.dtf = dtf;
        this.currentDate = currentDate;
        sortTaskArrayBySlaDate(allTasks);
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

        holder.itemView.setOnClickListener(adapterInterface.getOnClickListener(position));
        holder.btnComplete.setOnClickListener(adapterInterface.getBtnCompleteOnClickListener(position));
    }

    public void setExpirationTime(LinearLayout myRow, TextView tvExpirationTime, int days) {
        if (days > 0) {
            tvExpirationTime.setText("Expira em " + days + " dia (s)");
        } else if (days == 0) {
            myRow.setBackgroundColor(Color.parseColor("#FBF6B3"));
            tvExpirationTime.setText("Expira hoje !");
        } else {
            myRow.setBackgroundColor(Color.parseColor("#FFDCD9"));
            tvExpirationTime.setText("Expirada :(");
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

    public void setOnClickListenerInterface(AdapterInterface adapterInterface) {
        this.adapterInterface = adapterInterface;
    }

    public void sortTaskArrayBySlaDate(ArrayList<TaskModel> allTasks) {
        Collections.sort(allTasks, (task1, task2) -> {
            int days1 = Days.daysBetween(currentDate, LocalDate.parse(task1.getSlaDate(), dtf)).getDays();
            int days2 = Days.daysBetween(currentDate, LocalDate.parse(task2.getSlaDate(), dtf)).getDays();
            return Integer.compare(days1, days2);
        });
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        sortTaskArrayBySlaDate(allTasks);
        notifyDataSetChanged();
    }

    public void updateTask(int position, TaskModel updatedTask) {
        allTasks.set(position, updatedTask);
        sortTaskArrayBySlaDate(allTasks);
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
