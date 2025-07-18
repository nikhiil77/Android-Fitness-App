package com.example.androidfitness.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfitness.DBhandler;
import com.example.androidfitness.R;

public class DeleteWorkoutActivity extends AppCompatActivity {

    private DBhandler dbHandler;
    private LinearLayout exerciseListContainer;  // Changed from workoutListContainer
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_exercise);

        dbHandler = new DBhandler(this);
        exerciseListContainer = findViewById(R.id.exercise_list);  // Match XML ID
        searchBar = findViewById(R.id.search_bar);

        loadWorkouts();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterWorkouts(s.toString());
            }
        });
    }

    private void loadWorkouts() {
        exerciseListContainer.removeAllViews();
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        // Define the columns you want to query
        String[] columns = {
                DBhandler.COL_EXERCISE_ID,
                DBhandler.COL_EXERCISE_NAME,
                DBhandler.COL_WORKOUT_CATEGORY
        };

        Cursor cursor = db.query(
                DBhandler.TABLE_EXERCISES,
                columns,
                null, null, null, null,
                DBhandler.COL_EXERCISE_NAME + " ASC");

        // Get column indexes first for better performance
        int idIndex = cursor.getColumnIndex(DBhandler.COL_EXERCISE_ID);
        int nameIndex = cursor.getColumnIndex(DBhandler.COL_EXERCISE_NAME);
        int categoryIndex = cursor.getColumnIndex(DBhandler.COL_WORKOUT_CATEGORY);

        // Check if indexes are valid (-1 means column not found)
        if (idIndex == -1 || nameIndex == -1 || categoryIndex == -1) {
            Toast.makeText(this, "Database columns not found", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return;
        }

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String category = cursor.getString(categoryIndex);

                addWorkoutCard(id, name, category);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error reading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            db.close();
        }
    }

    private void filterWorkouts(String query) {
        exerciseListContainer.removeAllViews();
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        String[] columns = {
                DBhandler.COL_EXERCISE_ID,
                DBhandler.COL_EXERCISE_NAME,
                DBhandler.COL_WORKOUT_CATEGORY
        };

        String selection = DBhandler.COL_EXERCISE_NAME + " LIKE ? OR " +
                DBhandler.COL_WORKOUT_CATEGORY + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(
                DBhandler.TABLE_EXERCISES,
                columns,
                selection,
                selectionArgs,
                null, null,
                DBhandler.COL_EXERCISE_NAME + " ASC");

        int idIndex = cursor.getColumnIndex(DBhandler.COL_EXERCISE_ID);
        int nameIndex = cursor.getColumnIndex(DBhandler.COL_EXERCISE_NAME);
        int categoryIndex = cursor.getColumnIndex(DBhandler.COL_WORKOUT_CATEGORY);

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String category = cursor.getString(categoryIndex);

                addWorkoutCard(id, name, category);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error filtering: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            db.close();
        }
    }
    private void addWorkoutCard(int id, String name, String category) {
        View workoutCard = LayoutInflater.from(this).inflate(R.layout.workout_card, null);

        TextView exerciseName = workoutCard.findViewById(R.id.exercise_name);
        TextView workoutCategory = workoutCard.findViewById(R.id.workout_category);
        Button deleteButton = workoutCard.findViewById(R.id.delete_button);

        exerciseName.setText(name);
        workoutCategory.setText(category);

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(id, name));

        exerciseListContainer.addView(workoutCard);  // Changed container reference
    }

    private void showDeleteConfirmationDialog(int id, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Delete " + name + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteWorkout(id))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkout(int id) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int deletedRows = db.delete(
                DBhandler.TABLE_EXERCISES,
                DBhandler.COL_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();

        if (deletedRows > 0) {
            Toast.makeText(this, "Workout deleted", Toast.LENGTH_SHORT).show();
            loadWorkouts();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }
}