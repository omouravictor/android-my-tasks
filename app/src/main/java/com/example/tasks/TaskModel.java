package com.example.tasks;

public class TaskModel {

    private int id;
    private String name;
    private String slaDate;

    public TaskModel() {
    }

    public TaskModel(int id, String name, String slaDate) {
        this.id = id;
        this.name = name;
        this.slaDate = slaDate;
    }

    public TaskModel(String name, String slaDate) {
        this.name = name;
        this.slaDate = slaDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
