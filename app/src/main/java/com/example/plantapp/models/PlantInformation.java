package com.example.plantapp.models;

import java.util.List;
import java.util.Map;

public class PlantInformation {
    public Data data;

    public static class Data {
        public String common_name;
        public String scientific_name;
        public String image_url;
        public boolean vegetable;
        public MainSpecies main_species;

        public static class MainSpecies {
            public Map<String, List<String>> common_names;
        }
    }
}