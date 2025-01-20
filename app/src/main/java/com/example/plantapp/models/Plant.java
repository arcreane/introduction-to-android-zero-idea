package com.example.plantapp.models;

import android.graphics.Bitmap;

public class Plant {
    private String caption;
    private String description;
    private Bitmap image;

    public Plant(String caption, String description, Bitmap image) {
        this.caption = caption;
        this.description = description;
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }
}
