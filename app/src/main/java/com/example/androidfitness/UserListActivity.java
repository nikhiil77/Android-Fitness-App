package com.example.androidfitness;

import android.database.Cursor;
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
import androidx.appcompat.app.AppCompatActivity;

public class UserListActivity extends AppCompatActivity {

    private EditText searchBar;
    private LinearLayout userListContainer;
    private DBhandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list); // Make sure this matches your XML filename

        dbHandler = DBhandler.getInstance(this);
        searchBar = findViewById(R.id.search_bar);
        userListContainer = findViewById(R.id.user_list_container);

        loadAllUsers();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
        });
    }

    private void loadAllUsers() {
        // Clear existing user cards (keep search bar and title)
        int childCount = userListContainer.getChildCount();
        for (int i = childCount - 1; i >= 2; i--) {
            userListContainer.removeViewAt(i);
        }

        Cursor cursor = dbHandler.getAllUsers();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndex("ID"); // Use your actual column name
                int usernameColumnIndex = cursor.getColumnIndex("username");
                int emailColumnIndex = cursor.getColumnIndex("email");

                do {
                    int userId = cursor.getInt(idColumnIndex);
                    String username = cursor.getString(usernameColumnIndex);
                    String email = cursor.getString(emailColumnIndex);

                    addUserCard(userId, username, email);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addUserCard(int userId, String username, String email) {
        // Inflate using the correct layout resource
        View userCard = LayoutInflater.from(this).inflate(R.layout.user_card_item, userListContainer, false);

        TextView nameView = userCard.findViewById(R.id.user_name);
        TextView emailView = userCard.findViewById(R.id.user_email);
        Button deleteButton = userCard.findViewById(R.id.delete_button);

        nameView.setText(username);
        emailView.setText(email);

        deleteButton.setOnClickListener(v -> {
            if (dbHandler.deleteUser(userId)) {
                userListContainer.removeView(userCard);
                Toast.makeText(this, username + " deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        userListContainer.addView(userCard);
    }

    private void filterUsers(String query) {
        query = query.toLowerCase().trim();

        for (int i = 2; i < userListContainer.getChildCount(); i++) {
            View child = userListContainer.getChildAt(i);
            TextView name = child.findViewById(R.id.user_name);
            TextView email = child.findViewById(R.id.user_email);

            boolean matches = name.getText().toString().toLowerCase().contains(query) ||
                    email.getText().toString().toLowerCase().contains(query);

            child.setVisibility(matches ? View.VISIBLE : View.GONE);
        }
    }
}