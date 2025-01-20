package com.example.plantapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantapp.R;
import com.example.plantapp.SavePlantWallActivity;

public class FeedFragment extends Fragment {
    private LinearLayout plantListLayout;
    private Button addNewPlantButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        plantListLayout = view.findViewById(R.id.plantListLayout);
        addNewPlantButton = view.findViewById(R.id.addNewPlantButton);

        // Load saved plants and display them
        loadPlants();

        // Add button listener to navigate to SavePlantWallActivity
        addNewPlantButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SavePlantWallActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload plants when returning to the fragment
        plantListLayout.removeAllViews(); // Clear existing views
        loadPlants();
    }

    private void loadPlants() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PlantDetails", Context.MODE_PRIVATE);
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
            Toast.makeText(requireContext(), "No plants found!", Toast.LENGTH_SHORT).show();
        }
    }
}