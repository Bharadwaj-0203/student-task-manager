package com.example.studenttaskmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleEdt, descEdt, dueDateEdt;
    private Button saveBtn;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Get username from intent
        username = getIntent().getStringExtra("username");

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        titleEdt = findViewById(R.id.editTaskTitle);
        descEdt = findViewById(R.id.editTaskDesc);
        dueDateEdt = findViewById(R.id.editDueDate);
        saveBtn = findViewById(R.id.btnSaveTask);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String title = titleEdt.getText().toString().trim();
        String desc = descEdt.getText().toString().trim();
        String dueDate = dueDateEdt.getText().toString().trim();

        // Validate input
        if(title.isEmpty() || desc.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get teacher ID
            int teacherId = dbHelper.getUserId(username);

            if(teacherId != -1) {
                // Add task to database
                long result = dbHelper.addTask(title, desc, dueDate, teacherId);

                if(result != -1) {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error: Teacher ID not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null) {
            dbHelper.close();
        }
    }
}