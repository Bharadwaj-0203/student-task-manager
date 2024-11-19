package com.example.studenttaskmanager.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import com.example.studenttaskmanager.database.DatabaseHelper;

public class DatabaseBackup {
    public static void backupDatabase(Context context) {
        try {
            File currentDB = context.getDatabasePath(DatabaseHelper.DB_NAME);
            File backupDB = new File(context.getExternalFilesDir(null),
                    "backup_" + System.currentTimeMillis() + ".db");

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}