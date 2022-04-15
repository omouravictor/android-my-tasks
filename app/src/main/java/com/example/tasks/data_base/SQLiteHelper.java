package com.example.tasks.data_base;

import android.annotation.SuppressLint;
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
    private static final String TASK_COLUMN_CATEGORY_NAME = "category_name";
    private static final String TASK_COLUMN_NAME = "name";
    private static final String TASK_COLUMN_EXPIRATION_DATE = "expiration_date";
    private static final String TASK_COLUMN_FINISHED_DATE = "finished_date";
    private static final String CATEGORY_TABLE_NAME = "tb_category";
    private static final String CATEGORY_COLUMN_NAME = "name";
    private final SQLiteDatabase db = this.getReadableDatabase();
    private String query;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + CATEGORY_TABLE_NAME + "("
                        + CATEGORY_COLUMN_NAME + " TEXT PRIMARY KEY"
                        + ")"
        );

        db.execSQL(
                "CREATE TABLE " + TASK_TABLE_NAME + "("
                        + TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TASK_COLUMN_NAME + " TEXT NOT NULL,"
                        + TASK_COLUMN_EXPIRATION_DATE + " DATE NOT NULL,"
                        + TASK_COLUMN_FINISHED_DATE + " DATE,"
                        + TASK_COLUMN_CATEGORY_NAME + " TEXT NOT NULL,"
                        + "FOREIGN KEY (" + TASK_COLUMN_CATEGORY_NAME + ") " +
                        "REFERENCES " + CATEGORY_TABLE_NAME + " (" + CATEGORY_COLUMN_NAME + ")"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        onCreate(db);
    }

    public long createTask(@NonNull TaskModel task) {
        ContentValues cv = new ContentValues();
        long result;

        cv.put(TASK_COLUMN_NAME, task.getName());
        cv.put(TASK_COLUMN_EXPIRATION_DATE, task.getExpirationDate());
        cv.put(TASK_COLUMN_FINISHED_DATE, task.getFinishedDate());
        cv.put(TASK_COLUMN_CATEGORY_NAME, task.getCategoryName());

        result = db.insert(TASK_TABLE_NAME, null, cv);
        return result;
    }

    public long createCategory(@NonNull CategoryModel category) {
        ContentValues cv = new ContentValues();
        long result;

        cv.put(CATEGORY_COLUMN_NAME, category.getName());

        result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        return result;
    }

    public long updateTask(@NonNull TaskModel task) {
        ContentValues cv = new ContentValues();
        long result;

        cv.put(TASK_COLUMN_NAME, task.getName());
        cv.put(TASK_COLUMN_EXPIRATION_DATE, task.getExpirationDate());
        cv.put(TASK_COLUMN_FINISHED_DATE, task.getFinishedDate());
        cv.put(TASK_COLUMN_CATEGORY_NAME, task.getCategoryName());

        result = db.update(TASK_TABLE_NAME, cv, "id=" + task.getId(), null);
        return result;
    }

    public ArrayList<TaskModel> deleteSelectedTasks(ArrayList<TaskModel> selectedTasks) {
        ArrayList<TaskModel> deletedTasks = new ArrayList<>();

        for (TaskModel task : selectedTasks) {
            long result = db.delete(TASK_TABLE_NAME, "id=" + task.getId(), null);
            if (result == 1) deletedTasks.add(task);
        }

        return deletedTasks;
    }

    public void deleteOnHoldTasks() {
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NULL");
    }

    public void deleteFinishedTasks() {
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NOT NULL");
    }

    public ArrayList<TaskModel> getAllTasksOnHold() {
        query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NULL" +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasks(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasks() {
        query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NOT NULL" +
                " ORDER BY " + TASK_COLUMN_FINISHED_DATE + " DESC";

        return getTasks(query);
    }

    public ArrayList<TaskModel> getAllOnHoldTasksOfCategory(String categoryName) {
        query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NULL" + " AND " +
                TASK_COLUMN_CATEGORY_NAME + " = '" + categoryName + "'" +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasks(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasksOfCategory(String categoryName) {
        query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NOT NULL" + " AND " +
                TASK_COLUMN_CATEGORY_NAME + " = '" + categoryName + "'" +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasks(query);
    }

    @SuppressLint("Recycle")
    public int getQtdFinishedTask(String categoryName) {
        query = "SELECT " + TASK_COLUMN_ID +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NOT NULL" + " AND " +
                TASK_COLUMN_CATEGORY_NAME + " = '" + categoryName + "'";

        return db.rawQuery(query, null).getCount();
    }

    @SuppressLint("Recycle")
    public int getQtdOnHoldTask(String categoryName) {
        query = "SELECT " + TASK_COLUMN_ID +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_FINISHED_DATE + " IS NULL" + " AND " +
                TASK_COLUMN_CATEGORY_NAME + " = '" + categoryName + "'";

       return db.rawQuery(query, null).getCount();
    }

    ArrayList<TaskModel> getTasks(String query) {
        ArrayList<TaskModel> tasks = new ArrayList<>();
        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    public ArrayList<CategoryModel> getAllCategories() {
        ArrayList<CategoryModel> categories = new ArrayList<>();
        query = "SELECT *" +
                " FROM " + CATEGORY_TABLE_NAME +
                " ORDER BY " + CATEGORY_COLUMN_NAME + " ASC";
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
