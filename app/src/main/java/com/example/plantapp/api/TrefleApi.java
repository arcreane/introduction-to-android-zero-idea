package com.example.plantapp.api;

import com.example.plantapp.models.PlantInformation;
import com.example.plantapp.models.PlantResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrefleApi {
    @GET("api/v1/plants")
    Call<PlantResponse> searchPlants(
            @Query("token") String token,
            @Query("q") String query
    );


    @GET("api/v1/plants/{plantId}")
    Call<PlantInformation> getPlantDetails(
            @Path("plantId") String plantId,
            @Header("Authorization") String apiToken
    );

}
