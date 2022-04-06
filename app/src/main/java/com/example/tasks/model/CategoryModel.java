package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CategoryModel implements Parcelable {

    private String name;
    private ArrayList<TaskModel> tasksList;

    public CategoryModel(String name) {
        this.name = name;
    }

    public CategoryModel(String name, ArrayList<TaskModel> tasksList) {
        this.name = name;
        this.tasksList = tasksList;
    }

    protected CategoryModel(@NonNull Parcel in) {
        name = in.readString();
        tasksList = in.readArrayList(TaskModel.class.getClassLoader());
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(tasksList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TaskModel> getTasksList() {
        return tasksList;
    }

    public void setTasksList(ArrayList<TaskModel> tasksList) {
        this.tasksList = tasksList;
    }
}
