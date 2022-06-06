package com.example.tasks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsViewHolder>
        implements Filterable {

    private final List<TaskModel> tasksListFull;
    private final List<TaskModel> tasksList;
    private final List<Integer> requiredIDs;

    public RequirementsAdapter(
            SQLiteHelper myDB,
            TaskModel task
    ) {
        requiredIDs = task.getRequiredIDs();
        tasksList = new ArrayList<>();

        if (!requiredIDs.isEmpty())
            tasksList.addAll(myDB.getTasksByIdList(requiredIDs));

        if (task.isFinished())
            tasksList.addAll(tasksList.size(), myDB.getPossibleRequirementsForFinishedTask(task));
        else
            tasksList.addAll(tasksList.size(), myDB.getPossibleRequirementsForOnHoldTask(task));

        tasksListFull = new ArrayList<>(tasksList);
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
        TaskModel task = tasksList.get(position);

        holder.tvTaskName.setText(task.getTittle());

        holder.checkBox.setChecked(requiredIDs.contains(task.getId()));

        holder.itemView.setOnClickListener(v -> {
            CheckBox checkBox = holder.checkBox;
            if (checkBox.isChecked()) {
                requiredIDs.remove(task.getId());
                checkBox.setChecked(false);
            } else {
                requiredIDs.add(task.getId());
                checkBox.setChecked(true);
                if (tasksList.size() != tasksListFull.size()) {
                    int indexOld = requiredIDs.size();
                    TaskModel oldTask = tasksListFull.get(indexOld);
                    int indexCurrent = tasksListFull.indexOf(task);

                    tasksListFull.set(indexOld, task);
                    tasksListFull.set(indexCurrent, oldTask);
                }
            }
        });

        setRequirementLayout(task, holder);
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<TaskModel> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(tasksListFull);
                } else {
                    String filter = constraint.toString().toLowerCase().trim();

                    for (TaskModel task : tasksListFull) {
                        if (task.getTittle().toLowerCase().contains(filter))
                            filteredList.add(task);
                    }
                }

                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tasksList.clear();
                tasksList.addAll((List<TaskModel>) results.values);
                notifyDataSetChanged();
            }
        };
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

}
