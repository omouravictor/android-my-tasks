package com.example.tasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<CategoryModel> allCategories;

    public CategoryAdapter(ArrayList<CategoryModel> allCategories) {
        this.allCategories = allCategories;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        Button btnComplete;
        boolean isSelected;
        int background;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnComplete = itemView.findViewById(R.id.btnFinish);
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
    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public void addCategory(CategoryModel category) {
        allCategories.add(category);
        notifyItemInserted(getItemCount());
    }
}
