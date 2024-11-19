package com.example.studenttaskmanager.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;
import android.content.Intent;


public class StudentActivity extends AppCompatActivity {

    private ListView taskList;
    private TextView welcomeText;
    private Button logoutBtn;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        username = getIntent().getStringExtra("username");
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        taskList = findViewById(R.id.listViewStudentTasks);
        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.btnLogout);

        welcomeText.setText("Welcome " + username);

        // Set up logout button
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login screen
                Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        loadTasks();
    }

    private void loadTasks() {
        Cursor cursor = dbHelper.getAllTasksForStudent();

        String[] from = new String[]{
                DatabaseHelper.COL_TITLE,
                DatabaseHelper.COL_DESC,
                DatabaseHelper.COL_DATE
        };

        int[] to = new int[]{
                R.id.studentTaskTitle,
                R.id.studentTaskDesc,
                R.id.studentTaskDueDate
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                StudentActivity.this,
                R.layout.student_task_list_item,
                cursor,
                from,
                to,
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                try {
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                    int studentId = dbHelper.getUserId(username);

                    // Get and set teacher name
                    String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"));
                    TextView teacherNameView = view.findViewById(R.id.teacherName);
                    teacherNameView.setText("Assigned by: " + teacherName);

                    Button submitButton = view.findViewById(R.id.btnSubmitTask);

                    // Check if task is already submitted
                    if(dbHelper.hasStudentSubmitted(taskId, studentId)) {
                        submitButton.setText("Submitted");
                        submitButton.setEnabled(false);
                        submitButton.setAlpha(0.5f);
                    } else {
                        submitButton.setText("Submit");
                        submitButton.setEnabled(true);
                        submitButton.setAlpha(1.0f);

                        submitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(dbHelper.submitTask(taskId, studentId)) {
                                    Toast.makeText(StudentActivity.this,
                                            "Task submitted successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    // Update button state immediately
                                    submitButton.setText("Submitted");
                                    submitButton.setEnabled(false);
                                    submitButton.setAlpha(0.5f);
                                    // Refresh the list
                                    loadTasks();
                                } else {
                                    Toast.makeText(StudentActivity.this,
                                            "Failed to submit task",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        taskList.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();  // Refresh tasks when activity resumes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null) {
            dbHelper.close();
        }
    }
}