package com.example.androidfitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.androidfitness.BMIActivity;
import com.example.androidfitness.DBhandler;
import com.example.androidfitness.ProfileActivity;
import com.example.androidfitness.R;
import com.example.androidfitness.adapters.WorkoutAdapter;
import com.example.androidfitness.models.Workout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FemaleHomeActivity extends AppCompatActivity {

    private GridView gridView;
    private WorkoutAdapter adapter;
    private List<Workout> workoutList;
    private DBhandler dbHandler;

    private TextView tvMonthYear;
    private LinearLayout calendarDatesContainer;
    private Calendar currentCalendar;
    private View selectedDayView;

    // Search related variables
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_female_home);

        dbHandler = new DBhandler(this);

        // Initialize views
        tvMonthYear = findViewById(R.id.tv_month_year);
        calendarDatesContainer = findViewById(R.id.calendar_dates_container);
        gridView = findViewById(R.id.grid_workouts);
        searchView = findViewById(R.id.search_bar);

        currentCalendar = Calendar.getInstance();

        // Setup components
        setupCalendar();
        setupCalendarNavigation();
        setupWorkoutGrid();
        setupSearch();
        setupBottomNavigation();
    }

    private void setupWorkoutGrid() {
        workoutList = getWorkoutCategories();
        adapter = new WorkoutAdapter(this, workoutList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Workout selectedWorkout = (Workout) parent.getItemAtPosition(position);
            openWorkoutActivity(selectedWorkout);
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void openWorkoutActivity(Workout workout) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("workoutCategory", workout.getName());
        intent.putExtra("isFemale", true);
        startActivity(intent);
    }

    // Rest of your existing methods remain unchanged
    private List<Workout> getWorkoutCategories() {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new Workout("Chest", R.drawable.female_chest_icon));
        workouts.add(new Workout("Biceps", R.drawable.female_biceps_icon));
        workouts.add(new Workout("Triceps", R.drawable.female_triceps_icon));
        workouts.add(new Workout("Shoulders", R.drawable.female_shoulders_icon));
        workouts.add(new Workout("Back", R.drawable.female_back_icon));
        workouts.add(new Workout("Abs", R.drawable.female_abs_icon));
        workouts.add(new Workout("Legs", R.drawable.female_legs_icon));
        return workouts;
    }

    private void setupCalendar() {
        updateMonthYearHeader();
        calendarDatesContainer.removeAllViews();
        int daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            View dayView = LayoutInflater.from(this).inflate(R.layout.item_calendar_day, calendarDatesContainer, false);
            TextView tvDay = dayView.findViewById(R.id.tv_day);
            View selectionIndicator = dayView.findViewById(R.id.selection_indicator);

            tvDay.setText(String.valueOf(i));

            Calendar tempCalendar = (Calendar) currentCalendar.clone();
            tempCalendar.set(Calendar.DAY_OF_MONTH, i);
            if (isSameDay(tempCalendar, Calendar.getInstance())) {
                selectDay(dayView);
            }

            final int day = i;
            dayView.setOnClickListener(v -> {
                selectDay(v);
                Toast.makeText(this, "Selected: " + day, Toast.LENGTH_SHORT).show();
            });

            calendarDatesContainer.addView(dayView);
        }
    }

    private void selectDay(View dayView) {
        if (selectedDayView != null) {
            selectedDayView.findViewById(R.id.selection_indicator).setVisibility(View.INVISIBLE);
            TextView prevTvDay = selectedDayView.findViewById(R.id.tv_day);
            prevTvDay.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
        selectedDayView = dayView;
        dayView.findViewById(R.id.selection_indicator).setVisibility(View.VISIBLE);
        TextView tvDay = dayView.findViewById(R.id.tv_day);
        tvDay.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void updateMonthYearHeader() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void setupCalendarNavigation() {
        findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            setupCalendar();
        });

        findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            setupCalendar();
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_bmi) {
                startActivity(new Intent(this, BMIActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}