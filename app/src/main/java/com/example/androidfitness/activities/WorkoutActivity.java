package com.example.androidfitness.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfitness.DBhandler;
import com.example.androidfitness.R;
import com.example.androidfitness.adapters.ExerciseAdapter;
import com.example.androidfitness.models.Exercise;

import java.util.ArrayList;

public class WorkoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exerciseList;
    private TextView tvCategory;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        recyclerView = findViewById(R.id.recycler_exercises);
        tvCategory = findViewById(R.id.tv_category);
        dbHandler = DBhandler.getInstance(this);

        // Get the selected workout category from Intent
        String category = getIntent().getStringExtra("workoutCategory");
        if (category == null) {
            Toast.makeText(this, "No workout category selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCategory.setText(category + " Workouts");

        // Get exercises for the selected category based on user gender
        exerciseList = getExercisesForCategory(category);

        // Setup RecyclerView
        adapter = new ExerciseAdapter(this, exerciseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Exercise> getExercisesForCategory(String category) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        Cursor cursor;

        // Get user gender from database
        String username = dbHandler.getLoggedInUsername();
        String gender = dbHandler.getUserGender(username);

        // Fetch exercises based on gender
        if (gender != null && gender.equalsIgnoreCase("female")) {
            cursor = dbHandler.getExercisesForCategoryAndGender(category, "female");
        } else {
            // Default to male/unisex exercises
            cursor = dbHandler.getExercisesForCategory(category);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("exercise_image"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("exercise_description"));
                String videoUrl = cursor.getString(cursor.getColumnIndexOrThrow("exercise_video_url"));

                exercises.add(new Exercise(id, name, image, category, description, videoUrl));
            }
            cursor.close();
        }
        return exercises;
    }
}