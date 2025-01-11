package com.example.plantapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class HomeActivity extends AppCompatActivity {
    private String defaultPlantName = "My Plant"; // Default plant name
    private String defaultUserName = "Default User"; // Default user name
    private int defaultImageResource = R.drawable.default_plant;

    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView userNameTextView;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isSunlightOn = false;

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
        ImageButton musicButton = findViewById(R.id.music);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        ImageButton sunlightButton = findViewById(R.id.sunlight);

        // Handle insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Music button functionality
        musicButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                musicButton.setImageResource(R.drawable.icon_music); // Update icon for stopped state
            } else {
                mediaPlayer.start();
                isPlaying = true;
                musicButton.setImageResource(R.drawable.icon_music); // Update icon for playing state
            }
        });

        // Handle Sunlight Button Click
        sunlightButton.setOnClickListener(v -> {
            if (isSunlightOn) {
                // Turn sunlight off
                mainLayout.setBackgroundResource(android.R.color.white); // Reset to default background
            } else {
                // Turn sunlight on
                mainLayout.setBackgroundResource(R.drawable.sunlight_background);
            }
            isSunlightOn = !isSunlightOn;
        });

        displayUserData();

    }

    private void displayUserData() {
        // Set default text views
        plantNameTextView.setText(defaultPlantName);
        userNameTextView.setText(defaultUserName);

        // Set default image from drawable
        plantImageView.setImageResource(defaultImageResource);
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
