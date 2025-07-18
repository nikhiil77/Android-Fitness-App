package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.androidfitness.activities.FemaleHomeActivity;
import com.example.androidfitness.activities.MaleHomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private DBhandler dbHandler;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHandler = new DBhandler(this);
        tvUsername = findViewById(R.id.Username);

        // Fetch username from database
        username = dbHandler.getLoggedInUsername();
        if (username != null) {
            tvUsername.setText(username); // Set the username in the TextView
        } else {
            tvUsername.setText("User not found"); // Handle the case where no user is logged in
            Toast.makeText(this, "No user logged in. Redirecting to login screen.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class)); // Redirect to the login screen
            finish(); // Close the current activity
        }

        // Set up buttons
        findViewById(R.id.editProfile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.viewProfile).setOnClickListener(v -> startActivity(new Intent(this, ViewProfileActivity.class)));
        findViewById(R.id.contactus).setOnClickListener(v -> startActivity(new Intent(this, ContactusActivity.class)));
        findViewById(R.id.logout).setOnClickListener(v -> {
            dbHandler.logoutUser(); // Mark the user as logged out
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class)); // Redirect to the main activity
            finish(); // Close the current activity
        });

        // Handle bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Fetch the user's gender from the database
                String gender = dbHandler.getUserGender(username);
                if (gender != null) {
                    if (gender.equalsIgnoreCase("male")) {
                        startActivity(new Intent(this, MaleHomeActivity.class));
                    } else if (gender.equalsIgnoreCase("female")) {
                        startActivity(new Intent(this, FemaleHomeActivity.class));
                    }
                } else {
                    // If gender is not set, redirect to the profile setup screen
                    Toast.makeText(this, "Please complete your profile setup", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ProfileSetupActivity.class));
                }
                return true;
            } else if (item.getItemId() == R.id.nav_bmi) {
                startActivity(new Intent(this, BMIActivity.class)); // Redirect to BMI activity
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                // Already in the profile activity, so do nothing
                return true;
            }
            return false;
        });

        // Set the selected item in the bottom navigation bar
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}