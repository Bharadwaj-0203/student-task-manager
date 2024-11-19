package com.example.studenttaskmanager.models;

public class User {
    private int id;
    private String username;
    private String userType;

    public User(int id, String username, String userType) {
        this.id = id;
        this.username = username;
        this.userType = userType;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getUserType() { return userType; }
}