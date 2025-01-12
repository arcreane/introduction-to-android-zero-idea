package com.example.plantapp;

import android.content.Context;
import android.content.SharedPreferences;
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

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    private static final int SELECTED_COLOR = Color.parseColor("#9999CC");
    private Context context;
    private OnPlantSelectedListener onPlantSelectedListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private List<PlantResponse.Plant> plants = new ArrayList<>();

    public interface OnPlantSelectedListener {
        void onPlantSelected(PlantResponse.Plant plant);
    }

    public PlantAdapter(Context context) {
        this.context = context;
    }

    public void setOnPlantSelectedListener(OnPlantSelectedListener listener) {
        this.onPlantSelectedListener = listener;
    }

    public void setPlants(List<PlantResponse.Plant> plants) {
        this.plants = plants;
        checkForPreviousSelection();
        notifyDataSetChanged();
    }

    private void checkForPreviousSelection() {
        SharedPreferences prefs = context.getSharedPreferences("PlantAppPrefs", Context.MODE_PRIVATE);
        String previousId = prefs.getString("selectedPlantId", null);

        if (previousId != null) {
            for (int i = 0; i < plants.size(); i++) {
                if (plants.get(i).id.equals(previousId)) {
                    selectedPosition = i;
                    if (onPlantSelectedListener != null) {
                        onPlantSelectedListener.onPlantSelected(plants.get(i));
                    }
                    break;
                }
            }
        }
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
        private final ImageView plantImageView;
        private final TextView plantNameTextView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantImageView = itemView.findViewById(R.id.plantImageView);
            plantNameTextView = itemView.findViewById(R.id.plantNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int previousPosition = selectedPosition;
                    selectedPosition = position;

                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);

                    PlantResponse.Plant plant = plants.get(position);
                    if (onPlantSelectedListener != null) {
                        onPlantSelectedListener.onPlantSelected(plant);
                    }

                    // Save the selection to SharedPreferences
                    SharedPreferences prefs = context.getSharedPreferences("PlantAppPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("selectedPlantId", plant.id).apply();
                }
            });
        }

        public void bind(PlantResponse.Plant plant) {
            // Set plant name
            String displayName = plant.common_names != null && !plant.common_names.isEmpty()
                    ? plant.common_names.get(0)
                    : plant.scientific_name;
            plantNameTextView.setText(displayName);

            // Set background based on selection state
            itemView.setBackgroundColor(getAdapterPosition() == selectedPosition
                    ? SELECTED_COLOR
                    : Color.TRANSPARENT);

            // Load image
            if (plant.image_url != null && !plant.image_url.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(plant.image_url)
                        .centerCrop()
                        .into(plantImageView);
            }
        }
    }
}
