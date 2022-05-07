package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskModel implements Parcelable {

    private long id;
    private String name;
    private String description;
    private int status;
    private String expirationDate;
    private String finishedDate;
    private long categoryId;

    public TaskModel(String name, String expirationDate, long categoryId) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.categoryId = categoryId;
    }

    public TaskModel(
            long id,
            String name,
            String description,
            int status,
            String expirationDate,
            String finishedDate,
            long categoryId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.expirationDate = expirationDate;
        this.finishedDate = finishedDate;
        this.categoryId = categoryId;
    }

    protected TaskModel(@NonNull Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        status = in.readInt();
        expirationDate = in.readString();
        finishedDate = in.readString();
        categoryId = in.readLong();
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
        dest.writeString(description);
        dest.writeInt(status);
        dest.writeString(expirationDate);
        dest.writeString(finishedDate);
        dest.writeLong(categoryId);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
