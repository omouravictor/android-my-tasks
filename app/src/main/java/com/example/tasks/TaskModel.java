package com.example.tasks;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel implements Parcelable {

    private long id;
    private String name;
    private String slaDate;

    public TaskModel(long id, String name, String slaDate) {
        this.id = id;
        this.name = name;
        this.slaDate = slaDate;
    }

    public TaskModel(String name, String slaDate) {
        this.name = name;
        this.slaDate = slaDate;
    }

    protected TaskModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        slaDate = in.readString();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(slaDate);
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

    public String getSlaDate() {
        return slaDate;
    }

    public void setSlaDate(String dateSLA) {
        this.slaDate = dateSLA;
    }

}
