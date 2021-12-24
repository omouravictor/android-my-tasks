package com.example.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "Task.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "tb_tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SLA_DATE = "sla_date";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " LONG PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_SLA_DATE + " TEXT" + ")");
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long createTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, task.getName());
        cv.put(COLUMN_SLA_DATE, task.getSlaDate());

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    public void updateTask(TaskModel updatedTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, updatedTask.getName());
        cv.put(COLUMN_SLA_DATE, updatedTask.getSlaDate());

        long result = db.update(TABLE_NAME, cv, "id=" + updatedTask.getId(), null);
        db.close();

        if (result == -1)
            Toast.makeText(context, "Falha ao atualizar a tarefa.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<TaskModel> getAllTasks() {
        ArrayList<TaskModel> allTasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
                allTasks.add(task);
            }
            cursor.close();
        }

        return allTasks;
    }
}
