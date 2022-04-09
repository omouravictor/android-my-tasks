package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskModel implements Parcelable {

    private long id;
    private String name;
    private String expirationDate;
    private String finishedDate;
    private String categoryName;

    public TaskModel(String name, String expirationDate, String categoryName) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.categoryName = categoryName;
    }

    public TaskModel(long id, String name, String expirationDate, String finishedDate, String categoryName) {
        this.id = id;
        this.name = name;
        this.expirationDate = expirationDate;
        this.finishedDate = finishedDate;
        this.categoryName = categoryName;
    }

    protected TaskModel(@NonNull Parcel in) {
        id = in.readLong();
        name = in.readString();
        expirationDate = in.readString();
        finishedDate = in.readString();
        categoryName = in.readString();
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(expirationDate);
        dest.writeString(finishedDate);
        dest.writeString(categoryName);
    }

    public void finish(String finishedDate) {
        this.finishedDate = finishedDate;
    }

    public void undo() {
        this.finishedDate = null;
    }

    public boolean isFinished() {
        return finishedDate != null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
