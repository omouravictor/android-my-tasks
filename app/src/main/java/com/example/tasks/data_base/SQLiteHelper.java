package com.example.tasks.data_base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tasks.model.CategoryModel;
import com.example.tasks.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyTask.db";
    private static final int DATABASE_VERSION = 1;
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
                        + "required_task_id INTEGER REFERENCES tb_task(id),"
                        + "requirement_task_id INTEGER REFERENCES tb_task(id),"

                        + " CONSTRAINT PK_requirement PRIMARY KEY (required_task_id,requirement_task_id),"
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

    public void createRequirement(Integer taskID, List<Integer> requirementsID) {
        insertRequirementInDB(taskID, requirementsID);
    }

    void insertRequirementInDB(Integer taskID, List<Integer> requirementsID) {
        for (Integer id : requirementsID) {
            db.execSQL(
                    "INSERT INTO tb_requirement (required_task_id, requirement_task_id)" +
                            " VALUES(" + id + "," + taskID + ")"
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

    public void deleteTaskInDB(Integer taskID) {
        db.execSQL("DELETE FROM tb_task" +
                " WHERE id = " + taskID);
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

    public void deleteRequirementsOfTask(TaskModel task) {
        db.execSQL(
                "DELETE FROM tb_requirement" +
                        " WHERE requirement_task_id = " + task.getId()
        );
    }

    public List<TaskModel> getTasksByIdList(List<Integer> idList) {
        List<TaskModel> tasks = new ArrayList<>();
        TaskModel task;
        String query;

        for (Integer id : idList) {
            query = "SELECT *" +
                    " FROM tb_task " +
                    " WHERE id = " + id;
            task = getTaskFromDB(query);
            tasks.add(task);
        }

        return tasks;
    }

    public List<TaskModel> getPossibleRequirementsForFinishedTask(TaskModel task, List<Integer> requiredIDs) {
        String IDClause = " AND id != " + task.getId();

        for (Integer id : requiredIDs)
            IDClause += " AND id != " + id;

        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 1 AND category_id = " + task.getCategoryId() + IDClause +
                " ORDER BY finished_date DESC";

        return getTasksFromDB(query);
    }

    public List<TaskModel> getPossibleRequirementsForOnHoldTask(TaskModel task, List<Integer> requiredIDs) {
        String IDClause = " AND id != " + task.getId();

        for (Integer id : requiredIDs)
            IDClause += " AND id != " + id;

        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE category_id = " + task.getCategoryId() + IDClause +
                " ORDER BY expiration_date ASC";

        return getTasksFromDB(query);
    }

    public int getQtdFinishedTaskOfCategory(Integer categoryID) {
        String query = "SELECT COUNT(id)" +
                " FROM tb_task" +
                " WHERE status = 1 AND category_id = " + categoryID;
        Cursor cursor = db.rawQuery(query, null);
        int qtd;

        cursor.moveToFirst();
        qtd = cursor.getInt(0);
        cursor.close();

        return qtd;
    }

    public int getQtdOnHoldTaskOfCategory(Integer categoryID) {
        String query = "SELECT COUNT(id)" +
                " FROM tb_task" +
                " WHERE status = 0 AND category_id = " + categoryID;
        Cursor cursor = db.rawQuery(query, null);
        int qtd;

        cursor.moveToFirst();
        qtd = cursor.getInt(0);
        cursor.close();

        return qtd;
    }

    public int getQtdOfRequiredTasks(Integer taskID) {
        String query = "SELECT COUNT(required_task_id)" +
                " FROM tb_requirement" +
                " WHERE requirement_task_id = " + taskID;
        Cursor cursor = db.rawQuery(query, null);
        int qtd;

        cursor.moveToFirst();
        qtd = cursor.getInt(0);
        cursor.close();

        return qtd;
    }

    public List<TaskModel> getAllOnHoldTasksOfCategory(Integer categoryID) {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 0 AND category_id = " + categoryID +
                " ORDER BY expiration_date ASC, tittle ASC";

        return getTasksFromDB(query);
    }

    public List<TaskModel> getAllFinishedTasksOfCategory(Integer categoryID) {
        String query = "SELECT *" +
                " FROM tb_task" +
                " WHERE status = 1 AND category_id = " + categoryID +
                " ORDER BY finished_date DESC";

        return getTasksFromDB(query);
    }

    public List<CategoryModel> getAllCategories() {
        String query = "SELECT *" +
                " FROM tb_category" +
                " ORDER BY name ASC";

        return getCategoriesFromDB(query);
    }

    public List<Integer> getAllRequiredIDs(Integer requirementTaskID) {
        String query = "SELECT required_task_id" +
                " FROM tb_requirement" +
                " WHERE requirement_task_id = " + requirementTaskID;

        return getIDsFromDB(query);
    }

    public List<Integer> getAllRequirementsIDs(Integer requiredTaskID) {
        String query = "SELECT requirement_task_id" +
                " FROM tb_requirement" +
                " WHERE required_task_id = " + requiredTaskID;

        return getIDsFromDB(query);
    }

    public boolean taskCanBeFinished(Integer taskID) {
        // Conta quantas tarefas são requisito de taskID e NÃO foram concluídas
        String query = "SELECT COUNT(id) " +
                "FROM tb_task, tb_requirement " +
                "WHERE status = 0 AND id = required_task_id AND requirement_task_id = " + taskID;

        Cursor cursor = db.rawQuery(query, null);
        int qtd;

        cursor.moveToFirst();
        qtd = cursor.getInt(0);
        cursor.close();

        return qtd == 0;
    }

    public boolean taskCanBeUndo(Integer taskID) {
        // Conta quantas tarefas que precisam de taskID como requisito e ESTÃO concluídas
        String query = "SELECT COUNT(id) " +
                "FROM tb_task, tb_requirement " +
                "WHERE status = 1 AND id = requirement_task_id AND required_task_id = " + taskID;

        Cursor cursor = db.rawQuery(query, null);
        int qtd;

        cursor.moveToFirst();
        qtd = cursor.getInt(0);
        cursor.close();

        return qtd == 0;
    }

    public boolean listCanBeFinished(List<TaskModel> array) {

        for (TaskModel task : array) {
            if (!taskCanBeFinished(task.getId()))
                return false;
        }

        return true;
    }

    public boolean listCanBeUndo(List<TaskModel> array) {

        for (TaskModel task : array) {
            if (!taskCanBeUndo(task.getId()))
                return false;
        }

        return true;
    }

    TaskModel getTaskFromDB(String query) {
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            return new TaskModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6)
            );
        }

        cursor.close();

        return null;
    }

    List<TaskModel> getTasksFromDB(String query) {
        List<TaskModel> allTasksQuery = new ArrayList<>();
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
            allTasksQuery.add(task);
        }

        cursor.close();

        return allTasksQuery;
    }

    List<Integer> getIDsFromDB(String query) {
        List<Integer> requiredIDs = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Integer requiredTaskID = cursor.getInt(0);
            requiredIDs.add(requiredTaskID);
        }

        cursor.close();

        return requiredIDs;
    }

    List<CategoryModel> getCategoriesFromDB(String query) {
        List<CategoryModel> allCategoriesQuery = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            CategoryModel category = new CategoryModel(
                    cursor.getInt(0),
                    cursor.getString(1)
            );
            allCategoriesQuery.add(category);
        }

        cursor.close();

        return allCategoriesQuery;
    }

    Integer getLastID(String tbName) {
        String query = "SELECT id FROM " + tbName + " WHERE id = (SELECT MAX(id) FROM " + tbName + ")";
        Cursor cursor = db.rawQuery(query, null);
        int lastId;

        cursor.moveToFirst();
        lastId = cursor.getInt(0);
        cursor.close();

        return lastId;
    }
}
