package com.example.plantapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
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
    private boolean isWatering = false;
    private boolean isBeingFed = false;



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
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        ImageButton sunlightButton = findViewById(R.id.sunlight);
        FrameLayout waterContainer = findViewById(R.id.appContainer);
        ImageButton waterButton = findViewById(R.id.waterButton);
        FrameLayout plantFoodContainer = findViewById(R.id.appContainer);
        ImageButton plantFoodButton = findViewById(R.id.plantFood);

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

        // Handle Water Button Click
        waterButton.setOnClickListener(v -> {
            if (isWatering) {
                waterContainer.removeAllViews();
            } else {
                startWaterAnimation(waterContainer);
            }
            isWatering = !isWatering;
        });

        plantFoodButton.setOnClickListener(v -> {
            if (isBeingFed) {
                plantFoodContainer.removeAllViews();
            } else {
                startPlantFoodAnimation(plantFoodContainer);
            }
            isBeingFed = !isBeingFed;
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

    private void startWaterAnimation(FrameLayout container) {
        for (int i = 0; i < 30; i++) {
            View rainDrop = new View(this);
            rainDrop.setBackgroundResource(R.drawable.water_drop);

            // Randomize raindrop positions
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(5, 30);
            int containerWidth = container.getWidth() > 0 ? container.getWidth() : 1080; // Fallback to a default width
            params.leftMargin = (int) (Math.random() * containerWidth);
            params.topMargin = -50; // Start off-screen
            rainDrop.setLayoutParams(params);

            container.addView(rainDrop);

            // Animate the raindrop
            ObjectAnimator animator = ObjectAnimator.ofFloat(rainDrop, "translationY", -50, container.getHeight());
            animator.setDuration(2000 + (int) (Math.random() * 1000)); // Randomize duration
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
    }


    private void startPlantFoodAnimation(FrameLayout container) {
        for (int i = 0; i < 30; i++) {
            // Create a new view for each particle
            View supplementParticle = new View(this);
            supplementParticle.setBackgroundResource(R.drawable.supplement_particle); // Reference to your drawable

            // Randomize positions
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(30, 30); // Adjust the size of particles
            int containerWidth = container.getWidth() > 0 ? container.getWidth() : 1080;
            params.leftMargin = (int) (Math.random() * containerWidth); // Random horizontal position
            params.topMargin = -50; // Start off-screen vertically
            supplementParticle.setLayoutParams(params);

            container.addView(supplementParticle);

            // Animate the particles (movement)
            ObjectAnimator animator = ObjectAnimator.ofFloat(supplementParticle, "translationY", -50, container.getHeight());
            animator.setDuration(2000 + (int) (Math.random() * 1000)); // Randomize animation duration
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
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
