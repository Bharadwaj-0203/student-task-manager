package com.example.studenttaskmanager.models;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private int createdBy;

    public Task(int id, String title, String description, String dueDate, int createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public int getCreatedBy() { return createdBy; }
}