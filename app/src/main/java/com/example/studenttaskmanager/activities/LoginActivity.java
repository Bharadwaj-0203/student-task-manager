package com.example.studenttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;
import com.example.studenttaskmanager.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEdt, passwordEdt;

    private Button registerBtn;
    Button loginBtn;
    DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize database and session manager
        dbHelper = new DatabaseHelper(this);
        dbHelper.restoreBackup();
        sessionManager = new SessionManager(this);

        // get views
        usernameEdt = findViewById(R.id.editUsername);
        passwordEdt = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);

        // login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();

                // basic validation
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check login
                if (dbHelper.checkLogin(username, password)) {
                    String userType = dbHelper.getUserType(username);

                    // create a session
                    sessionManager.createLoginSession(username, userType);

                    if (userType.equals("teacher")) {
                        Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerBtn = findViewById(R.id.btnGoToRegister);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}