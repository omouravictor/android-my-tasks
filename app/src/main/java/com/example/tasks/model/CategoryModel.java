package com.example.tasks.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CategoryModel implements Parcelable {

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
    private Integer id;
    private String name;

    public CategoryModel() {
    }

    public CategoryModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    protected CategoryModel(@NonNull Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
