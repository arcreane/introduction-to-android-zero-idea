package com.example.plantapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlantDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int PLANT_SELECTION_REQUEST_CODE = 103;

    private EditText plantNameInput;
    private ImageView plantImage;
    private Button selectImageButton;
    private Button clickImageButton;
    private ImageButton clearImageButton;
    private boolean hasCustomImage = false;
    private Button saveButton;
    private Button previousButton;

    private Uri imageUri;
    private String savedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        initializeViews();
        setupClickListeners();
        displaySelectedPlant();
    }

    private void initializeViews() {
        plantNameInput = findViewById(R.id.plantNameInput);
        plantImage = findViewById(R.id.plantImage);
        selectImageButton = findViewById(R.id.selectImageButton);
        clickImageButton = findViewById(R.id.clickImageButton);
        saveButton = findViewById(R.id.saveButton);
        previousButton = findViewById(R.id.previousButton);

        clearImageButton = findViewById(R.id.clearImageButton);
        plantImage.setImageResource(R.drawable.default_plant);
    }

    private void setupClickListeners() {
        selectImageButton.setOnClickListener(v -> openImagePicker());
        clickImageButton.setOnClickListener(v -> checkCameraPermission());
        saveButton.setOnClickListener(v -> savePlantDetails());
        previousButton.setOnClickListener(v -> finish());
        clearImageButton.setOnClickListener(v -> resetImage());
    }



    private void resetImage() {
        plantImage.setImageResource(R.drawable.default_plant);
        clearImageButton.setVisibility(View.GONE);
        savedImagePath = null;
        hasCustomImage = false;
    }

    private void displaySelectedPlant() {
        String plantName = getIntent().getStringExtra("PLANT_NAME");
        String plantImageUrl = getIntent().getStringExtra("PLANT_IMAGE_URL");

        if (plantName != null) {
            plantNameInput.setText(plantName);
        }
        if (plantImageUrl != null) {
            Glide.with(this).load(plantImageUrl).into(plantImage);
        }
    }

    private void savePlantDetails() {
        String plantName = plantNameInput.getText().toString().trim();
        if (plantName.isEmpty()) {
            plantNameInput.setError("Please enter plant name");
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("plantName", plantName);
        editor.putString("userName", getIntent().getStringExtra("USER_NAME"));
        if (savedImagePath != null) {
            editor.putString("plantImagePath", savedImagePath);
        }
        editor.apply();

        //todo add the redirect
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Plant Image"), PICK_IMAGE_REQUEST);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                handleGalleryImage(data);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                handleCameraImage(data);
            } else if (requestCode == PLANT_SELECTION_REQUEST_CODE) {
                handlePlantSelection(data);
            }
        }
    }

    private void handleGalleryImage(Intent data) {
        imageUri = data.getData();
        try {
            savedImagePath = saveImage(imageUri);
            plantImage.setImageURI(imageUri);
            clearImageButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraImage(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        savedImagePath = saveBitmapImage(photo);
        if (savedImagePath != null) {
            plantImage.setImageBitmap(photo);
            clearImageButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePlantSelection(Intent data) {
        String selectedPlantId = data.getStringExtra("SELECTED_PLANT_ID");
        String selectedPlantName = data.getStringExtra("SELECTED_PLANT_NAME");
        String selectedPlantImageUrl = data.getStringExtra("SELECTED_PLANT_IMAGE_URL");

        if (selectedPlantId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("PlantAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedPlantId", selectedPlantId);
            editor.apply();
        }

        if (selectedPlantName != null) {
            plantNameInput.setText(selectedPlantName);
        }
        if (selectedPlantImageUrl != null) {
            Glide.with(this).load(selectedPlantImageUrl).into(plantImage);
        }
    }


    private String saveImage(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PLANT_" + timeStamp + ".jpg";
        File storageDir = new File(getFilesDir(), "plant_images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, imageFileName);
        FileOutputStream fos = new FileOutputStream(imageFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        inputStream.close();
        return imageFile.getAbsolutePath();
    }

    private String saveBitmapImage(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PLANT_" + timeStamp + ".jpg";
        File storageDir = new File(getFilesDir(), "plant_images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, imageFileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}