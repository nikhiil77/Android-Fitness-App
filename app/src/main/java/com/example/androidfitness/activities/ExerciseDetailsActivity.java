package com.example.androidfitness.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfitness.DBhandler;
import com.example.androidfitness.R;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {

    private ImageView exerciseImage;
    private TextView exerciseName, exerciseDescription;
    private Spinner setsSpinner, repsSpinner, weightSpinner;
    private Button btnStart;
    private DBhandler dbHandler;

    // Timer popup variables
    private PopupWindow timerPopup;
    private TextView tvTimer, tvSetCount, tvRepCount, tvProgress;
    private Button btnStartNow, btnPauseResume, btnSkip;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning;
    private int currentSet = 1;
    private int currentRep = 1;
    private int totalSets = 3;
    private int totalReps = 10;
    private final long repDuration = 2000; // 2 seconds per rep
    private long timePerSet; // Total time for current set (reps × 2 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        // Initialize views
        exerciseImage = findViewById(R.id.exerciseImage);
        exerciseName = findViewById(R.id.exerciseName);
        exerciseDescription = findViewById(R.id.exerciseDescription);

        // Spinners
        setsSpinner = findViewById(R.id.spinnerSets);
        repsSpinner = findViewById(R.id.spinnerReps);
        weightSpinner = findViewById(R.id.spinnerWeight);

        // Main start button
        btnStart = findViewById(R.id.btnStart);

        dbHandler = DBhandler.getInstance(this);

        setupSpinners();
        loadExerciseDetails();

        btnStart.setOnClickListener(v -> startWorkout());
    }

    private void setupSpinners() {
        // Sets Spinner (1-10)
        List<Integer> setsList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            setsList.add(i);
        }
        ArrayAdapter<Integer> setsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, setsList);
        setsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsSpinner.setAdapter(setsAdapter);
        setsSpinner.setSelection(2); // Default to 3 sets

        // Reps Spinner (1-20)
        List<Integer> repsList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            repsList.add(i);
        }
        ArrayAdapter<Integer> repsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, repsList);
        repsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repsSpinner.setAdapter(repsAdapter);
        repsSpinner.setSelection(9); // Default to 10 reps

        // Weight Spinner (0-200)
        List<Integer> weightList = new ArrayList<>();
        for (int i = 0; i <= 200; i += 5) {
            weightList.add(i);
        }
        ArrayAdapter<Integer> weightAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weightList);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightAdapter);
        weightSpinner.setSelection(10); // Default to 50
    }

    private void loadExerciseDetails() {
        int exerciseId = getIntent().getIntExtra("exercise_id", -1);
        if (exerciseId == -1) {
            finish();
            return;
        }

        Cursor cursor = dbHandler.getExerciseDetails(exerciseId);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"));
            String image = cursor.getString(cursor.getColumnIndexOrThrow("exercise_image"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("exercise_description"));

            // Set exercise details
            exerciseName.setText(name);
            exerciseDescription.setText(description);

            // Load appropriate image based on gender
            int imageResId = getResources().getIdentifier(
                    image, "drawable", getPackageName());
            if (imageResId != 0) {
                exerciseImage.setImageResource(imageResId);
            }

            cursor.close();
        } else {
            finish();
        }
    }


    private void startWorkout() {
        totalSets = (Integer) setsSpinner.getSelectedItem();
        totalReps = (Integer) repsSpinner.getSelectedItem();
        currentSet = 1;
        currentRep = 1;
        timePerSet = totalReps * repDuration; // Calculate total set time
        timeLeftInMillis = timePerSet;
        showTimerPopup();
    }

    private void showTimerPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_timer, null);

        timerPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        timerPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timerPopup.setOutsideTouchable(false);

        // Initialize popup views
        tvTimer = popupView.findViewById(R.id.tvTimer);
        tvSetCount = popupView.findViewById(R.id.tvSetCount);
        tvRepCount = popupView.findViewById(R.id.tvRepCount);
        tvProgress = popupView.findViewById(R.id.tvProgress);
        btnStartNow = popupView.findViewById(R.id.btnStartNow);
        btnPauseResume = popupView.findViewById(R.id.btnPauseResume);
        btnSkip = popupView.findViewById(R.id.btnSkip);

        // Set initial values
        updateSetAndRepDisplay();
        updateTimerText();

        // Set click listeners
        btnStartNow.setOnClickListener(v -> {
            startTimer();
            btnStartNow.setVisibility(View.GONE);
            btnPauseResume.setVisibility(View.VISIBLE);
        });

        btnPauseResume.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        });

        btnSkip.setOnClickListener(v -> {
            skipCurrentSet();
        });

        // Show popup centered on screen
        timerPopup.showAtLocation(findViewById(android.R.id.content),
                Gravity.CENTER, 0, 0);
    }

    private void updateSetAndRepDisplay() {
        tvSetCount.setText(currentSet + "/" + totalSets + " sets");
        tvRepCount.setText(currentRep + "/" + totalReps + " reps");
        int progress = (int) (((float)(currentSet-1) / totalSets) * 100);
        tvProgress.setText(progress + "%");
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();

                // Calculate current rep based on elapsed time
                long elapsed = timePerSet - millisUntilFinished;
                int calculatedRep = (int)(elapsed / repDuration) + 1;

                if (calculatedRep != currentRep && calculatedRep <= totalReps) {
                    currentRep = calculatedRep;
                    updateSetAndRepDisplay();
                }
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                completeSet();
            }
        }.start();

        timerRunning = true;
        btnPauseResume.setText("Pause");
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeft = String.format("%02d", seconds);
        tvTimer.setText(timeLeft);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        btnPauseResume.setText("Resume");
    }

    private void resumeTimer() {
        startTimer();
    }

    private void skipCurrentSet() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        completeSet();
    }

    private void completeSet() {
        currentSet++;
        if (currentSet <= totalSets) {
            currentRep = 1;
            timeLeftInMillis = timePerSet;
            updateSetAndRepDisplay();
            startTimer();
        } else {
            timerPopup.dismiss();
            showWorkoutComplete();
        }
    }

    private void showWorkoutComplete() {
        Toast.makeText(this, "Workout Completed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (timerPopup != null && timerPopup.isShowing()) {
            timerPopup.dismiss();
        }
    }
}