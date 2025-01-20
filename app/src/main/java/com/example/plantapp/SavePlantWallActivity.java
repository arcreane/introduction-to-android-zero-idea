package com.example.plantapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePlantWallActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView selectedImageView;
    private EditText captionEditText, descriptionEditText;
    private Button uploadImageButton, savePlantButton;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant_to_wall);

        // Initialize views
        selectedImageView = findViewById(R.id.selectedImageView);
        captionEditText = findViewById(R.id.captionEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        savePlantButton = findViewById(R.id.savePlantButton);


        // Handle Image Upload
        uploadImageButton.setOnClickListener(v -> openGallery());

        // Handle Save Button
        savePlantButton.setOnClickListener(v -> savePlant());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePlant() {
        String caption = captionEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
        } else if (caption.isEmpty()) {
            Toast.makeText(this, "Please enter a caption", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Save the image to internal storage
                String imagePath = saveImageToInternalStorage();

                // Save details to SharedPreferences (append to the existing plant list)
                SharedPreferences sharedPreferences = getSharedPreferences("PlantDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Retrieve existing plants list (if any)
                String plantsData = sharedPreferences.getString("plants", "");
                String newPlant = caption + "|" + description + "|" + imagePath;

                // Append the new plant to the list
                if (!plantsData.isEmpty()) {
                    plantsData += ";" + newPlant;
                } else {
                    plantsData = newPlant;
                }

                // Save the updated plant list back
                editor.putString("plants", plantsData);
                editor.apply();

                Toast.makeText(this, "Plant saved successfully!", Toast.LENGTH_SHORT).show();

                // Navigate to MyPlantWallActivity
                Intent intent = new Intent(this, PlantWallActivity.class);
                startActivity(intent);
                finish(); // Optional: Close this activity
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save plant", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private String saveImageToInternalStorage() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

        // Create a unique filename
        String fileName = "plant_" + System.currentTimeMillis() + ".png";

        // Get the internal storage directory
        File directory = getFilesDir();
        File imageFile = new File(directory, fileName);

        // Save the bitmap to the file
        FileOutputStream fos = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();

        return imageFile.getAbsolutePath();
    }



}

