package com.example.plantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.plantapp.api.AvatarApi;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WelcomeActivity extends AppCompatActivity {
    private EditText userNameInput;
    private Button nextButton;
    private ImageView logo;
    private ImageView avatarView;
    private ImageButton previousAvatarButton;
    private ImageButton nextAvatarButton;
    private AvatarApi avatarApi;
    private static final String BASE_URL = "https://api.multiavatar.com/";

    private int currentNumber = 1; // Start with 1
    private int defaultNumber = 1; // Store first number shown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeViews();
        setupRetrofit();
        setupClickListeners();
        loadAvatar(currentNumber); // Load first avatar
    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.userNameInput);
        nextButton = findViewById(R.id.nextButton);
        logo = findViewById(R.id.logo);
        avatarView = findViewById(R.id.avatar);
        previousAvatarButton = findViewById(R.id.previousAvatarButton);
        nextAvatarButton = findViewById(R.id.nextAvatarButton);

        logo.setImageResource(R.drawable.logo);
        previousAvatarButton.setEnabled(false);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        avatarApi = retrofit.create(AvatarApi.class);
    }

    private void setupClickListeners() {
        nextAvatarButton.setOnClickListener(v -> {
            currentNumber++;
            loadAvatar(currentNumber);
            updateNavigationButtons();
        });

        previousAvatarButton.setOnClickListener(v -> {
            if (currentNumber > 1) {
                currentNumber--;
                loadAvatar(currentNumber);
                updateNavigationButtons();
            }
        });

        nextButton.setOnClickListener(v -> {
            String userName = userNameInput.getText().toString().trim();
            if (userName.isEmpty()) {
                userNameInput.setError("Please enter your name");
                return;
            }

            Intent intent = new Intent(WelcomeActivity.this, PlantDetailsActivity.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("AVATAR_ID", String.valueOf(currentNumber));
            startActivity(intent);
        });
    }

    private void loadAvatar(int number) {
        String avatarUrl = BASE_URL + number + ".png";
        Glide.with(WelcomeActivity.this)
                .load(avatarUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatarView);
    }

    private void updateNavigationButtons() {
        previousAvatarButton.setEnabled(currentNumber > 1);
        nextAvatarButton.setEnabled(true);
    }
}