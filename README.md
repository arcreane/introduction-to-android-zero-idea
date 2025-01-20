# 🌱 PlantApp

PlantApp is an Android application designed to help users care for and manage their plants. It offers a personalized experience with features like plant selection, customization, and user onboarding.

## ✨ Features

### 👤 User Onboarding
- 🎉 Personalized welcome screen
- 🎭 Custom avatar selection using the Multiavatar API
- 📝 User name input

### 🌿 Plant Selection and Management
- 🔍 Browse and select plants from a curated list
- ℹ️ View detailed plant information
- ➕ Add custom plants with personalized details
- 📸 Option to take photos or select images from the gallery for plants

### 🌺 Plant Details
- 💾 Store and display plant names
- 🖼️ Show plant images (either selected or custom)
- 💽 Save plant information for future sessions

## 🛠️ Technical Details

### 🧩 Key Components
1. `WelcomeActivity`: Handles user onboarding and avatar selection
2. `PlantSelectionActivity`: Allows users to browse and select plants
3. `PlantDetailsActivity`: Manages input and storage of plant details
4. `PlantAdapter`: Handles the display of plants in a grid layout

### 🚀 Notable Features
- 🔌 API Integration:
    - Multiavatar API for user avatars
    - Trefle API for plant data
- 📷 Image Handling:
    - Gallery image selection
    - Camera integration for taking plant photos
- 💾 Local Storage:
    - SharedPreferences for user and plant data
    - Local file storage for plant images

### 📚 Libraries Used
- Retrofit: For API calls
- Glide: For efficient image loading
- RecyclerView: For displaying plant grids

## 🚀 Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Ensure you have the latest Android SDK installed
4. Update the `BASE_URL` and `API_TOKEN` in `PlantSelectionActivity` with your Trefle API credentials
5. Update the `BASE_URL` in `WelcomeActivity` for the Multiavatar API if needed
6. Build and run the application on an emulator or physical device

## 📱 Usage

1. Launch the app and enter your name on the welcome screen
2. Select an avatar using the navigation buttons
3. Proceed to plant selection or add a custom plant
4. View and manage your plants in the main interface

## 🔐 Permissions

The app requires the following permissions:
- 📷 Camera access (for taking plant photos)
- 💾 Storage access (for saving and loading plant images)

## 🤝 Contributing

Contributions to PlantApp are welcome. Please feel free to submit pull requests or open issues for bugs and feature requests.

