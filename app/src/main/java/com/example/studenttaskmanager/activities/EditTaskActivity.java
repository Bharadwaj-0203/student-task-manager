package com.example.studenttaskmanager.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;

public class EditTaskActivity extends AppCompatActivity {

    private EditText titleEdt, descEdt, dueDateEdt;
    private Button updateBtn, deleteBtn;
    private DatabaseHelper dbHelper;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskId = getIntent().getIntExtra("task_id", -1);
        if(taskId == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        titleEdt = findViewById(R.id.editTaskTitle);
        descEdt = findViewById(R.id.editTaskDesc);
        dueDateEdt = findViewById(R.id.editDueDate);
        updateBtn = findViewById(R.id.btnUpdateTask);
        deleteBtn = findViewById(R.id.btnDeleteTask);

        loadTaskDetails();

        updateBtn.setOnClickListener(v -> updateTask());
        deleteBtn.setOnClickListener(v -> confirmDelete());
    }

    private void loadTaskDetails() {
        Cursor cursor = dbHelper.getTaskDetails(taskId);
        if(cursor.moveToFirst()) {
            titleEdt.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TITLE)));
            descEdt.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DESC)));
            dueDateEdt.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
        }
        cursor.close();
    }

    private void updateTask() {
        String title = titleEdt.getText().toString().trim();
        String desc = descEdt.getText().toString().trim();
        String dueDate = dueDateEdt.getText().toString().trim();

        if(title.isEmpty() || desc.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(dbHelper.updateTask(taskId, title, desc, dueDate)) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> deleteTask())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTask() {
        if(dbHelper.deleteTask(taskId)) {
            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }
}
