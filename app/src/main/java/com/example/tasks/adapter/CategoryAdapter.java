package com.example.tasks.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.activity.CategoryTasksActivity;
import com.example.tasks.data_base.SQLiteHelper;
import com.example.tasks.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final SQLiteHelper myDB;
    private final ActivityResultLauncher<Intent> actResult;
    private final ArrayList<CategoryModel> allCategories;
    private final Intent categoryTasksActivityIntent;

    public CategoryAdapter(
            Context context,
            SQLiteHelper myDB,
            ActivityResultLauncher<Intent> actResult
    ) {
        this.myDB = myDB;
        this.actResult = actResult;
        this.allCategories = myDB.getAllCategories();
        categoryTasksActivityIntent = new Intent(context, CategoryTasksActivity.class);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvQtdOnHoldTask, tvQtdFinishedTask;
        ImageButton imbEditCategory, imbDeleteCategory;
        boolean isSelected;
        int background;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvQtdOnHoldTask = itemView.findViewById(R.id.tvQtdOnHoldTask);
            tvQtdFinishedTask = itemView.findViewById(R.id.tvQtdFinishedTask);
            imbEditCategory = itemView.findViewById(R.id.imbEditCategory);
            imbDeleteCategory = itemView.findViewById(R.id.imbDeleteCategory);
        }
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

        holder.tvCategoryName.setText(category.getName());

        holder.tvQtdOnHoldTask.setText("Em espera: " + myDB.getQtdOnHoldTask(category.getName()));
        holder.tvQtdFinishedTask.setText("Concluídas: " + myDB.getQtdFinishedTask(category.getName()));

        holder.itemView.setOnClickListener(v -> {
            categoryTasksActivityIntent.putExtra("categoryName", category.getName());
            categoryTasksActivityIntent.putExtra("position", holder.getAdapterPosition());
            actResult.launch(categoryTasksActivityIntent);
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

    public void refreshCategory(int position) {
        notifyItemChanged(position);
    }
}
