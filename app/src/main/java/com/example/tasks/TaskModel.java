package com.example.tasks;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskModel implements Parcelable {

    private long id;
    private String name;
    private int isFinished = 0;
    private String finishedDate;
    private String expirationDate;

    public TaskModel(String name, String expirationDate) {
        this.name = name;
        this.expirationDate = expirationDate;
    }

    public TaskModel(String name, String expirationDate, int isFinished, String finishedDate) {
        this.name = name;
        setIsFinished(isFinished);
        this.finishedDate = finishedDate;
        this.expirationDate = expirationDate;
    }

    public TaskModel(long id, String name, String expirationDate, int isFinished, String finishedDate) {
        this.id = id;
        this.name = name;
        setIsFinished(isFinished);
        this.finishedDate = finishedDate;
        this.expirationDate = expirationDate;
    }

    protected TaskModel(@NonNull Parcel in) {
        id = in.readLong();
        name = in.readString();
        isFinished = in.readInt();
        finishedDate = in.readString();
        expirationDate = in.readString();
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
        dest.writeInt(isFinished);
        dest.writeString(finishedDate);
        dest.writeString(expirationDate);
    }

    public void finish(String finishedDate) {
        this.isFinished = 1;
        this.finishedDate = finishedDate;
    }

    public void undo() {
        this.isFinished = 0;
        this.finishedDate = null;
    }

    public boolean isFinished() {
        return isFinished == 1;
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

    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        if (isFinished == 1) this.isFinished = 1;
    }

    public String getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }
}
