package com.example.myapplication.api;

import com.example.myapplication.models.PlantResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrefleApi {
    @GET("api/v1/plants")
    Call<PlantResponse> searchPlants(
            @Query("token") String token,
            @Query("q") String query
    );
}
