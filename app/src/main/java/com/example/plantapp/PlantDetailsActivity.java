package com.example.plantapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


public class PlantDetailsActivity extends AppCompatActivity {

    private EditText plantNameInput;
    private ImageView plantImage;

    private Button saveButton;
    private Button previousButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        initializeViews();
        setupClickListeners();
        displaySelectedPlant();
    }

    private void initializeViews() {
        plantNameInput = findViewById(R.id.plantNameInput);
        plantImage = findViewById(R.id.plantImage);
        saveButton = findViewById(R.id.saveButton);
        previousButton = findViewById(R.id.previousButton);
        plantImage.setImageResource(R.drawable.default_plant);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> savePlantDetails());
        previousButton.setOnClickListener(v -> finish());
    }



    private void displaySelectedPlant() {
        String plantName = getIntent().getStringExtra("PLANT_NAME");
        String plantImageUrl = getIntent().getStringExtra("PLANT_IMAGE_URL");

        if (plantName != null) {
            plantNameInput.setText(plantName);
        }
        if (plantImageUrl != null) {
            Glide.with(this).load(plantImageUrl).into(plantImage);
        }
    }

    private void savePlantDetails() {
        String plantName = plantNameInput.getText().toString().trim();
        if (plantName.isEmpty()) {
            plantNameInput.setError("Please enter plant name");
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("plantName", plantName);
        editor.putString("userName", getIntent().getStringExtra("USER_NAME"));

        editor.apply();
        //Go to next page
        // todo

    }







    private void handlePlantSelection(Intent data) {
        String selectedPlantId = data.getStringExtra("SELECTED_PLANT_ID");
        String selectedPlantName = data.getStringExtra("SELECTED_PLANT_NAME");
        String selectedPlantImageUrl = data.getStringExtra("SELECTED_PLANT_IMAGE_URL");

        if (selectedPlantId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedPlantId", selectedPlantId);
            editor.apply();
        }

        if (selectedPlantName != null) {
            plantNameInput.setText(selectedPlantName);
        }
        if (selectedPlantImageUrl != null) {
            Glide.with(this).load(selectedPlantImageUrl).into(plantImage);
        }
    }



}
