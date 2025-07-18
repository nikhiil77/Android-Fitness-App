package com.example.androidfitness.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfitness.DBhandler;
import com.example.androidfitness.R;
import com.example.androidfitness.adapters.ExerciseAdapter;
import com.example.androidfitness.models.Exercise;

import java.util.ArrayList;

public class ExerciseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> exerciseList;
    private DBhandler dbHandler;
    private String workoutCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        recyclerView = findViewById(R.id.exerciseRecyclerView);
        dbHandler = DBhandler.getInstance(this);

        workoutCategory = getIntent().getStringExtra("workout_category");
        if (workoutCategory == null) {
            Toast.makeText(this, "Invalid Workout Category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExercises(workoutCategory);
    }

    private void loadExercises(String category) {
        exerciseList = new ArrayList<>();
        Cursor cursor = dbHandler.getExercisesForCategory(category);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("exercise_image"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String videoUrl = cursor.getString(cursor.getColumnIndexOrThrow("video_url"));

                Exercise exercise = new Exercise(id, name, image, category, description, videoUrl);
                exerciseList.add(exercise);
            }
            cursor.close();
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        exerciseAdapter = new ExerciseAdapter(this, exerciseList);
        recyclerView.setAdapter(exerciseAdapter);
    }
}
