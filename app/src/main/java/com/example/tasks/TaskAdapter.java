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
    private TaskAdapter finishedTasksAdapter;
    private final Context context;
    private ActivityResultLauncher<Intent> activityResult;
    private final SQLiteHelper myDB;
    private final ArrayList<TaskModel> allTasks;
    private DateTimeFormatter dtf;
    private LocalDate currentDate;
    private Intent updateActivityIntent;
    private final AlertDialog.Builder builder;
    private boolean isActionMode;
    private ActionMode actionMode;
    private final ArrayList<TaskModel> selectedTasks = new ArrayList<>();
    private final ArrayList<TaskViewHolder> selectedHolders = new ArrayList<>();
    private final ArrayList<TaskViewHolder> allHolders = new ArrayList<>();

    public TaskAdapter(TaskAdapter adapter) {
        this.context = adapter.context;
        this.myDB = adapter.myDB;
        this.allTasks = myDB.getAllFinishedTasks();
        this.builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
    }

    public TaskAdapter(
            Context context,
            ActivityResultLauncher<Intent> activityResult,
            SQLiteHelper myDB,
            DateTimeFormatter dtf,
            LocalDate currentDate
    ) {
        this.context = context;
        this.activityResult = activityResult;
        this.myDB = myDB;
        this.allTasks = myDB.getAllTasksOnHold();
        this.dtf = dtf;
        this.currentDate = currentDate;
        this.updateActivityIntent = new Intent(context, UpdateActivity.class);
        this.builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        sortTaskArrayBySlaDate();
    }

    public ActionMode getActionMode() {
        return actionMode;
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
            btnComplete = itemView.findViewById(R.id.btnFinish);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskViewHolder holder = new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false)
        );
        allHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = allTasks.get(position);

        holder.tvTaskName.setText(task.getName());

        if (task.getIsFinished() == 0) prepareTasksOnHold(task, holder);
        else prepareFinishedTasks(task, holder);

        holder.itemView.setOnLongClickListener(v -> {
            if (!isActionMode) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        myOnCreateActionMode(mode, menu);
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

    public void prepareTasksOnHold(TaskModel task, TaskViewHolder holder) {
        setExpirationTime(task, holder);

        holder.btnComplete.setOnClickListener(v -> {
            builder.setMessage("Concluir '" + task.getName() + "'?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                deleteTask(holder.getAdapterPosition());
                task.setIsFinished(1);
                finishedTasksAdapter.addTask(task);
                dialog.dismiss();
            });
            builder.show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (!isActionMode) {
                updateActivityIntent.putExtra("task", task);
                updateActivityIntent.putExtra("position", holder.getAdapterPosition());
                activityResult.launch(updateActivityIntent);
            } else
                myOnPrepareActionMode(actionMode, holder, task);
        });

    }

    public void prepareFinishedTasks(TaskModel task, TaskViewHolder holder) {
        setFinishedTaskLayout(holder);

        holder.btnComplete.setOnClickListener(v -> {
            builder.setMessage("Excluir '" + task.getName() + "'?");
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
            if (isActionMode) myOnPrepareActionMode(actionMode, holder, task);
        });

    }

    public void setFinishedTaskLayout(TaskViewHolder holder) {
        int green = context.getColor(R.color.green);
        holder.itemView.setBackgroundColor(green);
        holder.background = green;
        holder.tvExpirationTime.setText("Concluída");
    }

    public void sortTaskArrayBySlaDate() {
        allTasks.sort(Comparator.comparingInt(
                task -> Days.daysBetween(currentDate, LocalDate.parse(task.getSlaDate(), dtf)).getDays()
        ));
        notifyItemRangeChanged(0, getItemCount());
    }

    private void setExpirationTime(TaskModel task, TaskViewHolder holder) {
        int days = Days.daysBetween(currentDate, LocalDate.parse(task.getSlaDate(), dtf)).getDays();
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

    private void myOnCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.my_action_mode_menu, menu);
        actionMode = mode;
        isActionMode = true;
    }

    private void myOnActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.finish) {
            builder.setMessage("Concluir " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                ArrayList<TaskModel> deletedTasks = myDB.deleteSelectedTasks(selectedTasks);
                if (deletedTasks.size() == selectedTasks.size()) {
                    deleteSelectedTasks(deletedTasks);
                } else {
                    deleteSelectedTasks(deletedTasks);
                    Toast.makeText(context, "Falha ao deletar alguma(s) tarefa(s).", Toast.LENGTH_SHORT).show();
                }
                putHoldersAsNotSelected(selectedHolders);
                dialog.dismiss();
                mode.finish();
            });
            builder.show();
        } else if (item.getItemId() == R.id.selectAll) {
            if (selectedTasks.size() == allTasks.size()) {
                putHoldersAsNotSelected(allHolders);
                selectedTasks.clear();
                selectedHolders.clear();
                mode.finish();
            } else {
                putHoldersAsSelected();
                selectedTasks.clear();
                selectedHolders.clear();
                selectedTasks.addAll(allTasks);
            }
            mode.setTitle(String.valueOf(selectedTasks.size()));
        }
    }

    private void myOnDestroyActionMode() {
        isActionMode = false;
        if (!selectedTasks.isEmpty()) {
            selectedTasks.clear();
            selectedHolders.clear();
            putHoldersAsNotSelected(allHolders);
        }
    }

    private void myOnPrepareActionMode(ActionMode mode, TaskViewHolder holder, TaskModel task) {
        if (!holder.isSelected) {
            holder.btnComplete.setEnabled(false);
            holder.isSelected = true;
            selectedTasks.add(task);
            selectedHolders.add(holder);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            mode.setTitle(String.valueOf(selectedTasks.size()));
        } else {
            holder.btnComplete.setEnabled(true);
            holder.isSelected = false;
            selectedTasks.remove(task);
            selectedHolders.remove(holder);
            holder.itemView.setBackgroundColor(holder.background);
            mode.setTitle(String.valueOf(selectedTasks.size()));
            if (selectedTasks.isEmpty())
                mode.finish();
        }
    }

    private void putHoldersAsSelected() {
        for (TaskViewHolder holder : allHolders) {
            holder.isSelected = true;
            holder.btnComplete.setEnabled(false);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    private void putHoldersAsNotSelected(ArrayList<TaskViewHolder> holders) {
        for (TaskViewHolder holder : holders) {
            holder.isSelected = false;
            holder.btnComplete.setEnabled(true);
            holder.itemView.setBackgroundColor(holder.background);
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
        allHolders.remove(position);
        notifyItemRemoved(position);
    }

    private void deleteSelectedTasks(ArrayList<TaskModel> deletedTasks) {
        int index;
        for (TaskModel task : deletedTasks) {
            index = allTasks.indexOf(task);
            allTasks.remove(index);
            allHolders.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void deleteAllTasks() {
        allTasks.clear();
        allHolders.clear();
        notifyDataSetChanged();
    }

    public void setFinishedTasksAdapter(TaskAdapter finishedTasksAdapter) {
        this.finishedTasksAdapter = finishedTasksAdapter;
    }
}
