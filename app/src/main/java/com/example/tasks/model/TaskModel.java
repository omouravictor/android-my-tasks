package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TaskModel implements Parcelable {

    private Integer id;
    private String tittle;
    private String expirationDate;
    private String description;
    private int status;
    private String finishedDate;
    private Integer categoryId;
    private List<Integer> requiredIDs = new ArrayList<>();

    public TaskModel(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public TaskModel(
            Integer id,
            String tittle,
            String description,
            int status,
            String expirationDate,
            String finishedDate,
            Integer categoryId
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
        id = in.readInt();
        tittle = in.readString();
        description = in.readString();
        status = in.readInt();
        expirationDate = in.readString();
        finishedDate = in.readString();
        categoryId = in.readInt();
        requiredIDs = in.readArrayList(Integer.TYPE.getClassLoader());
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
        if (id != null) dest.writeInt(id);
        else dest.writeInt(-1);

        dest.writeString(tittle);
        dest.writeString(description);
        dest.writeInt(status);
        dest.writeString(expirationDate);
        dest.writeString(finishedDate);
        dest.writeInt(categoryId);
        dest.writeList(requiredIDs);
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

    public boolean hasRequirements() {
        return !requiredIDs.isEmpty();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        if (status != 0 && status != 1)
            this.status = 0;
        else
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setRequiredIDs(List<Integer> requiredIDs) {
        this.requiredIDs = requiredIDs;
    }

    public List<Integer> getRequiredIDs() {
        return requiredIDs;
    }
}
