package com.example.go4lunch.network;


import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.go4lunch.modelApiNearby.Restaurant;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyRestaurants {


    // Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable Restaurant restaurant);
        void onFailure();
    }

    // Public method to start fetching restaurants
    public static void fetchRestaurants(Callbacks callbacks, Location location){
        // Create a weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);
        // Get a Retrofit instance and the related endpoints
        NearbyGoogleApi nearbyGoogleApi = NearbyGoogleApi.retrofit.create(NearbyGoogleApi.class);

        // Create the call on API
        Map<String, String> params = new HashMap<>();
        params.put("location", location.getLatitude()+","+location.getLongitude());
        params.put("radius", "1500");
        params.put("type", "restaurant");
        params.put("key", "AIzaSyD6pekqGKHnG9bm4jbb21ges37dv2UgH5w");
        Call<Restaurant> call = nearbyGoogleApi.getResults(params);
        // Start the call
        call.enqueue(new Callback<Restaurant>() {

            @Override
            public void onResponse(Call <Restaurant> call, Response<Restaurant> response) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
                Log.e("fetchUsers", "success  " + response.toString());
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
                Log.e("fetchUsers", "failure  "+ t );
            }
        });
    }

}
