package com.example.plantapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.plantapp.api.TrefleApi;
import com.example.plantapp.models.PlantResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantSelectionActivity extends AppCompatActivity {
    private RecyclerView plantRecyclerView;
    private PlantAdapter plantAdapter;
    private static final String BASE_URL = "https://trefle.io/";
    private static final String API_TOKEN = "S2rrG3323xE4m6Kmvz535RxDyKsXEtPsmf7VJEjMwik";
    private Button selectPlantButton;
    private PlantResponse.Plant selectedPlant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_selection);

        plantRecyclerView = findViewById(R.id.plantRecyclerView);
        plantRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        plantAdapter = new PlantAdapter(this);
        plantRecyclerView.setAdapter(plantAdapter);
        selectPlantButton = findViewById(R.id.selectPlantButton);

        plantAdapter.setOnPlantSelectedListener(plant -> {
            selectedPlant = plant;
            selectPlantButton.setVisibility(View.VISIBLE);
        });

        selectPlantButton.setOnClickListener(v -> {
            if (selectedPlant != null) {
                SharedPreferences prefs = getSharedPreferences("PlantAppPrefs", Context.MODE_PRIVATE);
                prefs.edit()
                        .putString("selectedPlantId", selectedPlant.id)
                        .apply();

                // Add these log statements
                Log.d("PlantSelection", "Attempting to save plant ID: " + selectedPlant.id);
                String savedId = prefs.getString("selectedPlantId", "not_found");
                Log.d("PlantSelection", "Verified saved plant ID: " + savedId);

                Toast.makeText(this, "Plant selected successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        fetchPlants();
    }
            private void fetchPlants() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TrefleApi trefleApi = retrofit.create(TrefleApi.class);
        Call<PlantResponse> call = trefleApi.searchPlants(API_TOKEN, "");

        call.enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plantAdapter.setPlants(response.body().data);
                } else {
                    Toast.makeText(PlantSelectionActivity.this,
                            "Failed to load plants", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Toast.makeText(PlantSelectionActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
