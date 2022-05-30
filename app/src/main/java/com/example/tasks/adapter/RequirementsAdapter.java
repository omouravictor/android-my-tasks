package com.example.tasks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.List;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsViewHolder> {

    private final List<TaskModel> allTasks;
    private final List<Integer> requiredIDs;

    public RequirementsAdapter(
            SQLiteHelper myDB,
            TaskModel task
    ) {
        if (task.isFinished())
            allTasks = myDB.getPossibleRequirementsFinishedTask(task);
        else
            allTasks = myDB.getPossibleRequirementsOnHoldTask(task);

        requiredIDs = task.getRequiredIDs();

        if (task.hasRequirements()) {
            List<TaskModel> requiredTasks = myDB.getAllRequiredTasks(task);
            allTasks.addAll(0, requiredTasks);
        }
    }

    public static class RequirementsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName, tvExpirationTime;
        CheckBox checkBox;
        int background;

        public RequirementsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTittle);
            tvExpirationTime = itemView.findViewById(R.id.tvExpirationTime);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public RequirementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequirementsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_requirement_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementsViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);

        // Neste trecho allTasks já está com os requisitos ocupando as primeiras posições
        if (position < requiredIDs.size())
            holder.checkBox.setChecked(true);

        holder.tvTaskName.setText(task.getTittle());

        holder.itemView.setOnClickListener(v -> {
            CheckBox checkBox = holder.checkBox;
            if (checkBox.isChecked()) {
                requiredIDs.remove(task.getId());
                checkBox.setChecked(false);
            } else {
                requiredIDs.add(task.getId());
                checkBox.setChecked(true);
            }
        });

        setRequirementLayout(task, holder);
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    void setRequirementLayout(TaskModel task, RequirementsViewHolder holder) {
        Context context = holder.itemView.getContext();

        if (task.isFinished())
            setFinishedTaskLayout(context, task, holder);
        else
            setOnHoldTaskLayout(context, task, holder);

    }

    void setOnHoldTaskLayout(Context context, TaskModel task, RequirementsViewHolder holder) {
        LocalDate currentDate = LocalDate.now();
        int days = Days.daysBetween(currentDate, LocalDate.parse(task.getExpirationDate())).getDays();

        if (days > 0) {
            int white = context.getColor(R.color.white);
            holder.itemView.setBackgroundColor(white);
            holder.background = white;
            holder.tvExpirationTime.setText(context.getString(R.string.expires_in_x_days, days));
        } else if (days == 0) {
            int yellow = context.getColor(R.color.lightYellow);
            holder.itemView.setBackgroundColor(yellow);
            holder.background = yellow;
            holder.tvExpirationTime.setText(R.string.expires_today);
        } else {
            int red = context.getColor(R.color.lightRed);
            holder.itemView.setBackgroundColor(red);
            holder.background = red;
            holder.tvExpirationTime.setText(R.string.expired);
        }
    }

    void setFinishedTaskLayout(Context context, TaskModel task, RequirementsViewHolder holder) {
        int green = context.getColor(R.color.green);
        LocalDate finishedDate = LocalDate.parse(task.getFinishedDate());
        String dateFormatText = MyFunctions.getDateText(
                finishedDate.getDayOfMonth(),
                finishedDate.getMonthOfYear(),
                finishedDate.getYear()
        );

        holder.itemView.setBackgroundColor(green);
        holder.background = green;
        holder.tvExpirationTime.setText(context.getString(R.string.finished_in_x, dateFormatText));
    }

    public List<Integer> getRequirements() {
        return requiredIDs;
    }

}
