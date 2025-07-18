package com.example.androidfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "ID";
    private static final String COL_USERNAME = "username";
    private static final String COL_AGE = "age";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_GENDER = "gender";
    private static final String COL_BODY_TYPE = "bodyType";
    private static final String COL_IS_LOGGED_IN = "is_logged_in";
    private static final String COL_HEIGHT = "height";
    private static final String COL_WEIGHT = "weight";

    // Workout table
    public static final String TABLE_WORKOUTS = "workouts";
    private static final String COL_WORKOUT_ID = "workout_id";
    public static final String COL_WORKOUT_NAME = "workout_name";

    // Exercise table

    public static final String TABLE_EXERCISES = "exercises";
    public static final String COL_EXERCISE_ID = "exercise_id";
    public static final String COL_EXERCISE_NAME = "exercise_name";
    public static final String COL_EXERCISE_IMAGE = "exercise_image";
    public static final String COL_EXERCISE_DESC = "exercise_description";
    public static final String COL_EXERCISE_VIDEO = "exercise_video_url";
    public static final String COL_WORKOUT_CATEGORY = "workout_category";
    public static final String COL_EXERCISE_GENDER = "gender";

    private static final String TABLE_ADMIN = "admin";
    private static final String COL_ADMIN_ID = "admin_id";
    private static final String COL_ADMIN_USERNAME = "admin_username";
    private static final String COL_ADMIN_PASSWORD = "admin_password";

    private static DBhandler instance; // Singleton instance
    private SQLiteDatabase db; // Single database reference

    public DBhandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBhandler getInstance(Context context) {
        if (instance == null) {
            instance = new DBhandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User Table
        String createUserTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_AGE + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_GENDER + " TEXT, " +
                COL_BODY_TYPE + " TEXT, " +
                COL_IS_LOGGED_IN + " INTEGER DEFAULT 0, " +
                COL_HEIGHT + " REAL DEFAULT 0, " +
                COL_WEIGHT + " REAL DEFAULT 0)";
        db.execSQL(createUserTable);

        // Workouts Table
        String createWorkoutTable = "CREATE TABLE " + TABLE_WORKOUTS + " (" +
                COL_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_WORKOUT_NAME + " TEXT UNIQUE NOT NULL)";
        db.execSQL(createWorkoutTable);

        // Exercises Table
        String createExerciseTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COL_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXERCISE_NAME + " TEXT NOT NULL, " +
                COL_EXERCISE_IMAGE + " TEXT NOT NULL, " +
                COL_EXERCISE_DESC + " TEXT, " +
                COL_EXERCISE_VIDEO + " TEXT, " +
                COL_WORKOUT_CATEGORY + " TEXT NOT NULL, " +
                COL_EXERCISE_GENDER + " TEXT DEFAULT 'male', " +
                "FOREIGN KEY(" + COL_WORKOUT_CATEGORY + ") REFERENCES " + TABLE_WORKOUTS + "(" + COL_WORKOUT_NAME + "))";
        db.execSQL(createExerciseTable);

        Log.d("DBhandler", "Database created with users, workouts, and exercises tables");

        insertDefaultWorkouts(db);
        insertDefaultExercises(db);
        insertFemaleExercises(db);

        String createAdminTable = "CREATE TABLE " + TABLE_ADMIN + " (" +
                COL_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ADMIN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_ADMIN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createAdminTable);
        insertDefaultAdmin(db);
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_ADMIN_USERNAME, "cucekadmin");
        values.put(COL_ADMIN_PASSWORD, "admin1010");

        try {
            db.insert(TABLE_ADMIN, null, values);
            Log.d("DBhandler", "Default admin account created");
        } catch (Exception e) {
            Log.e("DBhandler", "Error creating default admin", e);
        }
    }
    public boolean validateAdmin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_ADMIN,
                    new String[]{COL_ADMIN_ID},
                    COL_ADMIN_USERNAME + "=? AND " + COL_ADMIN_PASSWORD + "=?",
                    new String[]{username, password},
                    null, null, null);

            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_GENDER + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_BODY_TYPE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_IS_LOGGED_IN + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_HEIGHT + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_WEIGHT + " REAL DEFAULT 0");
        }

        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
            onCreate(db);
        }

        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_EXERCISES + " ADD COLUMN " + COL_EXERCISE_DESC + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_EXERCISES + " ADD COLUMN " + COL_EXERCISE_VIDEO + " TEXT");
            updateExerciseDescriptions(db);
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + TABLE_EXERCISES + " ADD COLUMN " + COL_EXERCISE_GENDER + " TEXT DEFAULT 'male'");
            updateExercisesWithGender(db); // Add this new method
        }
        if (oldVersion < 8) {  // Use your next version number here
            // Create admin table if it doesn't exist
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADMIN + " (" +
                    COL_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ADMIN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COL_ADMIN_PASSWORD + " TEXT NOT NULL)");

            // Insert default admin
            insertDefaultAdmin(db);
        }
    }

    private void updateExercisesWithGender(SQLiteDatabase db) {
        // Mark all existing exercises as male (default)
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_GENDER, "male");
        db.update(TABLE_EXERCISES, values, null, null);

        // Add female versions of exercises
        insertFemaleExercises(db);
    }
    private void insertDefaultWorkouts(SQLiteDatabase db) {
        String[] workouts = {"Chest", "Biceps", "Triceps", "Shoulders", "Back", "Abs", "Legs"};
        for (String workout : workouts) {
            ContentValues values = new ContentValues();
            values.put(COL_WORKOUT_NAME, workout);
            db.insert(TABLE_WORKOUTS, null, values);
        }
    }

    private void insertFemaleExercises(SQLiteDatabase db) {
        insertExercise(db, "Modified Push-ups", "female_pushups",
                "Knees on ground for easier form", "https://example.com/female_pushups", "Chest", "female");
        insertExercise(db, "Dumbbell Chest Press", "female_chestpress",
                "Lighter weights with controlled motion", "https://example.com/female_chestpress", "Chest", "female");
        insertExercise(db, "Light Bicep Curls", "female_bicepcurls",
                "Smaller weights with full range", "https://example.com/female_bicepcurls", "Biceps", "female");
        insertExercise(db, "Concentration Curls", "female_concentration",
                "Isolated curl for better focus", "https://example.com/female_concentration", "Biceps", "female");
        insertExercise(db, "Bench Dips", "female_benchdips",
                "Using bench for support", "https://example.com/female_benchdips", "Triceps", "female");
        insertExercise(db, "Overhead Triceps", "female_overhead",
                "Light dumbbell overhead extension", "https://example.com/female_overhead", "Triceps", "female");
        insertExercise(db, "Light Shoulder Press", "female_shoulderpress",
                "Lighter overhead press", "https://example.com/female_shoulderpress", "Shoulders", "female");
        insertExercise(db, "Front Raises", "female_frontraises",
                "Small weights lifted forward", "https://example.com/female_frontraises", "Shoulders", "female");
        insertExercise(db, "Assisted Pull-ups", "female_assistedpullups",
                "With band or machine support", "https://example.com/female_assistedpullups", "Back", "female");
        insertExercise(db, "Seated Row", "female_seatedrow",
                "Machine row for back muscles", "https://example.com/female_seatedrow", "Back", "female");
        insertExercise(db, "Knee Tucks", "female_kneetucks",
                "Seated knee lifts for core", "https://example.com/female_kneetucks", "Abs", "female");
        insertExercise(db, "Modified Plank", "female_plank",
                "On knees for easier hold", "https://example.com/female_plank", "Abs", "female");
        insertExercise(db, "Bodyweight Squats", "female_squats",
                "No weight squat variation", "https://example.com/female_squats", "Legs", "female");
        insertExercise(db, "Step-ups", "female_stepups",
                "Using bench or platform", "https://example.com/female_stepups", "Legs", "female");
    }

    private void updateExerciseDescriptions(SQLiteDatabase db) {
        updateExercise(db, "Bench Press", "Lie on bench and press weight upward", "https://example.com/benchpress");
        updateExercise(db, "Chest Flys", "Lie on bench with arms extended to sides", "https://example.com/chestfly");
        updateExercise(db, "Bicep Curls", "Curl weights up toward shoulders", "https://example.com/bicepcurl");
        updateExercise(db, "Hammer Curls", "Curl weights with palms facing each other", "https://example.com/hammercurl");
        updateExercise(db, "Tricep Dips", "Lower and raise body using arms", "https://example.com/tricepdip");
        updateExercise(db, "Skull Crushers", "Lower weight behind head while lying down", "https://example.com/skullcrusher");
        updateExercise(db, "Shoulder Press", "Press weights overhead", "https://example.com/shoulderpress");
        updateExercise(db, "Lateral Raises", "Raise arms to sides", "https://example.com/lateralraise");
        updateExercise(db, "Pull-ups", "Pull body up to bar", "https://example.com/pullup");
        updateExercise(db, "Deadlifts", "Lift weight from floor to hip level", "https://example.com/deadlift");
        updateExercise(db, "Crunches", "Lift shoulders off floor", "https://example.com/crunch");
        updateExercise(db, "Planks", "Hold body in straight line", "https://example.com/plank");
        updateExercise(db, "Squats", "Lower hips and stand back up", "https://example.com/squat");
        updateExercise(db, "Lunges", "Step forward and lower hips", "https://example.com/lunge");
    }

    private void updateExercise(SQLiteDatabase db, String name, String desc, String videoUrl) {
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_DESC, desc);
        values.put(COL_EXERCISE_VIDEO, videoUrl);
        db.update(TABLE_EXERCISES, values, COL_EXERCISE_NAME + "=?", new String[]{name});
    }

    private void insertDefaultExercises(SQLiteDatabase db) {
        insertExercise(db, "Bench Press", "bench_press", "Lie on bench and press weight upward", "https://example.com/benchpress", "Chest", "male");
        insertExercise(db, "Chest Flys", "chest_fly", "Lie on bench with arms extended to sides", "https://example.com/chestfly", "Chest", "male");
        insertExercise(db, "Bicep Curls", "bicep_curls", "Curl weights up toward shoulders", "https://example.com/bicepcurl", "Biceps", "male");
        insertExercise(db, "Hammer Curls", "hammer_curls", "Curl weights with palms facing each other", "https://example.com/hammercurl", "Biceps", "male");
        insertExercise(db, "Tricep Dips", "tricep_dips", "Lower and raise body using arms", "https://example.com/tricepdip", "Triceps", "male");
        insertExercise(db, "Skull Crushers", "skull_crushers", "Lower weight behind head while lying down", "https://example.com/skullcrusher", "Triceps", "male");
        insertExercise(db, "Shoulder Press", "shoulder_press", "Press weights overhead", "https://example.com/shoulderpress", "Shoulders", "male");
        insertExercise(db, "Lateral Raises", "lateral_raises", "Raise arms to sides", "https://example.com/lateralraise", "Shoulders", "male");
        insertExercise(db, "Pull-ups", "pull_ups", "Pull body up to bar", "https://example.com/pullup", "Back", "male");
        insertExercise(db, "Deadlifts", "deadlifts", "Lift weight from floor to hip level", "https://example.com/deadlift", "Back", "male");
        insertExercise(db, "Crunches", "crunches", "Lift shoulders off floor", "https://example.com/crunch", "Abs", "male");
        insertExercise(db, "Planks", "planks", "Hold body in straight line", "https://example.com/plank", "Abs", "male");
        insertExercise(db, "Squats", "squats", "Lower hips and stand back up", "https://example.com/squat", "Legs", "male");
        insertExercise(db, "Lunges", "lunges", "Step forward and lower hips", "https://example.com/lunge", "Legs", "male");
    }

    private void insertExercise(SQLiteDatabase db, String name, String imageName,
                                String description, String videoUrl, String category, String gender) {
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_NAME, name);
        values.put(COL_EXERCISE_IMAGE, imageName);
        values.put(COL_EXERCISE_DESC, description);
        values.put(COL_EXERCISE_VIDEO, videoUrl);
        values.put(COL_WORKOUT_CATEGORY, category);
        values.put(COL_EXERCISE_GENDER, gender);
        db.insert(TABLE_EXERCISES, null, values);
    }

    public Cursor getExerciseDetails(int exerciseId) {
        SQLiteDatabase db = getDatabase(false);
        return db.rawQuery("SELECT * FROM " + TABLE_EXERCISES +
                        " WHERE " + COL_EXERCISE_ID + "=?",
                new String[]{String.valueOf(exerciseId)});
    }


    public Cursor getExercisesForCategoryAndGender(String category, String gender) {
        SQLiteDatabase db = getDatabase(false);
        return db.rawQuery("SELECT * FROM " + TABLE_EXERCISES +
                        " WHERE " + COL_WORKOUT_CATEGORY + "=? AND " +
                        COL_EXERCISE_GENDER + "=?",
                new String[]{category, gender});
    }
    public Cursor getExercisesForCategory(String category) {
        SQLiteDatabase db = getDatabase(false);
        return db.rawQuery("SELECT * FROM " + TABLE_EXERCISES +
                        " WHERE " + COL_WORKOUT_CATEGORY + "=? AND " +
                        "(" + COL_EXERCISE_GENDER + "='male' OR " +
                        COL_EXERCISE_GENDER + "='unisex')",
                new String[]{category});
    }
    private SQLiteDatabase getDatabase(boolean writable) {
        if (db == null || !db.isOpen()) {
            db = writable ? this.getWritableDatabase() : this.getReadableDatabase();
        }
        return db;
    }

    public void addOrUpdateHeightWeight(double height, double weight) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_HEIGHT, height);
        values.put(COL_WEIGHT, weight);

        String username = getLoggedInUsername();
        if (username != null) {
            db.update(TABLE_NAME, values, COL_USERNAME + "=?", new String[]{username});
        } else {
            Log.e("DBhandler", "No logged-in user found while updating height/weight");
        }
    }

    public double[] getHeightWeight() {
        double[] heightWeight = new double[2];
        SQLiteDatabase db = getDatabase(false);
        String username = getLoggedInUsername();
        Cursor cursor = null;

        try {
            if (username != null) {
                cursor = db.rawQuery("SELECT " + COL_HEIGHT + ", " + COL_WEIGHT +
                                " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?",
                        new String[]{username});

                if (cursor.moveToFirst()) {
                    heightWeight[0] = cursor.getDouble(0);
                    heightWeight[1] = cursor.getDouble(1);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return heightWeight;
    }

    public String getUserEmail(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_EMAIL + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        String email = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return email;
    }

    public String getUserAge(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_AGE + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        String age = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return age;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertUser(String username, String age, String email, String password) {
        if (isEmailRegistered(email) || isUsernameTaken(username)) return false;

        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username.trim());
        values.put(COL_AGE, age);
        values.put(COL_EMAIL, email.trim());
        values.put(COL_PASSWORD, password.trim());

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public void updateGender(String username, String gender) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_GENDER, gender);
        db.update(TABLE_NAME, values, COL_USERNAME + "=?", new String[]{username});
    }

    public void updateBodyType(String username, String bodyType) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_BODY_TYPE, bodyType);
        db.update(TABLE_NAME, values, COL_USERNAME + "=?", new String[]{username});
    }

    public String getUserGender(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_GENDER + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        String gender = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return gender;
    }

    public String getUserBodyType(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_BODY_TYPE + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        String bodyType = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return bodyType;
    }

    public boolean isUserValid(String usernameOrEmail, String password) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE (" + COL_USERNAME + "=? OR " + COL_EMAIL + "=?) AND " + COL_PASSWORD + "=?",
                new String[]{usernameOrEmail, usernameOrEmail, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isProfileComplete(String username) {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_GENDER + ", " + COL_BODY_TYPE + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean isComplete = cursor.moveToFirst() && cursor.getString(0) != null && !cursor.getString(0).isEmpty() && cursor.getString(1) != null && !cursor.getString(1).isEmpty();
        cursor.close();
        return isComplete;
    }

    public String getLoggedInUsername() {
        SQLiteDatabase db = getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT " + COL_USERNAME + " FROM " + TABLE_NAME + " WHERE " + COL_IS_LOGGED_IN + " = 1", null);
        String username = cursor.moveToFirst() ? cursor.getString(0) : null;
        cursor.close();
        return username;
    }

    public void logoutUser() {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_IS_LOGGED_IN, 0);
        db.update(TABLE_NAME, values, null, null);
    }
    public void markUserAsLoggedIn(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            Log.e("DBhandler", "markUserAsLoggedIn failed: username/email is null or empty");
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Mark all users as logged out first
        ContentValues logoutValues = new ContentValues();
        logoutValues.put(COL_IS_LOGGED_IN, 0);
        db.update(TABLE_NAME, logoutValues, null, null);

        // Mark the current user as logged in
        ContentValues loginValues = new ContentValues();
        loginValues.put(COL_IS_LOGGED_IN, 1);
        int rows = db.update(TABLE_NAME, loginValues, COL_USERNAME + "=? OR " + COL_EMAIL + "=?", new String[]{usernameOrEmail, usernameOrEmail});

        db.close();

        if (rows > 0) {
            Log.d("DBhandler", usernameOrEmail + " marked as logged in");
        } else {
            Log.e("DBhandler", "markUserAsLoggedIn failed: No user found with username/email " + usernameOrEmail);
        }
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String email, String age, String gender, double height, double weight, String bodyType) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, newUsername); // Update username
        values.put(COL_EMAIL, email);
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        values.put(COL_HEIGHT, height);
        values.put(COL_WEIGHT, weight);
        values.put(COL_BODY_TYPE, bodyType);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_USERNAME + "=?", new String[]{oldUsername});
        return rowsUpdated > 0;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME,
                new String[]{COL_ID, COL_USERNAME, COL_EMAIL},
                null, null, null, null,
                COL_USERNAME + " ASC");
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean addExerciseWithCategory(String name, String imageName,
                                           String description, String videoUrl,
                                           String category, String gender) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Check if category exists
            if (!isCategoryExists(db, category)) {
                ContentValues categoryValues = new ContentValues();
                categoryValues.put(COL_WORKOUT_NAME, category);
                db.insert(TABLE_WORKOUTS, null, categoryValues);
            }

            // Insert exercise
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put(COL_EXERCISE_NAME, name);
            exerciseValues.put(COL_EXERCISE_IMAGE, imageName);
            exerciseValues.put(COL_EXERCISE_DESC, description);
            exerciseValues.put(COL_EXERCISE_VIDEO, videoUrl);
            exerciseValues.put(COL_WORKOUT_CATEGORY, category);
            exerciseValues.put(COL_EXERCISE_GENDER, gender);

            long result = db.insert(TABLE_EXERCISES, null, exerciseValues);
            db.setTransactionSuccessful();
            return result != -1;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private boolean isCategoryExists(SQLiteDatabase db, String category) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_WORKOUTS,
                    new String[]{COL_WORKOUT_NAME},
                    COL_WORKOUT_NAME + "=?",
                    new String[]{category},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
