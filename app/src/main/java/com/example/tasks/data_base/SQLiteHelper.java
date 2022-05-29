package com.example.tasks.data_base;

import android.annotation.SuppressLint;
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

    private final SQLiteDatabase db = this.getReadableDatabase();
    private static final String DATABASE_NAME = "MyTask.db";
    private static final int DATABASE_VERSION = 1;

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
                "CREATE TABLE tb_category ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "UNIQUE (name)"
                        + ")"
        );

        db.execSQL(
                "CREATE TABLE tb_task ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "tittle TEXT NOT NULL,"
                        + "description TEXT,"
                        + "status INTEGER NOT NULL,"
                        + "expiration_date DATE NOT NULL,"
                        + "finished_date DATE,"
                        + "category_id INTEGER NOT NULL,"

                        + " FOREIGN KEY (category_id) REFERENCES tb_category(id)"
                        + " ON DELETE CASCADE"
                        + ")"
        );

        db.execSQL(
                "CREATE TABLE tb_requirement ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "required_task_id INTEGER REFERENCES tb_task(id),"
                        + "requirement_task_id INTEGER REFERENCES tb_task(id),"

                        + " FOREIGN KEY (required_task_id) REFERENCES tb_task(id) ON DELETE CASCADE,"
                        + " FOREIGN KEY (requirement_task_id) REFERENCES tb_task(id) ON DELETE CASCADE"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_task");
        db.execSQL("DROP TABLE IF EXISTS tb_category");
        db.execSQL("DROP TABLE IF EXISTS tb_requirement");
        onCreate(db);
    }

    public Integer createCategory(@NonNull CategoryModel category) {
        Integer categoryId;

        insertCategoryInDB(category);
        categoryId = getLastID("tb_category");

        return categoryId;
    }

    void insertCategoryInDB(CategoryModel category) {
        db.execSQL(
                "INSERT INTO tb_category (name) VALUES('" + category.getName() + "')"
        );
    }

    public Integer createTask(@NonNull TaskModel task) {
        Integer taskId;

        insertTaskInDB(task);
        taskId = getLastID("tb_task");

        return taskId;
    }

    void insertTaskInDB(TaskModel task) {
        db.execSQL(
                "INSERT INTO tb_task (tittle, description, status, expiration_date, category_id)" +
                        " VALUES('" + task.getTittle() + "','" + task.getDescription() + "',"
                        + task.getStatus() + ",'" + task.getExpirationDate() + "',"
                        + task.getCategoryId() + ")"
        );
    }

    public Integer createRequirement(@NonNull TaskModel task) {
        Integer requirementID;

        insertRequirementInDB(task);
        requirementID = getLastID("tb_requirement");

        return requirementID;
    }

    void insertRequirementInDB(TaskModel task) {
        ArrayList<Integer> requirementsID = task.getRequirementsID();

        for (Integer id : requirementsID) {
            db.execSQL(
                    "INSERT INTO tb_requirement (required_task_id, requirement_task_id)" +
                            " VALUES(" + id + "," + task.getId() + ")"
            );
        }

    }

    public void updateCategory(CategoryModel category) {
        db.execSQL(
                "UPDATE tb_category " +
                        "SET name = '" + category.getName() + "'" +
                        " WHERE id = " + category.getId()
        );
    }

    public void updateTask(@NonNull TaskModel task) {
        if (task.isFinished()) {
            db.execSQL(
                    "UPDATE tb_task " +
                            "SET tittle = '" + task.getTittle() + "'," +
                            "description = '" + task.getDescription() + "'," +
                            "status = " + task.getStatus() + "," +
                            "expiration_date = '" + task.getExpirationDate() + "'," +
                            "finished_date = '" + task.getFinishedDate() + "'," +
                            "category_id = " + task.getCategoryId() +
                            " WHERE id = " + task.getId()
            );
        } else {
            db.execSQL(
                    "UPDATE tb_task " +
                            "SET tittle = '" + task.getTittle() + "'," +
                            "description = '" + task.getDescription() + "'," +
                            "status = " + task.getStatus() + "," +
                            "expiration_date = '" + task.getExpirationDate() + "'," +
                            "finished_date = NULL," +
                            "category_id = " + task.getCategoryId() +
                            " WHERE id = " + task.getId()
            );
        }
    }

    public ArrayList<TaskModel> deleteSelectedTasks(ArrayList<TaskModel> selectedTasks) {
        ArrayList<TaskModel> deletedTasks = new ArrayList<>();

        for (TaskModel task : selectedTasks) {
            try {
                db.execSQL("DELETE FROM tb_task" +
                        " WHERE id = " + task.getId());
                deletedTasks.add(task);
            } catch (Exception e) {
                System.out.println("Failed on delete task with id = " + task.getId());
            }
        }

        return deletedTasks;
    }

    public void deleteCategory(CategoryModel category) {
        db.execSQL("DELETE FROM tb_category" +
                " WHERE id = " + category.getId());
    }

    public void deleteAllCategories() {
        db.execSQL("DELETE FROM tb_category");
    }

    public void deleteOnHoldTasks(Integer categoryId) {
        db.execSQL("DELETE FROM tb_task" +
                " WHERE status = 0" +
                " AND category_id = " + categoryId);
    }

    public void deleteFinishedTasks(Integer categoryId) {
        db.execSQL("DELETE FROM tb_task" +
                " WHERE status = 1" +
                " AND category_id = " + categoryId);
    }

    public ArrayList<TaskModel> getAllTasksOnHold() {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 0" +
                " ORDER BY expiration_date ASC";

        return getTasksFromDB(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasks() {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 1" +
                " ORDER BY finished_date DESC";

        return getTasksFromDB(query);
    }

    public ArrayList<TaskModel> getAllOnHoldTasksOfCategory(Integer categoryId) {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 0 AND category_id = " + categoryId +
                " ORDER BY expiration_date ASC";

        return getTasksFromDB(query);
    }

    public ArrayList<TaskModel> getAllFinishedTasksOfCategory(Integer categoryId) {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 1 AND category_id = " + categoryId +
                " ORDER BY finished_date ASC";

        return getTasksFromDB(query);
    }

    @SuppressLint("Recycle")
    public long getQtdFinishedTask(Integer categoryId) {
        String query = "SELECT COUNT(id)" +
                " FROM tb_task" +
                " WHERE status = 1 AND category_id = " + categoryId;
        Cursor cursor = db.rawQuery(query, null);
        long qtd;

        cursor.moveToFirst();
        qtd = cursor.getLong(0);
        cursor.close();

        return qtd;
    }

    @SuppressLint("Recycle")
    public long getQtdOnHoldTask(Integer categoryId) {
        String query = "SELECT COUNT(id)" +
                " FROM tb_task" +
                " WHERE status = 0 AND category_id = " + categoryId;
        Cursor cursor = db.rawQuery(query, null);
        long qtd;

        cursor.moveToFirst();
        qtd = cursor.getLong(0);
        cursor.close();

        return qtd;
    }

    public ArrayList<CategoryModel> getAllCategories() {
        String query = "SELECT *" +
                " FROM tb_category" +
                " ORDER BY name ASC";

        return getCategoriesFromDB(query);
    }

    ArrayList<TaskModel> getTasksFromDB(String query) {
        ArrayList<TaskModel> tasks = new ArrayList<>();
        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TaskModel task = new TaskModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6)
                );
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    ArrayList<CategoryModel> getCategoriesFromDB(String query) {
        ArrayList<CategoryModel> categories = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                CategoryModel category = new CategoryModel(
                        cursor.getInt(0),
                        cursor.getString(1)
                );
                categories.add(category);
            }
            cursor.close();
        }
        return categories;
    }

    Integer getLastID(String tbName) {
        int lastId;
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + tbName + " WHERE id = (SELECT MAX(id) FROM " + tbName + ")",
                null
        );

        cursor.moveToFirst();
        lastId = cursor.getInt(0);
        cursor.close();

        return lastId;
    }
}
