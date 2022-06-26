package com.example.go4lunch.network;

import com.example.go4lunch.modelApiNearby.Restaurant;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface NearbyGoogleApi {

    // Request REST from type Get with complement URL
    @GET("place/nearbysearch/json")
    Call<Restaurant> getResults(@QueryMap Map<String, String> params);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
