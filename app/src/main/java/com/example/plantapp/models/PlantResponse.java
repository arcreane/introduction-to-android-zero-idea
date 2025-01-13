package com.example.plantapp.models;
import java.util.List;

public class PlantResponse {
    public List<Plant> data;

    public static class Plant {
        public String id;
        public String scientific_name;
        public List<String> common_names;
        public String image_url;
    }
}


