package com.example.plantapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private EditText userNameInput;
    private Button nextButton;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If no data exists, show welcome screen
        setContentView(R.layout.activity_welcome);

        // Initialize views
        userNameInput = findViewById(R.id.userNameInput);
        nextButton = findViewById(R.id.nextButton);
        logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameInput.getText().toString().trim();
                if (userName.isEmpty()) {
                    userNameInput.setError("Please enter your name");
                    return;
                }

                // Start PlantDetailsActivity and pass the username
                Intent intent = new Intent(WelcomeActivity.this, PlantDetailsActivity.class);
                intent.putExtra("USER_NAME", userName);
                startActivity(intent);
            }
        });
    }

}