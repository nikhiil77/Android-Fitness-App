package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.androidfitness.activities.FemaleHomeActivity;
import com.example.androidfitness.activities.MaleHomeActivity;

public class ProfileSetupActivity extends AppCompatActivity {

    private String username;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        dbHandler = new DBhandler(this);
        username = getIntent().getStringExtra("USERNAME");
        Log.d("ProfileSetupActivity", "Username received: " + username);

        if (username == null) {
            Log.e("ProfileSetupActivity", "Username is null. Redirecting to login screen.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Check if gender and body type are already set
        String gender = dbHandler.getUserGender(username);
        String bodyType = dbHandler.getUserBodyType(username);

        if (savedInstanceState == null) {
            if (gender == null || gender.isEmpty()) {
                // Gender is not set, load GenderFragment
                loadFragment(new GenderFragment());
            } else if (bodyType == null || bodyType.isEmpty()) {
                // Gender is set but body type is not, load BodyTypeFragment
                loadFragment(new BodyTypeFragment());
            } else {
                // Both gender and body type are set, redirect to home activity
                redirectToHomeActivity(gender);
            }
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.profile_setup_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public String getUsername() {
        return username;
    }

    public void setUserGender(String gender) {
        dbHandler.updateGender(username, gender);
        Log.d("ProfileSetupActivity", "Gender updated to: " + gender);

        // Check if body type is already set
        String bodyType = dbHandler.getUserBodyType(username);
        if (bodyType == null || bodyType.isEmpty()) {
            // Body type is not set, load BodyTypeFragment
            loadFragment(new BodyTypeFragment());
        } else {
            // Both gender and body type are set, redirect to home activity
            redirectToHomeActivity(gender);
        }
    }

    public void setUserBodyType(String bodyType) {
        dbHandler.updateBodyType(username, bodyType);
        Log.d("ProfileSetupActivity", "Body type updated to: " + bodyType);

        // Check if gender is already set
        String gender = dbHandler.getUserGender(username);
        if (gender != null && !gender.isEmpty()) {
            // Both gender and body type are set, redirect to home activity
            redirectToHomeActivity(gender);
        }
    }

    public void redirectToHomeActivity(String gender) {
        // Mark the user as logged in
        dbHandler.markUserAsLoggedIn(username);
        Log.d("ProfileSetupActivity", "User marked as logged in: " + username);

        Intent intent;
        if ("Male".equalsIgnoreCase(gender)) {
            intent = new Intent(ProfileSetupActivity.this, MaleHomeActivity.class);
        } else {
            intent = new Intent(ProfileSetupActivity.this, FemaleHomeActivity.class);
        }

        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }
}