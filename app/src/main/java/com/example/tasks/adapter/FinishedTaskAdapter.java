package com.example.tasks.adapter;

import android.app.Activity;
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

import com.example.tasks.MyFunctions;
import com.example.tasks.R;
import com.example.tasks.activity.UpdateFinishedTaskActivity;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.TaskModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FinishedTaskAdapter extends RecyclerView.Adapter<FinishedTaskAdapter.TaskViewHolder> {
    private OnHoldTaskAdapter adaptOnHoldTasks;
    private ActionMode myActionMode;
    private final int catAdaptPosition;
    private final Activity activity;
    private final ActivityResultLauncher<Intent> actResult;
    private final SQLiteHelper myDB;
    private final List<TaskModel> allTasks;
    private final LocalDate currentDate;
    private final Intent updateActivityIntent;
    private final AlertDialog.Builder builder;
    private boolean isActionMode;
    private final List<TaskModel> selectedTasks;
    private final List<TaskViewHolder> selectedHolders;
    private final List<TaskViewHolder> allHolders;

    public FinishedTaskAdapter(
            Activity activity,
            int catAdaptPosition,
            ActivityResultLauncher<Intent> actResult,
            SQLiteHelper myDB,
            Integer categoryId
    ) {
        this.activity = activity;
        this.catAdaptPosition = catAdaptPosition;
        this.actResult = actResult;
        this.myDB = myDB;
        this.allTasks = myDB.getAllFinishedTasksOfCategory(categoryId);
        currentDate = LocalDate.now();
        updateActivityIntent = new Intent(activity, UpdateFinishedTaskActivity.class);
        builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        selectedTasks = new ArrayList<>();
        selectedHolders = new ArrayList<>();
        allHolders = new ArrayList<>();
    }

    public void setOnHoldTaskAdapter(OnHoldTaskAdapter adaptOnHoldTasks) {
        this.adaptOnHoldTasks = adaptOnHoldTasks;
    }

    public List<TaskModel> getAllTasks() {
        return allTasks;
    }

    public void putTasksAsFinished(List<TaskModel> tasksArray) {
        for (TaskModel task : tasksArray) {
            task.finish(currentDate.toString());
            myDB.updateTask(task);
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName, tvExpirationTime;
        Button btnUndo;
        boolean isSelected;
        int background;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTittle);
            tvExpirationTime = itemView.findViewById(R.id.tvExpirationTime);
            btnUndo = itemView.findViewById(R.id.btnAction);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_task_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        allHolders.add(holder);

        TaskModel task = allTasks.get(position);

        holder.tvTaskName.setText(task.getTittle());

        holder.itemView.setOnClickListener(v -> {
            if (!isActionMode) {
                updateActivityIntent.putExtra("task", task);
                updateActivityIntent.putExtra("taskAdaptPosition", holder.getAdapterPosition());
                actResult.launch(updateActivityIntent);
            } else
                myOnPrepareActionMode(holder, task);
        });

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

        prepareFinishedTasks(task, holder);
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    void setFinishedTaskLayout(TaskModel task, TaskViewHolder holder) {
        int green = activity.getColor(R.color.green);
        LocalDate finishedDate = LocalDate.parse(task.getFinishedDate());
        String dateFormatText = MyFunctions.getDateText(
                finishedDate.getDayOfMonth(),
                finishedDate.getMonthOfYear(),
                finishedDate.getYear()
        );

        holder.itemView.setBackgroundColor(green);
        holder.background = green;
        holder.tvExpirationTime.setText(activity.getString(R.string.finished_in_x, dateFormatText));
        holder.btnUndo.setText(R.string.undo);
    }

    void prepareFinishedTasks(TaskModel task, TaskViewHolder holder) {
        setFinishedTaskLayout(task, holder);

        holder.btnUndo.setEnabled(true);
        holder.btnUndo.setOnClickListener(v -> {
            builder.setMessage("Desfazer '" + task.getTittle() + "'?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                deleteTask(holder.getAdapterPosition());
                putTasksAsOnHold(task);
                adaptOnHoldTasks.addTask(task);
                dialog.dismiss();
            });
            builder.show();
        });
    }

    public void sortTasksArrayByFinishedDate() {
        allTasks.sort(Comparator.comparingInt(
                task -> Days.daysBetween(
                        LocalDate.parse(task.getFinishedDate()),
                        currentDate
                ).getDays()
        ));
        notifyItemRangeChanged(0, getItemCount());
    }

    void myOnCreateActionMode(ActionMode mode, Menu menu, TaskModel task) {
        myActionMode = mode;
        isActionMode = true;

        myActionMode.getMenuInflater().inflate(R.menu.my_action_mode_menu, menu);

        if (task.isFinished()) mode.getMenu().getItem(0).setVisible(false);
        else mode.getMenu().getItem(1).setVisible(false);
    }

    void putTasksAsOnHold(TaskModel task) {
        task.undo();
        myDB.updateTask(task);
    }

    void putTasksAsOnHold(List<TaskModel> tasksArray) {
        for (TaskModel task : tasksArray) {
            task.undo();
            myDB.updateTask(task);
        }
    }

    void putAllHoldersAsNotSelected() {
        for (TaskViewHolder holder : allHolders) {
            holder.isSelected = false;
            holder.btnUndo.setEnabled(true);
            holder.itemView.setBackgroundColor(holder.background);
        }
    }

    void putAllHoldersAsSelected() {
        for (TaskViewHolder holder : allHolders) {
            holder.isSelected = true;
            holder.btnUndo.setEnabled(false);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    void menuItemBackToOnHold() {
        if (!selectedTasks.isEmpty()) {
            builder.setMessage("Desfazer " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                deleteSelectedTasks(selectedTasks);
                putTasksAsOnHold(selectedTasks);
                adaptOnHoldTasks.addAllTasks(selectedTasks);
                putHoldersAsNotSelected(selectedHolders);
                myActionMode.finish();
                dialog.dismiss();
            });
            builder.show();
        }
    }

    void menuItemDelete() {
        builder.setMessage("Exluir " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            List<TaskModel> deletedTasks = myDB.deleteSelectedTasks(selectedTasks);

            deleteSelectedTasks(deletedTasks);
            activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));

            if (deletedTasks.size() != selectedTasks.size())
                Toast.makeText(activity, "Falha ao deletar alguma(s) tarefa(s).", Toast.LENGTH_SHORT).show();

            putHoldersAsNotSelected(selectedHolders);
            myActionMode.finish();
            dialog.dismiss();
        });
        builder.show();
    }

    void menuItemSelectAll() {
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

    void myOnActionItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.backToOnHold)
            menuItemBackToOnHold();
        else if (item.getItemId() == R.id.delete)
            menuItemDelete();
        else if (item.getItemId() == R.id.selectAll)
            menuItemSelectAll();
    }

    void putHoldersAsNotSelected(List<TaskViewHolder> holders) {
        for (TaskViewHolder holder : holders) {
            holder.isSelected = false;
            holder.btnUndo.setEnabled(true);
            holder.itemView.setBackgroundColor(holder.background);
        }
    }

    void myOnDestroyActionMode() {
        isActionMode = false;
        putHoldersAsNotSelected(selectedHolders);
        if (!selectedTasks.isEmpty() && !selectedHolders.isEmpty()) clearSelectedTasksAndHolders();
    }

    void myOnPrepareActionMode(TaskViewHolder holder, TaskModel task) {
        if (!holder.isSelected) {
            holder.btnUndo.setEnabled(false);
            holder.isSelected = true;
            selectedTasks.add(task);
            selectedHolders.add(holder);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            myActionMode.setTitle(String.valueOf(selectedTasks.size()));
        } else {
            holder.btnUndo.setEnabled(true);
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

    void addTask(TaskModel task) {
        allTasks.add(task);
        notifyItemInserted(getItemCount());
        activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
    }

    public void addAllTasks(List<TaskModel> tasks) {
        int positionStart = getItemCount();
        allTasks.addAll(tasks);
        notifyItemRangeInserted(positionStart, tasks.size());
        activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
    }

    public void updateTask(int position, TaskModel task) {
        allTasks.set(position, task);
        notifyItemChanged(position);
    }

    void deleteTask(int position) {
        allTasks.remove(position);
        allHolders.remove(position);
        notifyItemRemoved(position);
    }

    void deleteSelectedTasks(List<TaskModel> tasks) {
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
