package com.example.plantapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.plantapp.models.PlantResponse;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.content.Context;


public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
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

    class PlantViewHolder extends RecyclerView.ViewHolder {
        private ImageView plantImageView;
        private TextView plantNameTextView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantImageView = itemView.findViewById(R.id.plantImageView);
            plantNameTextView = itemView.findViewById(R.id.plantNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemView.setBackgroundColor(Color.parseColor("#E6E6FA"));
                    PlantResponse.Plant plant = plants.get(position);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_PLANT_NAME",
                            plant.common_names != null && !plant.common_names.isEmpty()
                                    ? plant.common_names.get(0)
                                    : plant.scientific_name);
                    resultIntent.putExtra("SELECTED_PLANT_IMAGE_URL", plant.image_url);
                    resultIntent.putExtra("SELECTED_PLANT_ID", plant.id);

                    SharedPreferences prefs = itemView.getContext().getSharedPreferences("PlantAppPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("selectedPlantId", plant.id).apply();

                    ((Activity) itemView.getContext()).setResult(Activity.RESULT_OK, resultIntent);
                    ((Activity) itemView.getContext()).finish();
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
