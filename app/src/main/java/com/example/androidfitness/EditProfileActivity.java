package com.example.androidfitness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etAge, etGender, etHeight, etWeight, etBodyType;
    private Button btnUpdate;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize database handler
        dbHandler = DBhandler.getInstance(this);

        // Initialize EditText fields
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etBodyType = findViewById(R.id.etBodyType);

        // Initialize Update Button
        btnUpdate = findViewById(R.id.btnUpdate);

        // Fetch and populate current user data
        loadUserData();

        // Handle Update Button Click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });
    }

    private void loadUserData() {
        // Get the logged-in user's username
        String username = dbHandler.getLoggedInUsername();

        if (username != null) {
            // Fetch user details from DBHandler
            String email = dbHandler.getUserEmail(username);
            String age = dbHandler.getUserAge(username);
            String gender = dbHandler.getUserGender(username);
            String bodyType = dbHandler.getUserBodyType(username);
            double[] heightWeight = dbHandler.getHeightWeight();

            // Set the fetched data to EditText fields
            etUsername.setText(username);
            etEmail.setText(email != null ? email : "");
            etAge.setText(age != null ? age : "");
            etGender.setText(gender != null ? gender : "");
            etBodyType.setText(bodyType != null ? bodyType : "");
            etHeight.setText(heightWeight[0] > 0 ? String.valueOf(heightWeight[0]) : "");
            etWeight.setText(heightWeight[1] > 0 ? String.valueOf(heightWeight[1]) : "");
        }
    }

    private void updateUserData() {
        // Get the logged-in user's username
        String oldUsername = dbHandler.getLoggedInUsername();

        if (oldUsername != null) {
            // Retrieve updated data from EditText fields
            String newUsername = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String bodyType = etBodyType.getText().toString().trim();
            double height = etHeight.getText().toString().isEmpty() ? 0 : Double.parseDouble(etHeight.getText().toString().trim());
            double weight = etWeight.getText().toString().isEmpty() ? 0 : Double.parseDouble(etWeight.getText().toString().trim());

            // Update the database
            boolean isUpdated = dbHandler.updateUserProfile(oldUsername, newUsername, email, age, gender, height, weight, bodyType);

            if (isUpdated) {
                // Mark the user as logged in with the new username
                dbHandler.markUserAsLoggedIn(newUsername);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No logged-in user found!", Toast.LENGTH_SHORT).show();
        }
    }
}