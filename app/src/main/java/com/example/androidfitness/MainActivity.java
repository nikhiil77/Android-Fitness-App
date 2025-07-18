package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidfitness.activities.FemaleHomeActivity;
import com.example.androidfitness.activities.MaleHomeActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button loginButton, registerButton, adminLoginButton;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHandler = new DBhandler(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        adminLoginButton = findViewById(R.id.adminLoginButton);


        loginButton.setOnClickListener(view -> loginUser());

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        adminLoginButton.setOnClickListener(view -> {
            // Start AdminLoginActivity when admin login button is clicked
            Intent adminIntent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(adminIntent);
        });
    }

    private void loginUser() {
        String usernameOrEmail = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username/email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHandler.isUserValid(usernameOrEmail, password)) {
            // Mark the user as logged in
            dbHandler.markUserAsLoggedIn(usernameOrEmail);

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            // Get the username from the database (in case the user logged in with email)
            String username = dbHandler.getLoggedInUsername();

            // Check if profile setup is complete
            if (dbHandler.isProfileComplete(username)) {
                // Profile setup is complete, redirect to the appropriate home activity
                String gender = dbHandler.getUserGender(username);

                Intent intent;
                if ("Male".equalsIgnoreCase(gender)) {
                    intent = new Intent(MainActivity.this, MaleHomeActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, FemaleHomeActivity.class);
                }

                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
            } else {
                // Profile setup is incomplete, redirect to ProfileSetupActivity
                Toast.makeText(this, "Please complete your profile setup", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ProfileSetupActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

}