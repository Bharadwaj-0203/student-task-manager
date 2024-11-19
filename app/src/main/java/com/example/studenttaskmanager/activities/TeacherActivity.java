package com.example.studenttaskmanager.activities;

import static com.example.studenttaskmanager.database.DatabaseHelper.COL_DATE;
import static com.example.studenttaskmanager.database.DatabaseHelper.COL_DESC;
import static com.example.studenttaskmanager.database.DatabaseHelper.COL_ID;
import static com.example.studenttaskmanager.database.DatabaseHelper.COL_TITLE;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;

public class TeacherActivity extends AppCompatActivity {

    private Button addTaskBtn;

    private Button logoutBtn;
    private ListView taskList;
    private TextView welcomeText;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // get username from intent
        username = getIntent().getStringExtra("username");

        // initialize views
        addTaskBtn = findViewById(R.id.btnAddTask);
        taskList = findViewById(R.id.listViewTasks);
        welcomeText = findViewById(R.id.welcomeText);
        dbHelper = new DatabaseHelper(this);

        // set welcome text
        welcomeText.setText("Welcome " + username);

        // add task button click
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, AddTaskActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        logoutBtn = findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear any stored data if needed
                Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // load tasks
        loadTasks();
        setupTaskList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void setupTaskList() {
        taskList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(TeacherActivity.this, EditTaskActivity.class);
            intent.putExtra("task_id", (int)id);
            startActivity(intent);
        });
    }

    private void loadTasks() {
        int teacherId = dbHelper.getUserId(username);
        Cursor cursor = dbHelper.getAllTasks(teacherId);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.task_list_item,
                cursor,
                new String[]{COL_TITLE, COL_DESC, COL_DATE},
                new int[]{R.id.taskTitle, R.id.taskDesc, R.id.taskDueDate},
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                TextView statusText = view.findViewById(R.id.taskStatus);

                // Get submissions with student names
                Cursor submissions = dbHelper.getSubmissions(taskId);
                StringBuilder submissionText = new StringBuilder();
                submissionText.append("Submissions (").append(submissions.getCount()).append("): \n");

                while(submissions.moveToNext()) {
                    String studentName = submissions.getString(
                            submissions.getColumnIndexOrThrow("student_name"));
                    submissionText.append("- ").append(studentName).append("\n");
                }

                statusText.setText(submissionText.toString());
                submissions.close();
            }
        };

        taskList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null) {
            dbHelper.close();
        }
    }
}