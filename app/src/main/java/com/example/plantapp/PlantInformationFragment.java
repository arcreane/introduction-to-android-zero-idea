package com.example.plantapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.plantapp.api.TrefleApi;
import com.example.plantapp.models.PlantInformationModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantInformationFragment extends Fragment {

    private static final String BASE_URL = "https://trefle.io/";
    private static final String API_TOKEN = "S2rrG3323xE4m6Kmvz535RxDyKsXEtPsmf7VJEjMwik";

    private TextView plantNameTextView;
    private ImageView plantImageView;
    private TextView plantScientificNameTextView;
    private TextView plantCommonNamesTextView;
    private TextView plantVegetableTextView;
    private TrefleApi trefleApi;

    public PlantInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant_information, container, false);

        initializeViews(view);
        setupRetrofit();
        fetchPlantData();

        return view;
    }

    private void initializeViews(View view) {
        plantNameTextView = view.findViewById(R.id.plantNameTextView);
        plantImageView = view.findViewById(R.id.plantImageView);
        plantScientificNameTextView = view.findViewById(R.id.plantScientificNameTextView);
        plantCommonNamesTextView = view.findViewById(R.id.plantCommonNamesTextView);
        plantVegetableTextView = view.findViewById(R.id.plantVegetableTextView);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        trefleApi = retrofit.create(TrefleApi.class);
    }

    private void fetchPlantData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PlantAppPrefs", getActivity().MODE_PRIVATE);
        String plantId = sharedPreferences.getString("selectedPlantId", "1");
        fetchPlantDetails(plantId);
    }

    private void fetchPlantDetails(String plantId) {
        Call<PlantInformationModel> call = trefleApi.getPlantDetails(plantId, API_TOKEN);

        call.enqueue(new Callback<PlantInformationModel>() {
            @Override
            public void onResponse(Call<PlantInformationModel> call, Response<PlantInformationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body().data);
                } else {
                    Toast.makeText(getContext(),
                            "Failed to fetch plant details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantInformationModel> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(PlantInformationModel.Data plantData) {
        // Set plant name
        plantNameTextView.setText(plantData.common_name != null ? plantData.common_name : "Unknown Plant");

        // Load image with error handling
        Glide.with(this)
                .load(plantData.image_url)
                .placeholder(R.drawable.default_plant) // Placeholder while loading
                .error(R.drawable.default_plant) // Error image if loading fails
                .into(plantImageView);

        // Set scientific name
        plantScientificNameTextView.setText("Scientific Name: " + (plantData.scientific_name != null ? plantData.scientific_name : "N/A"));

        // Set common names with language codes
        StringBuilder commonNames = new StringBuilder();
        if (plantData.main_species != null && plantData.main_species.common_names != null) {
            for (Map.Entry<String, List<String>> entry : plantData.main_species.common_names.entrySet()) {
                commonNames.append(entry.getKey().toUpperCase())
                        .append(": ")
                        .append(String.join(", ", entry.getValue()))
                        .append("\n\n");
            }
            plantCommonNamesTextView.setText(commonNames.toString());
        } else {
            plantCommonNamesTextView.setText("No common names available");
        }

        // Set vegetable status
        plantVegetableTextView.setText("Is it a vegetable? " + (plantData.vegetable ? "\n\nYes" : "\n\nNo"));
    }

}
