package com.example.androidfitness;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvAge, tvGender, tvHeight, tvWeight, tvBodyType;
    private DBhandler dbHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize database handler
        dbHandler = DBhandler.getInstance(this);

        // Initialize TextViews
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvAge = findViewById(R.id.tvAge);
        tvGender = findViewById(R.id.tvGender);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvBodyType = findViewById(R.id.tvBodyType);

        // Fetch and display user data
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data every time the activity is opened
        loadUserData();
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

            // Debug logs to verify fetched data
            Log.d("ViewProfileActivity", "Username: " + username);
            Log.d("ViewProfileActivity", "Email: " + email);
            Log.d("ViewProfileActivity", "Age: " + age);
            Log.d("ViewProfileActivity", "Gender: " + gender);
            Log.d("ViewProfileActivity", "Body Type: " + bodyType);
            Log.d("ViewProfileActivity", "Height: " + heightWeight[0]);
            Log.d("ViewProfileActivity", "Weight: " + heightWeight[1]);

            // Set the fetched data to TextViews
            tvUsername.setText(username != null ? username : "N/A");
            tvEmail.setText(email != null ? email : "N/A");
            tvAge.setText(age != null ? age : "N/A");
            tvGender.setText(gender != null ? gender : "N/A");
            tvBodyType.setText(bodyType != null ? bodyType : "N/A");
            tvHeight.setText(heightWeight[0] > 0 ? heightWeight[0] + " cm" : "N/A");
            tvWeight.setText(heightWeight[1] > 0 ? heightWeight[1] + " kg" : "N/A");
        } else {
            // Handle case where no logged-in user is found
            Log.e("ViewProfileActivity", "No logged-in user found.");
            tvUsername.setText("N/A");
            tvEmail.setText("N/A");
            tvAge.setText("N/A");
            tvGender.setText("N/A");
            tvBodyType.setText("N/A");
            tvHeight.setText("N/A");
            tvWeight.setText("N/A");
        }
    }
}