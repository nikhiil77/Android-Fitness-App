package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.example.androidfitness.activities.MaleHomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.androidfitness.activities.FemaleHomeActivity;

public class BMIActivity extends AppCompatActivity {

    private EditText etHeight, etWeight;
    private Button btnCalculate;
    private LottieAnimationView loadingAnimation;
    private DBhandler dbHandler;
    private BottomNavigationView bottomNavigationView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        // Initialize database handler
        dbHandler = new DBhandler(this);

        // Fetch the logged-in username
        username = dbHandler.getLoggedInUsername();

        // Initialize UI components
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up navigation item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Navigate to MaleHomeActivity or FemaleHomeActivity based on gender
                if (username != null) {
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
                }
                return true;
            } else if (itemId == R.id.nav_bmi) {
                // Already in the BMI activity, so do nothing
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to ProfileActivity
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Set the selected item in the bottom navigation bar
        bottomNavigationView.setSelectedItemId(R.id.nav_bmi);

        // Load stored height & weight when activity starts
        loadStoredHeightWeight();

        // Set text change listeners
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields(); // Enable/disable the button based on input
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);

        // Set click listener for the Calculate button
        btnCalculate.setOnClickListener(v -> {
            // Show loading animation
            loadingAnimation.setVisibility(View.VISIBLE);
            loadingAnimation.playAnimation();

            // Simulate a delay for processing
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                calculateBMI(); // Perform BMI calculation
                loadingAnimation.setVisibility(View.GONE); // Hide loading animation
            }, 2000);
        });
    }

    /**
     * Load stored height and weight for the logged-in user.
     */
    private void loadStoredHeightWeight() {
        if (dbHandler == null) {
            Log.e("BMIActivity", "DBHandler is null. Cannot load height and weight.");
            return;
        }

        double[] heightWeight = dbHandler.getHeightWeight();

        if (heightWeight == null || heightWeight.length < 2) {
            Log.e("BMIActivity", "Failed to load height and weight: Invalid or missing data.");
            return;
        }

        double height = heightWeight[0];
        double weight = heightWeight[1];

        // Ensure database values are correctly fetched
        Log.d("BMIActivity", "Fetched from DB - Height: " + height + ", Weight: " + weight);

        // Set height field
        etHeight.setText(height > 0 ? String.format("%.2f", height) : "");
        etWeight.setText(weight > 0 ? String.format("%.2f", weight) : "");

        checkFields(); // Ensure button state updates correctly
    }

    private void checkFields() {
        boolean allFieldsFilled = !etHeight.getText().toString().trim().isEmpty() &&
                !etWeight.getText().toString().trim().isEmpty();

        btnCalculate.setEnabled(allFieldsFilled);
        int color = allFieldsFilled ? ContextCompat.getColor(this, R.color.purple_700)
                : ContextCompat.getColor(this, R.color.gray);
        btnCalculate.setBackgroundColor(color);
    }

    private void calculateBMI() {
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        Log.d("BMIActivity", "Height input: " + heightStr + ", Weight input: " + weightStr);

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter both height and weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(this, "Height and weight must be greater than 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate BMI
            double bmi = weight / ((height / 100) * (height / 100));
            Log.d("BMIActivity", "BMI Calculated: " + bmi);

            // Save height and weight in the database only when recalculating BMI
            if (dbHandler != null) {
                dbHandler.addOrUpdateHeightWeight(height, weight);
                Log.d("BMIActivity", "Height and weight stored in database successfully.");
            } else {
                Toast.makeText(this, "Database error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to BMIResultActivity with BMI value
            Intent intent = new Intent(BMIActivity.this, BMIResultActivity.class);
            intent.putExtra("BMI_VALUE", bmi);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter numbers only.", Toast.LENGTH_SHORT).show();
            Log.e("BMIActivity", "NumberFormatException: " + e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e("BMIActivity", "Unexpected error: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHandler != null) {
            dbHandler.close();
            Log.d("DBHandler", "Database connection closed in onDestroy()");
        }
    }
}