package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminUsername, etAdminPassword;
    private Button btnAdminLogin;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        dbHandler = DBhandler.getInstance(this);
        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnAdminLogin.setOnClickListener(v -> handleAdminLogin());
    }

    private void handleAdminLogin() {
        String username = etAdminUsername.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHandler.validateAdmin(username, password)) {
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
            navigateToAdminInterface();
        } else {
            Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToAdminInterface() {
        // Replace AdminDashboardActivity with your actual admin interface class
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}