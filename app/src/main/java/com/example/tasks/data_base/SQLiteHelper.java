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
    private static final String TASK_COLUMN_TITTLE = "tittle";
    private static final String TASK_COLUMN_DESCRIPTION = "description";
    private static final String TASK_COLUMN_STATUS = "status";
    private static final String TASK_COLUMN_EXPIRATION_DATE = "expiration_date";
    private static final String TASK_COLUMN_FINISHED_DATE = "finished_date";
    private static final String TASK_COLUMN_CATEGORY_ID = "category_id";

    private static final String CATEGORY_TABLE_NAME = "tb_category";
    private static final String CATEGORY_COLUMN_NAME = "name";
    private static final String CATEGORY_COLUMN_ID = "id";

    private final SQLiteDatabase db = this.getReadableDatabase();

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + CATEGORY_TABLE_NAME + "("
                        + CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CATEGORY_COLUMN_NAME + " TEXT NOT NULL"
                        + ")"
        );

        db.execSQL(
                "CREATE TABLE " + TASK_TABLE_NAME + "("
                        + TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TASK_COLUMN_TITTLE + " TEXT NOT NULL,"
                        + TASK_COLUMN_DESCRIPTION + " TEXT,"
                        + TASK_COLUMN_STATUS + " INTEGER NOT NULL,"
                        + TASK_COLUMN_EXPIRATION_DATE + " DATE NOT NULL,"
                        + TASK_COLUMN_FINISHED_DATE + " DATE,"
                        + TASK_COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"

                        + " FOREIGN KEY (" + TASK_COLUMN_CATEGORY_ID + ")"
                        + " REFERENCES " + CATEGORY_TABLE_NAME + "(" + CATEGORY_COLUMN_ID + ")"
                        + " ON DELETE CASCADE"
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
        ContentValues cv = getTaskContentValues(task);

        return db.insert(TASK_TABLE_NAME, null, cv);
    }

    public long createCategory(@NonNull CategoryModel category) {
        ContentValues cv = getCategoryContentValues(category);

        return db.insert(CATEGORY_TABLE_NAME, null, cv);
    }

    public long updateTask(@NonNull TaskModel task) {
        ContentValues cv = getTaskContentValues(task);

        return db.update(TASK_TABLE_NAME, cv, "id=" + task.getId(), null);
    }

    public long updateCategory(CategoryModel category) {
        ContentValues cv = getCategoryContentValues(category);

        return db.update(CATEGORY_TABLE_NAME, cv, "id=" + category.getId(), null);
    }

    ContentValues getTaskContentValues(TaskModel task) {
        ContentValues cv = new ContentValues();

        cv.put(TASK_COLUMN_TITTLE, task.getTittle());
        cv.put(TASK_COLUMN_DESCRIPTION, task.getDescription());
        cv.put(TASK_COLUMN_STATUS, task.getStatus());
        cv.put(TASK_COLUMN_EXPIRATION_DATE, task.getExpirationDate());
        cv.put(TASK_COLUMN_FINISHED_DATE, task.getFinishedDate());
        cv.put(TASK_COLUMN_CATEGORY_ID, task.getCategoryId());

        return cv;
    }

    ContentValues getCategoryContentValues(CategoryModel category) {
        ContentValues cv = new ContentValues();

        cv.put(CATEGORY_COLUMN_NAME, category.getName());

        return cv;
    }

    public ArrayList<TaskModel> deleteSelectedTasks(ArrayList<TaskModel> selectedTasks) {
        ArrayList<TaskModel> deletedTasks = new ArrayList<>();

        for (TaskModel task : selectedTasks) {
            long result = db.delete(TASK_TABLE_NAME, "id=" + task.getId(), null);
            if (result == 1) deletedTasks.add(task);
        }

        return deletedTasks;
    }

    public long deleteCategory(CategoryModel category) {
        return db.delete(CATEGORY_TABLE_NAME, "id=" + category.getId(), null);
    }

    public void deleteOnHoldTasks(long categoryId) {
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 0" +
                " AND " + TASK_COLUMN_CATEGORY_ID + "=" + categoryId);
    }

    public void deleteFinishedTasks(long categoryId) {
        db.execSQL("DELETE FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 1" +
                " AND " + TASK_COLUMN_CATEGORY_ID + "=" + categoryId);
    }

    public ArrayList<TaskModel> getAllTasksOnHold() {
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 0" +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasksFromDb(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasks() {
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 1" +
                " ORDER BY " + TASK_COLUMN_FINISHED_DATE + " DESC";

        return getTasksFromDb(query);
    }

    public ArrayList<TaskModel> getAllOnHoldTasksOfCategory(long categoryId) {
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 0" + " AND " +
                TASK_COLUMN_CATEGORY_ID + " = " + categoryId +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasksFromDb(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasksOfCategory(long categoryId) {
        String query = "SELECT *" +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 1" + " AND " +
                TASK_COLUMN_CATEGORY_ID + " = " + categoryId +
                " ORDER BY " + TASK_COLUMN_EXPIRATION_DATE + " ASC";

        return getTasksFromDb(query);
    }

    @SuppressLint("Recycle")
    public int getQtdFinishedTask(long categoryId) {
        String query = "SELECT " + TASK_COLUMN_ID +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 1" + " AND " +
                TASK_COLUMN_CATEGORY_ID + " = " + categoryId;

        return db.rawQuery(query, null).getCount();
    }

    @SuppressLint("Recycle")
    public int getQtdOnHoldTask(long categoryId) {
        String query = "SELECT " + TASK_COLUMN_ID +
                " FROM " + TASK_TABLE_NAME +
                " WHERE " + TASK_COLUMN_STATUS + " = 0" + " AND " +
                TASK_COLUMN_CATEGORY_ID + " = " + categoryId;

        return db.rawQuery(query, null).getCount();
    }

    public ArrayList<CategoryModel> getAllCategories() {
        String query = "SELECT *" +
                " FROM " + CATEGORY_TABLE_NAME +
                " ORDER BY " + CATEGORY_COLUMN_NAME + " ASC";

        return getCategoriesFromDb(query);
    }

    ArrayList<TaskModel> getTasksFromDb(String query) {
        ArrayList<TaskModel> tasks = new ArrayList<>();
        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getLong(6)
                );
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    ArrayList<CategoryModel> getCategoriesFromDb(String query) {
        ArrayList<CategoryModel> categories = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                CategoryModel category = new CategoryModel(
                        cursor.getLong(0),
                        cursor.getString(1)
                );
                categories.add(category);
            }
            cursor.close();
        }
        return categories;
    }
}
