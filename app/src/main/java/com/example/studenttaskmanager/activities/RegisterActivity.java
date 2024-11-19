package com.example.studenttaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studenttaskmanager.R;
import com.example.studenttaskmanager.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEdt, passwordEdt, nameEdt;
    private RadioGroup typeRadioGroup;
    private Button registerBtn, backBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Initialize all views
        usernameEdt = findViewById(R.id.editRegUsername);
        passwordEdt = findViewById(R.id.editRegPassword);
        nameEdt = findViewById(R.id.editRegName);  // New name field
        typeRadioGroup = findViewById(R.id.radioGroupType);
        registerBtn = findViewById(R.id.btnRegister);
        backBtn = findViewById(R.id.btnBackToLogin);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String username = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        String fullName = nameEdt.getText().toString().trim();  // Get full name
        RadioButton selectedType = findViewById(typeRadioGroup.getCheckedRadioButtonId());
        String userType = selectedType.getText().toString().toLowerCase();

        // Validate all fields
        if(username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(dbHelper.isUserExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register with full name
        if(dbHelper.registerUser(username, password, fullName, userType)) {
            dbHelper.backupDatabase();
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
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