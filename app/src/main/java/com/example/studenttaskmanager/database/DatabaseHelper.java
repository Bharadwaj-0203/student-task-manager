package com.example.studenttaskmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.SharedPreferences;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // database name and version

    public static final String DB_NAME = "my_college_db";
    private static final int DB_VERSION = 1;

    // table names
    public static final String USER_TABLE = "users";
    public static final String TASK_TABLE = "tasks";
    public static final String SUBMIT_TABLE = "submissions";

    // common columns
    public static final String COL_ID = "_id";  // This must be "_id" for CursorAdapter

    // user table columns
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_TYPE = "usertype";

    public static final String COL_FULL_NAME = "full_name";

    // task table columns
    public static final String COL_TITLE = "task_title";
    public static final String COL_DESC = "task_desc";
    public static final String COL_DATE = "due_date";
    public static final String COL_TEACHER = "teacher_id";

    // submission table columns
    public static final String COL_TASK = "task_id";
    public static final String COL_STUDENT = "student_id";
    public static final String COL_STATUS = "status";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create users table
        String createUsers = "CREATE TABLE " + USER_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT, "
                + COL_FULL_NAME + " TEXT, "
                + COL_TYPE + " TEXT)";

        // create tasks table
        String createTasks = "CREATE TABLE " + TASK_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_TEACHER + " INTEGER, "
                + "FOREIGN KEY(" + COL_TEACHER + ") REFERENCES " + USER_TABLE + "(" + COL_ID + "))";

        // create submissions table
        String createSubmit = "CREATE TABLE " + SUBMIT_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TASK + " INTEGER, "
                + COL_STUDENT + " INTEGER, "
                + COL_STATUS + " TEXT, "
                + "FOREIGN KEY(" + COL_TASK + ") REFERENCES " + TASK_TABLE + "(" + COL_ID + "), "
                + "FOREIGN KEY(" + COL_STUDENT + ") REFERENCES " + USER_TABLE + "(" + COL_ID + "), "
                + "UNIQUE(" + COL_TASK + "," + COL_STUDENT + "))";

        // execute queries
        db.execSQL(createUsers);
        db.execSQL(createTasks);
        db.execSQL(createSubmit);
    }

    public boolean hasStudentSubmitted(int taskId, int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_TASK + "=? AND " + COL_STUDENT + "=?";
        String[] args = {String.valueOf(taskId), String.valueOf(studentId)};

        Cursor cursor = db.query(SUBMIT_TABLE,
                null,
                selection,
                args,
                null,
                null,
                null);

        boolean submitted = cursor.getCount() > 0;
        cursor.close();
        return submitted;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SUBMIT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    // Check login credentials
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ID};
        String selection = COL_USERNAME + "=? AND " + COL_PASSWORD + "=?";
        String[] args = {username, password};

        Cursor cursor = db.query(USER_TABLE, columns, selection, args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Get user type (teacher/student)
    public String getUserType(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_TYPE};
        String selection = COL_USERNAME + "=?";
        String[] args = {username};

        Cursor cursor = db.query(USER_TABLE, columns, selection, args, null, null, null);
        String type = "";
        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(COL_TYPE);  // Changed this line
            type = cursor.getString(columnIndex);
        }
        cursor.close();
        return type;
    }

    // Get user ID
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ID};
        String selection = COL_USERNAME + "=?";
        String[] args = {username};

        Cursor cursor = db.query(USER_TABLE, columns, selection, args, null, null, null);
        int id = -1;
        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(COL_ID);  // Changed this line
            id = cursor.getInt(columnIndex);
        }
        cursor.close();
        return id;
    }

    // Add new task
    public long addTask(String title, String desc, String dueDate, int teacherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESC, desc);
        values.put(COL_DATE, dueDate);
        values.put(COL_TEACHER, teacherId);
        return db.insert(TASK_TABLE, null, values);
    }

    // Get all tasks for a teacher
    public Cursor getAllTasks(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TASK_TABLE, null,
                COL_TEACHER + "=?",
                new String[]{String.valueOf(teacherId)},
                null, null, null);
    }

    // Get all tasks for student view
    public Cursor getAllTasksForStudent() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.*, u." + COL_FULL_NAME + " as teacher_name FROM " +
                TASK_TABLE + " t JOIN " + USER_TABLE + " u ON t." +
                COL_TEACHER + " = u." + COL_ID;
        return db.rawQuery(query, null);
    }

    // Get task details
    public Cursor getTaskDetails(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TASK_TABLE, null,
                COL_ID + "=?",
                new String[]{String.valueOf(taskId)},
                null, null, null);
    }

    // Submit a task
    public boolean submitTask(int taskId, int studentId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            // Check if already submitted
            if(hasStudentSubmitted(taskId, studentId)) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(COL_TASK, taskId);
            values.put(COL_STUDENT, studentId);
            values.put(COL_STATUS, "Submitted");
            long result = db.insert(SUBMIT_TABLE, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if task is submitted
    public boolean isTaskSubmitted(int taskId, int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_TASK + "=? AND " + COL_STUDENT + "=?";
        String[] args = {String.valueOf(taskId), String.valueOf(studentId)};
        Cursor cursor = db.query(SUBMIT_TABLE, null, selection, args, null, null, null);
        boolean submitted = cursor.getCount() > 0;
        cursor.close();
        return submitted;
    }

    // Get submissions for a task
    public Cursor getSubmissions(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.*, u." + COL_FULL_NAME + " as student_name FROM " + SUBMIT_TABLE + " s " +
                "JOIN " + USER_TABLE + " u ON s." + COL_STUDENT + " = u." + COL_ID +
                " WHERE s." + COL_TASK + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(taskId)});
    }

    public String getTeacherName(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_FULL_NAME};
        String selection = COL_ID + "=?";
        String[] selectionArgs = {String.valueOf(teacherId)};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);
        String teacherName = "";
        if(cursor.moveToFirst()) {
            teacherName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME));
        }
        cursor.close();
        return teacherName;
    }

    // Update task
    public boolean updateTask(int taskId, String title, String desc, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESC, desc);
        values.put(COL_DATE, dueDate);
        int result = db.update(TASK_TABLE, values,
                COL_ID + "=?",
                new String[]{String.valueOf(taskId)});
        return result > 0;
    }

    // Delete task
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First delete submissions
        db.delete(SUBMIT_TABLE, COL_TASK + "=?",
                new String[]{String.valueOf(taskId)});
        // Then delete task
        int result = db.delete(TASK_TABLE, COL_ID + "=?",
                new String[]{String.valueOf(taskId)});
        return result > 0;
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ID};
        String selection = COL_USERNAME + "=?";
        String[] args = {username};
        Cursor cursor = db.query(USER_TABLE, columns, selection, args, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean registerUser(String username, String password, String fullName, String userType) {
        if(isUserExists(username)) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_TYPE, userType);
        long result = db.insert(USER_TABLE, null, values);
        return result != -1;
    }

    public void backupDatabase() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Get all users
            Cursor cursor = db.query(USER_TABLE,
                    null,
                    null,
                    null,
                    null, null, null);

            SharedPreferences prefs = context.getSharedPreferences("DatabasePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            while(cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME));
                String userType = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));

                // Store each user's data
                editor.putString("user_" + username + "_pwd", password);
                editor.putString("user_" + username + "_name", fullName);
                editor.putString("user_" + username + "_type", userType);
            }

            editor.apply();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreBackup() {
        try {
            SharedPreferences prefs = context.getSharedPreferences("DatabasePrefs", Context.MODE_PRIVATE);
            SQLiteDatabase db = this.getWritableDatabase();

            // Get all stored preferences
            Map<String, ?> allPrefs = prefs.getAll();

            for(Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                String key = entry.getKey();
                if(key.startsWith("user_") && key.endsWith("_pwd")) {
                    String username = key.substring(5, key.length() - 4);
                    String password = prefs.getString(key, "");
                    String fullName = prefs.getString("user_" + username + "_name", "");
                    String userType = prefs.getString("user_" + username + "_type", "");

                    // Only insert if user doesn't exist
                    if(!isUserExists(username)) {
                        ContentValues values = new ContentValues();
                        values.put(COL_USERNAME, username);
                        values.put(COL_PASSWORD, password);
                        values.put(COL_FULL_NAME, fullName);
                        values.put(COL_TYPE, userType);
                        db.insert(USER_TABLE, null, values);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}