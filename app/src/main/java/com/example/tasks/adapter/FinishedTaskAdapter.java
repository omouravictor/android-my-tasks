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
import android.widget.LinearLayout;
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
import com.example.tasks.model.CategoryModel;
import com.example.tasks.model.TaskModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinishedTaskAdapter extends RecyclerView.Adapter<FinishedTaskAdapter.TaskViewHolder> {
    private final Map<Integer, TaskModel> map;
    private final int catAdaptPosition;
    private final Activity activity;
    private final ActivityResultLauncher<Intent> actResult;
    private final SQLiteHelper myDB;
    private final List<TaskModel> allTasks;
    private final LocalDate currentDate;
    private final Intent updateActivityIntent;
    private final List<TaskModel> selectedTasks;
    private final List<TaskViewHolder> selectedHolders;
    private final List<TaskViewHolder> allHolders;
    private final CategoryModel category;
    private OnHoldTaskAdapter adaptOnHoldTasks;
    private ActionMode myActionMode;
    private boolean isActionMode;

    public FinishedTaskAdapter(
            Activity activity,
            int catAdaptPosition,
            ActivityResultLauncher<Intent> actResult,
            SQLiteHelper myDB,
            CategoryModel category
    ) {
        this.activity = activity;
        this.catAdaptPosition = catAdaptPosition;
        this.actResult = actResult;
        this.myDB = myDB;
        this.category = category;
        this.allTasks = myDB.getAllFinishedTasksOfCategory(category.getId());
        map = new HashMap<>();
        currentDate = LocalDate.now();
        updateActivityIntent = new Intent(activity, UpdateFinishedTaskActivity.class);
        selectedTasks = new ArrayList<>();
        selectedHolders = new ArrayList<>();
        allHolders = new ArrayList<>();
        mapTasksByID(allTasks);
    }

    void mapTasksByID(List<TaskModel> tasks) {
        for (TaskModel task : tasks)
            map.put(task.getId(), task);
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

        holder.tvTittle.setText(task.getTittle());

        holder.itemView.setOnClickListener(v -> {
            if (!isActionMode) {
                updateActivityIntent.putExtra("task", task);
                updateActivityIntent.putExtra("categoryName", category.getName());
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
        int requiredQtd = myDB.getQtdOfRequiredTasks(task.getId());
        String dateFormatText = MyFunctions.getDateText(
                finishedDate.getDayOfMonth(),
                finishedDate.getMonthOfYear(),
                finishedDate.getYear()
        );

        if (requiredQtd > 0) {
            holder.layQtdRequirements.setVisibility(View.VISIBLE);
            holder.tvQtdRequirements.setText(String.valueOf(requiredQtd));
        } else {
            holder.layQtdRequirements.setVisibility(View.GONE);
        }

        holder.itemView.setBackgroundColor(green);
        holder.background = green;
        holder.tvFinishedDate.setText(activity.getString(R.string.finished_in_x, dateFormatText));
        holder.btnUndo.setText(R.string.undo);
    }

    void prepareFinishedTasks(TaskModel task, TaskViewHolder holder) {
        setFinishedTaskLayout(task, holder);

        holder.btnUndo.setOnClickListener(v -> {
            if (myDB.taskCanBeUndo(task.getId())) {
                deleteRow(holder.getAdapterPosition(), task);
                putTasksAsOnHold(task);
                adaptOnHoldTasks.addRow(task);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Essa tarefa não pode ser desfeita pois é requisito de outra " +
                        "tarefa que já está concluida :(");
                builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
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

    void menuItemUndo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
        builder.setMessage("Pelo menos uma tarefa selecionada é requisito de outra tarefa que já está concluida :(");

        if (!selectedTasks.isEmpty()) {
            if (myDB.listCanBeUndo(selectedTasks)) {
                deleteRowS(selectedTasks);
                putTasksAsOnHold(selectedTasks);
                adaptOnHoldTasks.addRowS(selectedTasks);
                putHoldersAsNotSelected(selectedHolders);
                myActionMode.finish();
            } else {
                builder.show();
            }
        }
    }

    void menuItemDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Exluir " + selectedTasks.size() + " tarefa(s) selecionada(s)?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            List<TaskModel> deletedTasks = new ArrayList<>();

            for (TaskModel task : selectedTasks) {
                try {
                    List<Integer> requirementTaskIDs = myDB.getAllRequirementsIDs(task.getId());
                    myDB.deleteTaskInDB(task.getId());
                    refreshRequirementRowS(requirementTaskIDs);
                    deletedTasks.add(task);
                } catch (Exception e) {
                    System.out.println("Failed on delete task with id = " + task.getId());
                }
            }

            deleteRowS(deletedTasks);
            activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));

            if (deletedTasks.size() != selectedTasks.size())
                Toast.makeText(activity, "Falha ao deletar alguma(s) tarefa(s).", Toast.LENGTH_SHORT).show();

            putHoldersAsNotSelected(selectedHolders);
            myActionMode.finish();
            dialog.dismiss();
        });
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
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
        if (item.getItemId() == R.id.undo)
            menuItemUndo();
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

    void addRow(TaskModel task) {
        allTasks.add(task);
        map.put(task.getId(), task);
        notifyItemInserted(getItemCount());
        activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
    }

    public void addRowS(List<TaskModel> tasks) {
        int positionStart = getItemCount();
        allTasks.addAll(tasks);
        mapTasksByID(tasks);
        notifyItemRangeInserted(positionStart, tasks.size());
        activity.setResult(3, new Intent().putExtra("catAdaptPosition", catAdaptPosition));
    }

    public void updateRow(int position, TaskModel task) {
        allTasks.set(position, task);
        map.put(task.getId(), task);
        notifyItemChanged(position);
    }

    void deleteRow(int position, TaskModel task) {
        allTasks.remove(position);
        map.remove(task.getId());
        allHolders.remove(position);
        notifyItemRemoved(position);
    }

    void deleteRowS(List<TaskModel> tasks) {
        for (TaskModel task : tasks) {
            int index = allTasks.indexOf(task);
            deleteRow(index, task);
        }
    }

    public void deleteAllRowS() {
        allTasks.clear();
        map.clear();
        allHolders.clear();
        notifyDataSetChanged();
    }

    void refreshRequirementRowS(List<Integer> requirementTaskIDs) {

        for (Integer id : requirementTaskIDs) {
            TaskModel requirement = map.get(id);
            int index = allTasks.indexOf(requirement);

            if (index != -1)
                notifyItemChanged(index);
            else
                adaptOnHoldTasks.refreshRequirementTaskRow(id);
        }

    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layQtdRequirements;
        TextView tvQtdRequirements, tvTittle, tvFinishedDate;
        Button btnUndo;
        boolean isSelected;
        int background;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            layQtdRequirements = itemView.findViewById(R.id.layQtdRequirements);
            tvQtdRequirements = itemView.findViewById(R.id.tvQtdRequirements);
            tvTittle = itemView.findViewById(R.id.tvTittle);
            tvFinishedDate = itemView.findViewById(R.id.tvDate);
            btnUndo = itemView.findViewById(R.id.btnAction);
        }
    }
}
