package com.example.studenttaskmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataValidator {
    public static boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            Date currentDate = new Date();
            return parsedDate != null && parsedDate.after(currentDate);
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidTaskInput(String title, String description, String dueDate) {
        return !title.isEmpty() &&
                title.length() >= 3 &&
                !description.isEmpty() &&
                description.length() >= 10 &&
                isValidDate(dueDate);
    }

    public static boolean isValidUsername(String username) {
        return username != null &&
                username.length() >= 3 &&
                username.matches("^[a-zA-Z0-9._-]{3,}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 6;
    }
}