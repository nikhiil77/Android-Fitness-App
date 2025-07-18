package com.example.androidfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    LinearLayout workoutContainer, userContainer;
    Button logoutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        workoutContainer = findViewById(R.id.workoutListContainer);
        userContainer = findViewById(R.id.userListContainer);
        logoutButton = findViewById(R.id.logoutButton);

        workoutContainer.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, WorkoutListActivity.class);
            startActivity(intent);
        });

        userContainer.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserListActivity.class);
            startActivity(intent);
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(AdminDashboardActivity.this, AdminLoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });
    }
}