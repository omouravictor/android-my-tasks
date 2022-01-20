package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Comparator;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private final AlertDialog.Builder builder;
    private final Intent updateActivityIntent;
    private final ActivityResultLauncher<Intent> activityResult;
    private final SQLiteHelper myDB;
    private final ArrayList<TaskModel> allTasks;
    private final DateTimeFormatter dtf;
    private final LocalDate currentDate;
    private final ArrayList<TaskModel> selectedTasks = new ArrayList<>();
    private final ArrayList<TaskViewHolder> selectedHolders = new ArrayList<>();
    private boolean isSelectAll = false;
    private boolean isEnable = false;
    private ActionMode actionMode;

    public TaskAdapter(
            Context context,
            ActivityResultLauncher<Intent> activityResult,
            SQLiteHelper myDB,
            ArrayList<TaskModel> items,
            DateTimeFormatter dtf,
            LocalDate currentDate
    ) {
        this.context = context;
        this.activityResult = activityResult;
        this.myDB = myDB;
        this.allTasks = items;
        this.dtf = dtf;
        this.currentDate = currentDate;
        this.updateActivityIntent = new Intent(context, UpdateActivity.class);
        this.builder = new AlertDialog.Builder(context);
        builder.setMessage("Confirmar conclusão?");
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        sortTaskArrayBySlaDate();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName, tvExpirationTime;
        Button btnComplete;
        boolean isSelected;
        int background;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvExpirationTime = itemView.findViewById(R.id.expirationTime);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            isSelected = false;
            background = 0;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.my_row, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);
        int days = Days.daysBetween(currentDate, LocalDate.parse(task.getSlaDate(), dtf)).getDays();

        holder.tvTaskName.setText(task.getName());

        setExpirationTime(holder, days);

        holder.btnComplete.setOnClickListener(v -> {
            builder.setTitle(task.getName());
            builder.setPositiveButton("Sim", (dialog, which) -> {
                if (myDB.deleteTask(task) != 0)
                    deleteTask(holder.getAdapterPosition());
                else
                    Toast.makeText(context, "Falha ao deletar a tarefa.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (!isEnable) {
                updateActivityIntent.putExtra("task", task);
                updateActivityIntent.putExtra("position", holder.getAdapterPosition());
                activityResult.launch(updateActivityIntent);
            } else
                myOnPrepareActionMode(actionMode, holder, task);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isEnable) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.my_menu2, menu);
                        actionMode = mode;
                        isEnable = true;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        myOnPrepareActionMode(mode, holder, task);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        myOnActionItemClicked(mode, item);
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        myOnDestroyActionMode();
                    }
                };
                v.startActionMode(callback);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    private void setExpirationTime(TaskViewHolder holder, int days) {
        if (days > 0) {
            int white = context.getColor(R.color.white);
            holder.itemView.setBackgroundColor(white);
            holder.background = white;
            holder.tvExpirationTime.setText(context.getString(R.string.expirationDaysText, days));
        } else if (days == 0) {
            int yellow = context.getColor(R.color.lightYellow);
            holder.itemView.setBackgroundColor(yellow);
            holder.background = yellow;
            holder.tvExpirationTime.setText(R.string.expirationTodayText);
        } else {
            int red = context.getColor(R.color.lightRed);
            holder.itemView.setBackgroundColor(red);
            holder.background = red;
            holder.tvExpirationTime.setText(R.string.expiredText);
        }
    }

    public void sortTaskArrayBySlaDate() {
        allTasks.sort(Comparator.comparingInt(
                task -> Days.daysBetween(currentDate, LocalDate.parse(task.getSlaDate(), dtf)).getDays()
        ));
        notifyItemRangeChanged(0, getItemCount());
    }

    private void myOnPrepareActionMode(ActionMode mode, TaskViewHolder holder, TaskModel task) {
        if (!holder.isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            selectedTasks.add(task);
            mode.setTitle(String.valueOf(selectedTasks.size()));
            selectedHolders.add(holder);
            holder.isSelected = true;
        } else {
            holder.itemView.setBackgroundColor(holder.background);
            selectedTasks.remove(task);
            mode.setTitle(String.valueOf(selectedTasks.size()));
            selectedHolders.remove(holder);
            holder.isSelected = false;
            if (selectedTasks.isEmpty())
                mode.finish();
        }
    }

    private void myOnActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            builder.setPositiveButton("Sim", (dialog, which) -> {
                ArrayList<TaskModel> deletedTasks = myDB.deleteSelectedTasks(selectedTasks);
                if (deletedTasks.size() == selectedTasks.size()) {
                    deleteSelectedTasks(deletedTasks);
                } else {
                    deleteSelectedTasks(deletedTasks);
                    Toast.makeText(context, "Falha ao deletar alguma tarefa.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                mode.finish();
            });
            builder.show();
        } else if (item.getItemId() == R.id.selectAll) {
            if (selectedTasks.size() == allTasks.size()) {
                isSelectAll = false;
                selectedTasks.clear();
            } else {
                isSelectAll = true;
                selectedTasks.clear();
                selectedTasks.addAll(allTasks);
            }
        }
    }

    private void myOnDestroyActionMode() {
        isEnable = false;
        isSelectAll = false;
        if (!selectedTasks.isEmpty()) {
            resetHolders();
            selectedTasks.clear();
            selectedHolders.clear();
        }
    }

    private void resetHolders() {
        for (TaskViewHolder holder : selectedHolders) {
            holder.itemView.setBackgroundColor(holder.background);
            holder.isSelected = false;
        }
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
    }

    public void updateTask(int position, TaskModel updatedTask) {
        allTasks.set(position, updatedTask);
        notifyItemChanged(position);
    }

    private void deleteTask(int position) {
        allTasks.remove(position);
        notifyItemRemoved(position);
    }

    private void deleteSelectedTasks(ArrayList<TaskModel> deletedTasks) {
        allTasks.removeAll(deletedTasks);
        notifyDataSetChanged();
    }

    public void deleteAllTasks() {
        allTasks.clear();
        notifyDataSetChanged();
    }

}
