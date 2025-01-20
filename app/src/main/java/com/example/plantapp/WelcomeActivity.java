package com.example.plantapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantapp.api.AvatarApi;
import java.io.IOException;
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

    private int currentNumber = 1;
    private int defaultNumber = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeViews();
        setupRetrofit();
        setupClickListeners();
        loadAvatar(currentNumber);
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
            if (!isLoading) {
                currentNumber++;
                loadAvatar(currentNumber);
                updateNavigationButtons();
            }
        });

        previousAvatarButton.setOnClickListener(v -> {
            if (!isLoading && currentNumber > 1) {
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
        isLoading = true;
        avatarView.setAlpha(0.5f); // Dim the image while loading

        Call<ResponseBody> call = avatarApi.getAvatar(String.valueOf(number));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] bytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        runOnUiThread(() -> {
                            avatarView.setImageBitmap(bitmap);
                            avatarView.setAlpha(1.0f);
                            isLoading = false;
                        });
                    } catch (IOException e) {
                        handleError("Error processing image: " + e.getMessage());
                    }
                } else {
                    handleError("Error loading avatar");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleError("Network error: " + t.getMessage());
            }
        });
    }

    private void handleError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_SHORT).show();
            avatarView.setAlpha(1.0f);
            isLoading = false;
        });
    }

    private void updateNavigationButtons() {
        previousAvatarButton.setEnabled(currentNumber > 1);
        nextAvatarButton.setEnabled(true);
    }
}