package com.example.plantapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private String defaultPlantName = "My Plant"; // Default plant name
    private String defaultUserName = "Default User"; // Default user name
    private int defaultImageResource = R.drawable.default_plant;
    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_home);

        // Initialize views
        plantImageView = findViewById(R.id.plantImageView);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        ConstraintLayout mainLayout = findViewById(R.id.main);

        // Handle insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Display default user data
        displayUserData();
    }

    private void displayUserData() {
        // Set default text views
        plantNameTextView.setText(defaultPlantName);
        userNameTextView.setText(defaultUserName);

        // Set default image from drawable
        plantImageView.setImageResource(defaultImageResource);
    }
}
