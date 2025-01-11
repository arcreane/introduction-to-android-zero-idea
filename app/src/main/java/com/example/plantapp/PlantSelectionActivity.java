package com.example.plantapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_selection);

        plantRecyclerView = findViewById(R.id.plantRecyclerView);
        plantRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        plantAdapter = new PlantAdapter();
        plantRecyclerView.setAdapter(plantAdapter);

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

    private class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
        private List<PlantResponse.Plant> plants = new ArrayList<>();

        public void setPlants(List<PlantResponse.Plant> plants) {
            this.plants = plants;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_plant, parent, false);
            return new PlantViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
            PlantResponse.Plant plant = plants.get(position);
            holder.bind(plant);
        }

        @Override
        public int getItemCount() {
            return plants.size();
        }

        private class PlantViewHolder extends RecyclerView.ViewHolder {
            private ImageView plantImageView;
            private TextView plantNameTextView;

            public PlantViewHolder(@NonNull View itemView) {
                super(itemView);
                plantImageView = itemView.findViewById(R.id.plantImageView);
                plantNameTextView = itemView.findViewById(R.id.plantNameTextView);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PlantResponse.Plant plant = plants.get(position);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SELECTED_PLANT_NAME",
                                plant.common_names != null && !plant.common_names.isEmpty()
                                        ? plant.common_names.get(0)
                                        : plant.scientific_name);
                        resultIntent.putExtra("SELECTED_PLANT_IMAGE_URL", plant.image_url);
                        resultIntent.putExtra("SELECTED_PLANT_ID", plant.id);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }


            public void bind(PlantResponse.Plant plant) {
                String displayName = plant.common_names != null && !plant.common_names.isEmpty()
                        ? plant.common_names.get(0)
                        : plant.scientific_name;
                plantNameTextView.setText(displayName);

                if (plant.image_url != null && !plant.image_url.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(plant.image_url)
                            .centerCrop()
                            .into(plantImageView);
                }
            }
        }
    }
}
