package com.example.plantapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.plantapp.api.AvatarApi;

import java.io.File;

import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    private String savedPlantName;
    private String savedUserName;
    private String savedImagePath;
    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView userNameTextView;
    private int sunlightClickCount = 0;
    private final Handler handler = new Handler();
    private boolean isSuperHot = false;
    private boolean isWatering = false;
    private boolean isBeingFed = false;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ImageView avatar;
    private AvatarApi avatarApi;
    private static final String BASE_URL = "https://api.multiavatar.com/";
    private String AvatarId;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews();
        setupMediaPlayer();
        retrieveUserData();
        displayUserData();
        setupRetrofit();
        setupClickListeners();
        loadAvatar(AvatarId);

        return rootView;
    }

    private void initializeViews() {
        plantImageView = rootView.findViewById(R.id.plantImageView);
        plantNameTextView = rootView.findViewById(R.id.plantNameTextView);
        userNameTextView = rootView.findViewById(R.id.userNameTextView);
        avatar = rootView.findViewById(R.id.avatar);
    }

    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.background);
        mediaPlayer.setLooping(true);
    }

    private void setupClickListeners() {
        ConstraintLayout mainLayout = rootView.findViewById(R.id.main);
        ImageButton sunlightButton = rootView.findViewById(R.id.sunlight);
        ImageButton waterButton = rootView.findViewById(R.id.waterButton);
        ImageButton plantFoodButton = rootView.findViewById(R.id.plantFood);
        ImageButton musicButton = rootView.findViewById(R.id.music);
        FrameLayout waterContainer = rootView.findViewById(R.id.appContainer);
        FrameLayout plantFoodContainer = rootView.findViewById(R.id.appContainer);

        setupMusicButton(musicButton);
        setupSunlightButton(sunlightButton, mainLayout);
        setupWaterButton(waterButton, waterContainer);
        setupPlantFoodButton(plantFoodButton, plantFoodContainer);
    }

    private void setupMusicButton(ImageButton musicButton) {
        musicButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                mediaPlayer.start();
                isPlaying = true;
            }
            musicButton.setImageResource(R.drawable.icon_music);
        });
    }

    private void setupSunlightButton(ImageButton sunlightButton, ConstraintLayout mainLayout) {
        sunlightButton.setOnClickListener(v -> handleSunlightClick(mainLayout));
    }

    private void setupWaterButton(ImageButton waterButton, FrameLayout waterContainer) {
        waterButton.setOnClickListener(v -> {
            if (isWatering) {
                waterContainer.removeAllViews();
            } else {
                startWaterAnimation(waterContainer);
            }
            isWatering = !isWatering;
        });
    }

    private void setupPlantFoodButton(ImageButton plantFoodButton, FrameLayout plantFoodContainer) {
        plantFoodButton.setOnClickListener(v -> {
            if (isBeingFed) {
                plantFoodContainer.removeAllViews();
            } else {
                startPlantFoodAnimation(plantFoodContainer);
            }
            isBeingFed = !isBeingFed;
        });
    }

    private void handleSunlightClick(ConstraintLayout mainLayout) {
        sunlightClickCount++;

        if (sunlightClickCount == 1) {
            mainLayout.setBackgroundResource(R.drawable.sunlight_background);
        } else if (sunlightClickCount == 2) {
            mainLayout.setBackgroundResource(R.drawable.hot_background);
            isSuperHot = true;
            startHotTimer();
        } else {
            mainLayout.setBackgroundResource(android.R.color.white);
            sunlightClickCount = 0;
            isSuperHot = false;
        }
    }

    private void startHotTimer() {
        handler.postDelayed(() -> {
            if (isSuperHot) {
                vibrateDevice();
                sendPlantDryNotification();
            }
        }, 5000);
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
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
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(requireContext(), "plant_notifications")
                .setSmallIcon(R.drawable.default_plant)
                .setContentTitle("Plant Alert")
                .setContentText("Unless you want your plant to turn into a crispy snack, give it some shade ! \uD83D\uDE1E")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireContext());
        notificationManagerCompat.notify(1, notification);
    }

    private void startWaterAnimation(FrameLayout container) {
        for (int i = 0; i < 30; i++) {
            View rainDrop = new View(requireContext());
            rainDrop.setBackgroundResource(R.drawable.water_drop);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(15, 30);
            int containerWidth = container.getWidth() > 0 ? container.getWidth() : 1080;
            params.leftMargin = (int) (Math.random() * containerWidth);
            params.topMargin = -50;
            rainDrop.setLayoutParams(params);

            container.addView(rainDrop);

            ObjectAnimator animator = ObjectAnimator.ofFloat(rainDrop, "translationY", -50, container.getHeight());
            animator.setDuration(500 + (int) (Math.random() * 1000));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
    }

    private void startPlantFoodAnimation(FrameLayout container) {
        for (int i = 0; i < 30; i++) {
            View supplementParticle = new View(requireContext());
            supplementParticle.setBackgroundResource(R.drawable.supplement_particle);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(30, 30);
            int containerWidth = container.getWidth() > 0 ? container.getWidth() : 1080;
            params.leftMargin = (int) (Math.random() * containerWidth);
            params.topMargin = -50;
            supplementParticle.setLayoutParams(params);

            container.addView(supplementParticle);

            ObjectAnimator animator = ObjectAnimator.ofFloat(supplementParticle, "translationY", -50, container.getHeight());
            animator.setDuration(2000 + (int) (Math.random() * 1000));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
    }

    private void retrieveUserData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PlantAppPrefs", Context.MODE_PRIVATE);
        savedPlantName = sharedPreferences.getString("plantName", "Default Plant");
        savedUserName = sharedPreferences.getString("userName", "Default User");
        savedImagePath = sharedPreferences.getString("plantImagePath", null);
        AvatarId = sharedPreferences.getString("AvatarId", "1");
    }

    private void displayUserData() {
        plantNameTextView.setText(savedPlantName);
        userNameTextView.setText(savedUserName);

        if (savedImagePath != null) {
            File imageFile = new File(savedImagePath);
            if (imageFile.exists()) {
                plantImageView.setImageBitmap(BitmapFactory.decodeFile(savedImagePath));
            } else {
                plantImageView.setImageResource(R.drawable.default_plant);
            }
        } else {
            plantImageView.setImageResource(R.drawable.default_plant);
        }
    }

    private void loadAvatar(String number) {
        String avatarUrl = BASE_URL + number + ".png";
        Glide.with(this)
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
    public void onDestroyView() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroyView();
    }
}