package com.example.androidfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfitness.activities.AddWorkoutActivity;
import com.example.androidfitness.activities.DeleteWorkoutActivity;

public class WorkoutListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        // Add Workout Button
        Button addWorkoutBtn = findViewById(R.id.add_workout_btn);
        addWorkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutListActivity.this, AddWorkoutActivity.class);
            startActivity(intent);
        });

        // Delete Workout Button
        Button deleteWorkoutBtn = findViewById(R.id.delete_workout_btn);
        deleteWorkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutListActivity.this, DeleteWorkoutActivity.class);
            startActivity(intent);
        });
    }
}