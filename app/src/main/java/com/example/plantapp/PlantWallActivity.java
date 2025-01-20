package com.example.plantapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class PlantWallActivity extends AppCompatActivity {
    private LinearLayout plantListLayout;
    private Button addNewPlantButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_wall);

        plantListLayout = findViewById(R.id.plantListLayout);
        addNewPlantButton = findViewById(R.id.addNewPlantButton);

        // Load saved plants and display them
        loadPlants();

        // Add button listener to navigate to SavePlantWallActivity
        addNewPlantButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlantWallActivity.this, SavePlantWallActivity.class);
            startActivity(intent);
        });
    }

    private void loadPlants() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlantDetails", Context.MODE_PRIVATE);
        String plantsData = sharedPreferences.getString("plants", "");

        if (!plantsData.isEmpty()) {
            String[] plantsArray = plantsData.split(";");
            for (String plantData : plantsArray) {
                String[] plantDetails = plantData.split("\\|");
                String caption = plantDetails[0];
                String description = plantDetails[1];
                String imagePath = plantDetails[2];

                // Create a new plant view and populate it
                View plantView = getLayoutInflater().inflate(R.layout.wall_item_plant, null);

                TextView captionTextView = plantView.findViewById(R.id.captionTextView);
                TextView descriptionTextView = plantView.findViewById(R.id.descriptionTextView);
                ImageView plantImageView = plantView.findViewById(R.id.plantImageView);

                captionTextView.setText(caption);
                descriptionTextView.setText(description);

                // Load the image from internal storage
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                plantImageView.setImageBitmap(bitmap);

                // Add the plant view to the layout
                plantListLayout.addView(plantView);
            }
        } else {
            Toast.makeText(this, "No plants found!", Toast.LENGTH_SHORT).show();
        }
    }
}
