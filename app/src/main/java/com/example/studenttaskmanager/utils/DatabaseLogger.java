package com.example.studenttaskmanager.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseLogger {
    private static final String LOG_TABLE = "data_logs";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static final String CREATE_LOG_TABLE =
            "CREATE TABLE " + LOG_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "operation TEXT, " +
                    "table_name TEXT, " +
                    "record_id INTEGER, " +
                    "timestamp TEXT, " +
                    "user TEXT)";

    public static void logDatabaseChange(SQLiteDatabase db, String operation, String table, long recordId, String username) {
        ContentValues values = new ContentValues();
        values.put("operation", operation);
        values.put("table_name", table);
        values.put("record_id", recordId);
        values.put("timestamp", sdf.format(new Date()));
        values.put("user", username);
        db.insert(LOG_TABLE, null, values);
    }
}