package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etAge, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp, btnLogin;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        dbHandler = new DBhandler(this);

        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInputs(username, age, email, password, confirmPassword)) {
                boolean isInserted = dbHandler.insertUser(username, age, email, password);

                if (isInserted) {
                    // Mark the user as logged in
                    dbHandler.markUserAsLoggedIn(username);

                    Toast.makeText(SignUpActivity.this, "Sign up successful! Please complete your profile.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, ProfileSetupActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed! Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInputs(String username, String ageStr, String email, String password, String confirmPassword) {
        if (username.isEmpty()) {
            etUsername.setError("Username is required!");
            return false;
        } else if (dbHandler.isUsernameTaken(username)) {
            etUsername.setError("Username already exists!");
            return false;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 10 || age > 100) {
                etAge.setError("Enter a valid age (10-100)");
                return false;
            }
        } catch (NumberFormatException e) {
            etAge.setError("Age must be a number!");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email!");
            return false;
        } else if (dbHandler.isEmailRegistered(email)) {
            etEmail.setError("Email already registered!");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required!");
            return false;
        } else if (password.length() < 6 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            etPassword.setError("Password must be at least 6 chars, include 1 uppercase & 1 number!");
            return false;
        }

        if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("Passwords do not match!");
            return false;
        }

        return true;
    }
}