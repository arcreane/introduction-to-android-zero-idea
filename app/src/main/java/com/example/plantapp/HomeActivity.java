package com.example.plantapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.plantapp.api.AvatarApi;

import java.io.File;

import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
    private String savedPlantName;
    private String savedUserName;
    private String savedImagePath;
    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView userNameTextView;
    private int sunlightClickCount = 0;
    // Variable for timer and vibration
    private Handler handler = new Handler();
    private boolean isSuperHot = false;
    private boolean isWatering = false;

    private boolean isBeingFed = false;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ImageView avatar;
    private AvatarApi avatarApi;

    private Button goToWallButton;
    private static final String BASE_URL = "https://api.multiavatar.com/";
    String AvatarId;

    private boolean isUserDataExists() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
        String plantName = sharedPreferences.getString("plantName", "Cactus");
        String userName = sharedPreferences.getString("userName", "Cactus Mom");
        AvatarId = sharedPreferences.getString("AvatarId", "1");

        // Check if required data exists
        return plantName != null && userName != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserDataExists()) {
            // Redirect to Welcome Activity if no data
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_home);

        // Initialize views
        plantImageView = findViewById(R.id.plantImageView);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        ImageButton sunlightButton = findViewById(R.id.sunlight);
        FrameLayout waterContainer = findViewById(R.id.appContainer);
        ImageButton waterButton = findViewById(R.id.waterButton);
        FrameLayout plantFoodContainer = findViewById(R.id.appContainer);
        ImageButton plantFoodButton = findViewById(R.id.plantFood);
        ImageButton musicButton = findViewById(R.id.music);
        avatar = findViewById(R.id.avatar);

        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);


        goToWallButton = findViewById(R.id.goToWallButton);

//        send a retrofit request to display the avatar user chose;


        // Retrieve data from SharedPreferences
        retrieveUserData();

        // Display the data
        displayUserData();

//        setup retrofit
        setupRetrofit();

        goToWallButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PlantWallActivity.class);
            startActivity(intent);
        });

        // Handle insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        musicButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                musicButton.setImageResource(R.drawable.icon_music);

            } else {
                mediaPlayer.start();
                isPlaying = true;
                musicButton.setImageResource(R.drawable.icon_music);
            }
        });




        // Handle Sunlight Button Click
        sunlightButton.setOnClickListener(v -> {
            sunlightClickCount++; // Increment the click count

            if (sunlightClickCount == 1) {
                // First click
                mainLayout.setBackgroundResource(R.drawable.sunlight_background);
            } else if (sunlightClickCount == 2) {
                // Second click
                mainLayout.setBackgroundResource(R.drawable.hot_background);
                isSuperHot = true;

                // Start the timer to check for 5 seconds
                handler.postDelayed(() -> {
                    if (isSuperHot) {
                        // Trigger vibration
                        vibrateDevice();

                        // Send notification that the plant is dry
                        sendPlantDryNotification();
                    }
                }, 5000); // 5 seconds delay
            } else if (sunlightClickCount == 3) {
                // Third click: Turn sunlight off
                mainLayout.setBackgroundResource(android.R.color.white);
                sunlightClickCount = 0; // Reset the click count
                isSuperHot = false;
            }
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

//        load avatars

        loadAvatar(AvatarId);
    }


    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }
    }


    private void sendPlantDryNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("plant_notifications", "Plant Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create notification
        Notification notification = new NotificationCompat.Builder(this, "plant_notifications")
                .setSmallIcon(R.drawable.default_plant) // Use your plant icon here
                .setContentTitle("Plant Alert")
                .setContentText("Unless you want your plant to turn into a crispy snack, give it some shade ! \uD83D\uDE1E")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Check for the notification permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        // Send the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);
    }



    // Start Water Animation
    private void startWaterAnimation(FrameLayout container) {
        for (int i = 0; i < 30; i++) {
            View rainDrop = new View(this);
            rainDrop.setBackgroundResource(R.drawable.water_drop);

            // Randomize raindrop positions
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(15, 30);
            int containerWidth = container.getWidth() > 0 ? container.getWidth() : 1080; // Fallback to a default width
            params.leftMargin = (int) (Math.random() * containerWidth);
            params.topMargin = -50; // Start off-screen
            rainDrop.setLayoutParams(params);

            container.addView(rainDrop);

            // Animate the raindrop
            ObjectAnimator animator = ObjectAnimator.ofFloat(rainDrop, "translationY", -50, container.getHeight());
            animator.setDuration(500 + (int) (Math.random() * 1000)); // Randomize duration
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


    // Retrieve User Data
    private void retrieveUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
        savedPlantName = sharedPreferences.getString("plantName", "Default Plant");
        savedUserName = sharedPreferences.getString("userName", "Default User");
        savedImagePath = sharedPreferences.getString("plantImagePath", null);

        // Log the retrieved data
        System.out.println("Plant Name: " + savedPlantName);
        System.out.println("User Name: " + savedUserName);
        System.out.println("Image Path: " + savedImagePath);
    }

    // Display User Data
    private void displayUserData() {
        // Set text views
        plantNameTextView.setText(savedPlantName);
        userNameTextView.setText(savedUserName);

        // Load and display the image if it exists
        if (savedImagePath != null) {
            File imageFile = new File(savedImagePath);
            if (imageFile.exists()) {
                plantImageView.setImageBitmap(BitmapFactory.decodeFile(savedImagePath));
            } else {
                // Set default image if saved image doesn't exist
                plantImageView.setImageResource(R.drawable.default_plant);
            }
        } else {
            // Set default image if no image path is saved
            plantImageView.setImageResource(R.drawable.default_plant);
        }
    }

    private void loadAvatar(String number) {
        String avatarUrl = BASE_URL + number + ".png";
        Glide.with(HomeActivity.this)
                .load(avatarUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar);
    }
    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        avatarApi = retrofit.create(AvatarApi.class);
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
