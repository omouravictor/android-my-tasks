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
    private TaskAdapter adaptFinishedTasks;
    private TaskAdapter adaptOnHoldTasks;
    private final Context context;
    private final ActivityResultLauncher<Intent> actResult;
    private final SQLiteHelper myDB;
    private final ArrayList<TaskModel> allTasks;
    private final DateTimeFormatter dtf;
    private final LocalDate currentDate;
    private final Intent updateActivityIntent;
    private final AlertDialog.Builder builder;
    private boolean isActionMode;
    private ActionMode myActionMode;
    private final ArrayList<TaskModel> selectedTasks = new ArrayList<>();
    private final ArrayList<TaskViewHolder> selectedHolders = new ArrayList<>();
    private final ArrayList<TaskViewHolder> allHolders = new ArrayList<>();

    public TaskAdapter(
            Context context,
            ActivityResultLauncher<Intent> actResult,
            SQLiteHelper myDB,
            ArrayList<TaskModel> allTasks,
            DateTimeFormatter dtf,
            LocalDate currentDate
    ) {
        this.context = context;
        this.actResult = actResult;
        this.myDB = myDB;
        this.allTasks = allTasks;
        this.dtf = dtf;
        this.currentDate = currentDate;
        this.updateActivityIntent = new Intent(context, UpdateActivity.class);
        this.builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        sortTaskArrayBySlaDate();
    }

    public void setFinishedTasksAdapter(TaskAdapter adaptFinishedTasks) {
        this.adaptFinishedTasks = adaptFinishedTasks;
    }

    public void setOnHoldTaskAdapter(TaskAdapter adaptOnHoldTasks) {
        this.adaptOnHoldTasks = adaptOnHoldTasks;
    }

    public ArrayList<TaskModel> getAllTasks() {
        return allTasks;
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
        return new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (position >= allHolders.size()) allHolders.add(holder);

        TaskModel task = allTasks.get(position);

        holder.tvTaskName.setText(task.getName());

        if (!task.isFinished()) prepareOnHoldTasks(task, holder);
        else prepareFinishedTasks(task, holder);

        holder.itemView.setOnLongClickListener(v -> {
            if (!isActionMode) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        myOnCreateActionMode(mode, menu, task);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        myOnPrepareActionMode(holder, task);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        myOnActionItemClicked(item);
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

    private void setOnHoldTaskLayout(TaskModel task, TaskViewHolder holder) {
        int days = Days.daysBetween(currentDate, LocalDate.parse(task.getExpirationDate(), dtf)).getDays();
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

    public void prepareOnHoldTasks(TaskModel task, TaskViewHolder holder) {
        setOnHoldTaskLayout(task, holder);

        holder.btnComplete.setOnClickListener(v -> {
            builder.setMessage("Concluir '" + task.getName() + "'?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                task.finish(currentDate.toString(dtf));
                myDB.updateTask(task);
                deleteTask(holder.getAdapterPosition());
                adaptFinishedTasks.addTask(task);
                dialog.dismiss();
            });
            builder.show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (!isActionMode) {
                updateActivityIntent.putExtra("task", task);
                updateActivityIntent.putExtra("position", holder.getAdapterPosition());
                actResult.launch(updateActivityIntent);
            } else
                myOnPrepareActionMode(holder, task);
        });
    }

    public void setFinishedTaskLayout(TaskModel task, TaskViewHolder holder) {
        int green = context.getColor(R.color.green);
        holder.itemView.setBackgroundColor(green);
        holder.background = green;
        holder.tvExpirationTime.setText(context.getString(R.string.finishedText, task.getFinishedDate()));
        holder.btnComplete.setText(R.string.btnDeleteText);
    }

    public void prepareFinishedTasks(TaskModel task, TaskViewHolder holder) {
        setFinishedTaskLayout(task, holder);

        holder.btnComplete.setEnabled(true);
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
            if (isActionMode) myOnPrepareActionMode(holder, task);
        });

    }

    public void sortTaskArrayBySlaDate() {
        allTasks.sort(Comparator.comparingInt(
                task -> Days.daysBetween(currentDate, LocalDate.parse(task.getExpirationDate(), dtf)).getDays()
        ));
        notifyItemRangeChanged(0, getItemCount());
    }

    private void myOnCreateActionMode(ActionMode mode, Menu menu, TaskModel task) {
        myActionMode = mode;
        isActionMode = true;

        myActionMode.getMenuInflater().inflate(R.menu.my_action_mode_menu, menu);

        if (task.isFinished()) mode.getMenu().getItem(0).setVisible(false);
        else mode.getMenu().getItem(1).setVisible(false);
    }

    void putTasksAsFinished(ArrayList<TaskModel> tasksArray) {
        for (TaskModel task : tasksArray) {
            task.finish(currentDate.toString(dtf));
            myDB.updateTask(task);
        }
    }

    void putTasksAsOnHold(ArrayList<TaskModel> tasksArray) {
        for (TaskModel task : tasksArray) {
            task.undo();
            myDB.updateTask(task);
        }
    }

    private void putAllHoldersAsNotSelected() {
        for (TaskViewHolder holder : allHolders) {
            holder.isSelected = false;
            holder.btnComplete.setEnabled(true);
            holder.itemView.setBackgroundColor(holder.background);
        }
    }

    private void putAllHoldersAsSelected() {
        for (TaskViewHolder holder : allHolders) {
            holder.isSelected = true;
            holder.btnComplete.setEnabled(false);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    private void menuItemFinish() {
        if (!selectedTasks.isEmpty()) {
            builder.setMessage("Concluir " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                deleteSelectedTasks(selectedTasks);
                adaptFinishedTasks.putTasksAsFinished(selectedTasks);
                adaptFinishedTasks.addAllTasks(selectedTasks);
                putHoldersAsNotSelected(selectedHolders);
                myActionMode.finish();
                dialog.dismiss();
            });
            builder.show();
        }
    }

    private void menuItemBackToOnHold() {
        if (!selectedTasks.isEmpty()) {
            builder.setMessage("Desfazer " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                deleteSelectedTasks(selectedTasks);
                adaptOnHoldTasks.putTasksAsOnHold(selectedTasks);
                adaptOnHoldTasks.addAllTasks(selectedTasks);
                putHoldersAsNotSelected(selectedHolders);
                myActionMode.finish();
                dialog.dismiss();
            });
            builder.show();
        }
    }

    private void menuItemDelete() {
        builder.setMessage("Exluir " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            ArrayList<TaskModel> deletedTasks = myDB.deleteSelectedTasks(selectedTasks);
            deleteSelectedTasks(deletedTasks);
            if (deletedTasks.size() != selectedTasks.size())
                Toast.makeText(context, "Falha ao deletar alguma(s) tarefa(s).", Toast.LENGTH_SHORT).show();
            putHoldersAsNotSelected(selectedHolders);
            myActionMode.finish();
            dialog.dismiss();
        });
        builder.show();
    }

    private void menuItemSelectAll() {
        if (selectedTasks.size() == allTasks.size()) {
            putAllHoldersAsNotSelected();
            clearSelectedTasksAndHolders();
        } else {
            putAllHoldersAsSelected();
            clearSelectedTasksAndHolders();
            selectedTasks.addAll(allTasks);
        }
        myActionMode.setTitle(String.valueOf(selectedTasks.size()));
    }

    private void myOnActionItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.finish)
            menuItemFinish();
        else if (item.getItemId() == R.id.backToOnHold)
            menuItemBackToOnHold();
        else if (item.getItemId() == R.id.delete)
            menuItemDelete();
        else if (item.getItemId() == R.id.selectAll)
            menuItemSelectAll();
    }

    private void putHoldersAsNotSelected(ArrayList<TaskViewHolder> holders) {
        for (TaskViewHolder holder : holders) {
            holder.isSelected = false;
            holder.btnComplete.setEnabled(true);
            holder.itemView.setBackgroundColor(holder.background);
        }
    }

    private void myOnDestroyActionMode() {
        isActionMode = false;
        putHoldersAsNotSelected(selectedHolders);
        if (!selectedTasks.isEmpty() && !selectedHolders.isEmpty()) clearSelectedTasksAndHolders();
    }

    private void myOnPrepareActionMode(TaskViewHolder holder, TaskModel task) {
        if (!holder.isSelected) {
            holder.btnComplete.setEnabled(false);
            holder.isSelected = true;
            selectedTasks.add(task);
            selectedHolders.add(holder);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            myActionMode.setTitle(String.valueOf(selectedTasks.size()));
        } else {
            holder.btnComplete.setEnabled(true);
            holder.isSelected = false;
            selectedTasks.remove(task);
            selectedHolders.remove(holder);
            holder.itemView.setBackgroundColor(holder.background);
            myActionMode.setTitle(String.valueOf(selectedTasks.size()));
            if (selectedTasks.isEmpty()) myActionMode.finish();
        }
    }

    void clearSelectedTasksAndHolders() {
        selectedTasks.clear();
        selectedHolders.clear();
    }

    public void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
    }

    public void addAllTasks(ArrayList<TaskModel> tasks) {
        int positionStart = getItemCount();
        allTasks.addAll(tasks);
        notifyItemRangeInserted(positionStart, tasks.size());
    }

    public void updateTask(int position, TaskModel task) {
        allTasks.set(position, task);
        notifyItemChanged(position);
    }

    private void deleteTask(int position) {
        allTasks.remove(position);
        allHolders.remove(position);
        notifyItemRemoved(position);
    }

    private void deleteSelectedTasks(ArrayList<TaskModel> tasks) {
        for (TaskModel task : tasks) {
            int index = allTasks.indexOf(task);
            deleteTask(index);
        }
    }

    public void deleteAllTasks() {
        allTasks.clear();
        allHolders.clear();
        notifyDataSetChanged();
    }
}
