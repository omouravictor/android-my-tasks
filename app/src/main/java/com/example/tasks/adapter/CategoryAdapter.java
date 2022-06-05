package com.example.tasks.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.activity.CategoryTasksActivity;
import com.example.tasks.activity.UpdateCategoryActivity;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.CategoryModel;

import java.util.Comparator;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final SQLiteHelper myDB;
    private final ActivityResultLauncher<Intent> actResult;
    private final List<CategoryModel> allCategories;
    private final Intent categoryTasksActIntent;
    private final Intent updateActIntent;

    public CategoryAdapter(
            Context context,
            SQLiteHelper myDB,
            ActivityResultLauncher<Intent> actResult
    ) {
        this.myDB = myDB;
        this.actResult = actResult;
        this.allCategories = myDB.getAllCategories();
        categoryTasksActIntent = new Intent(context, CategoryTasksActivity.class);
        updateActIntent = new Intent(context, UpdateCategoryActivity.class);
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.CategoryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_category_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoryModel category = allCategories.get(position);
        Context context = holder.itemView.getContext();
        int qtdOnHold = myDB.getQtdOnHoldTask(category.getId());
        int qtdFinished = myDB.getQtdFinishedTask(category.getId());

        holder.tvCategoryName.setText(category.getName());

        holder.tvQtdOnHoldTask.setText(context.getString(R.string.on_hold_x, qtdOnHold));
        holder.tvQtdFinishedTask.setText(context.getString(R.string.finished_x, qtdFinished));

        holder.imbEditCategory.setOnClickListener(v -> {
            updateActIntent.putExtra("category", category);
            updateActIntent.putExtra("catAdaptPosition", holder.getAdapterPosition());
            actResult.launch(updateActIntent);
        });

        holder.imbDeleteCategory.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage("Deseja exluir '" + category.getName() + "' e todas suas tarefas?");

            builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

            builder.setPositiveButton("Sim", (dialog, which) -> {

                try {
                    int index = allCategories.indexOf(category);
                    myDB.deleteCategory(category);
                    allCategories.remove(index);
                    notifyItemRemoved(index);
                } catch (Exception e) {
                    Toast.makeText(context, "Houve um erro", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });

            builder.show();
        });

        holder.itemView.setOnClickListener(v -> {
            categoryTasksActIntent.putExtra("category", category);
            categoryTasksActIntent.putExtra("catAdaptPosition", holder.getAdapterPosition());
            actResult.launch(categoryTasksActIntent);
        });
    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public void addCategory(CategoryModel category) {
        allCategories.add(category);
        notifyItemInserted(getItemCount());
    }

    public void updateCategory(int position, CategoryModel category) {
        allCategories.set(position, category);
        notifyItemChanged(position);
    }

    public void deleteAll() {
        allCategories.clear();
        notifyDataSetChanged();
    }

    public void refreshCategory(int position) {
        notifyItemChanged(position);
    }

    public void sortCategoryByName() {
        allCategories.sort(Comparator.comparing(CategoryModel::getName));
        notifyItemRangeChanged(0, getItemCount());
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvQtdOnHoldTask, tvQtdFinishedTask;
        ImageButton imbEditCategory, imbDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvQtdOnHoldTask = itemView.findViewById(R.id.tvQtdOnHoldTask);
            tvQtdFinishedTask = itemView.findViewById(R.id.tvQtdFinishedTask);
            imbEditCategory = itemView.findViewById(R.id.imbEditCategory);
            imbDeleteCategory = itemView.findViewById(R.id.imbDeleteCategory);
        }
    }
}
