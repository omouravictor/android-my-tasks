package com.example.tasks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsViewHolder> {

    private final ArrayList<TaskModel> allTasks;
    private final ArrayList<Integer> requiredIDs;

    public RequirementsAdapter(
            SQLiteHelper myDB,
            ArrayList<Integer> requiredIDs,
            Integer categoryID,
            Integer taskID
    ) {
        allTasks = myDB.getRequirementTasks(categoryID, taskID);
        this.requiredIDs = requiredIDs;
        sortAllTasksWithRequirementsFirst();
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

        if (position < requiredIDs.size()) holder.checkBox.setChecked(true);

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
        LocalDate currentDate = LocalDate.now();
        Context context = holder.itemView.getContext();

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

    public void sortAllTasksWithRequirementsFirst() {
        int insertPosition = 0;
        for (int i = 0; i < requiredIDs.size(); i++) {
            Integer requirementID = requiredIDs.get(i);
            for (int j = 0; j < allTasks.size(); j++) {
                TaskModel task = allTasks.get(j);
                if (requirementID.equals(task.getId())) {
                    TaskModel temp = allTasks.get(insertPosition);
                    allTasks.set(insertPosition, task);
                    allTasks.set(j, temp);
                    insertPosition++;
                    break;
                }
            }
        }
    }

    public ArrayList<Integer> getRequirements() {
        return requiredIDs;
    }

}
