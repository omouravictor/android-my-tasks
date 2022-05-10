package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskModel implements Parcelable {

    private long id;
    private String tittle;
    private String description;
    private int status = 0;
    private String expirationDate;
    private String finishedDate;
    private long categoryId;

    public TaskModel(String tittle, String description, String expirationDate, long categoryId) {
        this.tittle = tittle;
        this.description = description;
        this.expirationDate = expirationDate;
        this.categoryId = categoryId;
    }

    public TaskModel(
            long id,
            String tittle,
            String description,
            int status,
            String expirationDate,
            String finishedDate,
            long categoryId
    ) {
        this.id = id;
        this.tittle = tittle;
        this.description = description;
        this.status = status;
        this.expirationDate = expirationDate;
        this.finishedDate = finishedDate;
        this.categoryId = categoryId;
    }

    protected TaskModel(@NonNull Parcel in) {
        id = in.readLong();
        tittle = in.readString();
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
        dest.writeString(tittle);
        dest.writeString(description);
        dest.writeInt(status);
        dest.writeString(expirationDate);
        dest.writeString(finishedDate);
        dest.writeLong(categoryId);
    }

    public void finish(String finishedDate) {
        this.status = 1;
        this.finishedDate = finishedDate;
    }

    public void undo() {
        this.status = 0;
        this.finishedDate = null;
    }

    public boolean isFinished() {
        return status == 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
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
