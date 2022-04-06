package com.example.tasks.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tasks.model.CategoryModel;
import com.example.tasks.model.TaskModel;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyTask.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TASK_TABLE_NAME = "tb_task";
    private static final String TASK_COLUMN_ID = "id";
    private static final String TASK_COLUMN_NAME = "name";
    private static final String TASK_COLUMN_EXPIRATION_DATE = "expiration_date";
    private static final String TASK_COLUMN_IS_FINISHED = "is_finished";
    private static final String TASK_COLUMN_FINISHED_DATE = "finished_date";
    private static final String CATEGORY_TABLE_NAME = "tb_category";
    private static final String CATEGORY_COLUMN_NAME = "name";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        String createTbTaskQuery = ("CREATE TABLE " + TASK_TABLE_NAME + "("
                + TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TASK_COLUMN_NAME + " TEXT,"
                + TASK_COLUMN_EXPIRATION_DATE + " DATE,"
                + TASK_COLUMN_IS_FINISHED + " INTEGER,"
                + TASK_COLUMN_FINISHED_DATE + " DATE" + ")");
        db.execSQL(createTbTaskQuery);

        String createTbCategoryQuery = ("CREATE TABLE " + CATEGORY_TABLE_NAME + "("
                + CATEGORY_COLUMN_NAME + " TEXT PRIMARY KEY" + ")");
        db.execSQL(createTbCategoryQuery);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        onCreate(db);
    }

    public long createTask(@NonNull TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long result;

        cv.put(TASK_COLUMN_NAME, task.getName());
        cv.put(TASK_COLUMN_EXPIRATION_DATE, task.getExpirationDate());
        cv.put(TASK_COLUMN_IS_FINISHED, task.getIsFinished());
        cv.put(TASK_COLUMN_FINISHED_DATE, task.getFinishedDate());

        result = db.insert(TASK_TABLE_NAME, null, cv);
        return result;
    }

    public long createCategory(@NonNull CategoryModel category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long result;

        cv.put(CATEGORY_COLUMN_NAME, category.getName());

        result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        return result;
    }

    public long updateTask(@NonNull TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long result;

        cv.put(TASK_COLUMN_NAME, task.getName());
        cv.put(TASK_COLUMN_EXPIRATION_DATE, task.getExpirationDate());
        cv.put(TASK_COLUMN_IS_FINISHED, task.getIsFinished());
        cv.put(TASK_COLUMN_FINISHED_DATE, task.getFinishedDate());

        result = db.update(TASK_TABLE_NAME, cv, "id=" + task.getId(), null);
        return result;
    }

    public ArrayList<TaskModel> deleteSelectedTasks(ArrayList<TaskModel> selectedTasks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<TaskModel> deletedTasks = new ArrayList<>();

        for (TaskModel task : selectedTasks) {
            long result = db.delete(TASK_TABLE_NAME, "id=" + task.getId(), null);
            if (result == 1) deletedTasks.add(task);
        }

        return deletedTasks;
    }

    public void deleteOnHoldTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_IS_FINISHED + " = 0");
    }

    public void deleteFinishedTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_IS_FINISHED + " = 1");
    }

    public ArrayList<TaskModel> getAllTasksOnHold() {
        ArrayList<TaskModel> onHoldTasks = new ArrayList<>();
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_IS_FINISHED + " = 0" +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
                onHoldTasks.add(task);
            }
            cursor.close();
        }

        return onHoldTasks;
    }

    public ArrayList<TaskModel> getAllFinishedTasks() {
        ArrayList<TaskModel> finishedTasks = new ArrayList<>();
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_IS_FINISHED + " = 1" +
                " ORDER BY " + TASK_COLUMN_FINISHED_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
                finishedTasks.add(task);
            }
            cursor.close();
        }

        return finishedTasks;
    }

    public ArrayList<CategoryModel> getAllCategories() {
        ArrayList<CategoryModel> categories = new ArrayList<>();
        String query = "SELECT *" +
                " FROM " + CATEGORY_TABLE_NAME +
                " ORDER BY " + CATEGORY_COLUMN_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                CategoryModel category = new CategoryModel(
                        cursor.getString(0)
                );
                categories.add(category);
            }
            cursor.close();
        }

        return categories;
    }
}
