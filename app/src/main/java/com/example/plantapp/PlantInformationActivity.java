package com.example.plantapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.plantapp.api.TrefleApi;
import com.example.plantapp.models.PlantInformation;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantInformationActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://trefle.io/";
    private static final String API_TOKEN = "S2rrG3323xE4m6Kmvz535RxDyKsXEtPsmf7VJEjMwik";

    private TextView plantNameTextView;
    private ImageView plantImageView;
    private TextView plantScientificNameTextView;
    private TextView plantCommonNamesTextView;
    private TextView plantVegetableTextView;
    private TrefleApi trefleApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_information);

        initializeViews();
        setupRetrofit();
        fetchPlantData();
    }

    private void initializeViews() {
        plantNameTextView = findViewById(R.id.plantNameTextView);
        plantImageView = findViewById(R.id.plantImageView);
        plantScientificNameTextView = findViewById(R.id.plantScientificNameTextView);
        plantCommonNamesTextView = findViewById(R.id.plantCommonNamesTextView);
        plantVegetableTextView = findViewById(R.id.plantVegetableTextView);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        trefleApi = retrofit.create(TrefleApi.class);
    }

    private void fetchPlantData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
        String plantId = sharedPreferences.getString("selectedPlantId", "1");
        fetchPlantDetails(plantId);
    }

    private void fetchPlantDetails(String plantId) {
        Call<PlantInformation> call = trefleApi.getPlantDetails(plantId, API_TOKEN);

        call.enqueue(new Callback<PlantInformation>() {
            @Override
            public void onResponse(Call<PlantInformation> call, Response<PlantInformation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body().data);
                } else {
                    Toast.makeText(PlantInformationActivity.this,
                            "Failed to fetch plant details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantInformation> call, Throwable t) {
                Toast.makeText(PlantInformationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(PlantInformation.Data plantData) {
        // Set plant name
        if (plantData.common_name != null) {
            plantNameTextView.setText(plantData.common_name);
        } else {
            plantNameTextView.setText("Unknown Plant");
        }

        // Load image
        if (plantData.image_url != null) {
            Glide.with(this)
                    .load(plantData.image_url)
                    .fallback(R.drawable.default_plant)
                    .into(plantImageView);
        }

        // Set scientific name
        plantScientificNameTextView.setText("Scientific Name: " +
                (plantData.scientific_name != null ? plantData.scientific_name : "N/A"));

        // Set common names with language codes
        if (plantData.main_species != null && plantData.main_species.common_names != null) {
            StringBuilder commonNames = new StringBuilder();
            for (Map.Entry<String, List<String>> entry :
                    plantData.main_species.common_names.entrySet()) {
                commonNames.append(entry.getKey().toUpperCase())
                        .append(": ")
                        .append(String.join(", ", entry.getValue()))
                        .append("\n");
            }
            plantCommonNamesTextView.setText(commonNames.toString());
        } else {
            plantCommonNamesTextView.setText("No common names available");
        }

        // Set vegetable status
        plantVegetableTextView.setText("Is it a vegetable? " +
                (plantData.vegetable ? "Yes" : "No"));
    }
}