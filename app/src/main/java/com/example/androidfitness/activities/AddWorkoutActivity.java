package com.example.androidfitness.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.androidfitness.DBhandler;
import com.example.androidfitness.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddWorkoutActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private TextInputEditText exerciseNameInput, descriptionInput, videoLinkInput;
    private AutoCompleteTextView categorySpinner;
    private RadioGroup genderRadioGroup;
    private Button uploadImageBtn, addExerciseBtn;
    private TextView imageNameText;
    private String imageFileName = "";
    private Uri imageUri;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        dbHandler = new DBhandler(this);
        initializeViews();
        setupCategorySpinner();
        setupButtonListeners();
    }

    private void initializeViews() {
        exerciseNameInput = findViewById(R.id.exercise_name_input);
        descriptionInput = findViewById(R.id.description_input);
        videoLinkInput = findViewById(R.id.video_link_input);
        categorySpinner = findViewById(R.id.category_spinner);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        uploadImageBtn = findViewById(R.id.upload_image_btn);
        addExerciseBtn = findViewById(R.id.add_exercise_btn);
        imageNameText = findViewById(R.id.image_name_text);
    }

    private void setupButtonListeners() {
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExerciseToDatabase();
            }
        });
    }

    private void showImagePickerOptions() {
        // Create an intent to open any image file from any location
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Add persistable permission to allow future access
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Create an intent to capture from camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.androidfitness.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }

        // Show a chooser dialog with both options
        Intent chooserIntent = Intent.createChooser(intent, "Select Image Source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {cameraIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null && data.getData() != null) {
                // Image selected from file browser
                imageUri = data.getData();

                // Take persistable permission for future access
                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                imageFileName = getFileNameFromUri(imageUri);
                imageNameText.setText("Image selected: " + imageFileName);
            } else {
                // Image captured from camera
                imageFileName = "exercise_" + System.currentTimeMillis() + ".jpg";
                imageNameText.setText("Image captured: " + imageFileName);
            }
            imageNameText.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void setupCategorySpinner() {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DBhandler.TABLE_WORKOUTS,
                    new String[]{DBhandler.COL_WORKOUT_NAME},
                    null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    categories.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories);

        categorySpinner.setAdapter(adapter);
    }

    private void addExerciseToDatabase() {
        String name = exerciseNameInput.getText().toString().trim();
        String category = categorySpinner.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String videoLink = videoLinkInput.getText().toString().trim();

        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = radioButton != null ? radioButton.getText().toString().toLowerCase() : "male";

        if (!validateInputs(name, category, description)) {
            return;
        }

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBhandler.COL_EXERCISE_NAME, name);
            values.put(DBhandler.COL_EXERCISE_IMAGE, imageFileName);
            values.put(DBhandler.COL_EXERCISE_DESC, description);
            values.put(DBhandler.COL_EXERCISE_VIDEO, videoLink);
            values.put(DBhandler.COL_WORKOUT_CATEGORY, category);
            values.put(DBhandler.COL_EXERCISE_GENDER, gender);

            long result = db.insert(DBhandler.TABLE_EXERCISES, null, values);
            if (result != -1) {
                Toast.makeText(this, "Exercise added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add exercise", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private boolean validateInputs(String name, String category, String description) {
        if (name.isEmpty()) {
            exerciseNameInput.setError("Exercise name is required");
            return false;
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            return false;
        }
        if (imageFileName.isEmpty()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}