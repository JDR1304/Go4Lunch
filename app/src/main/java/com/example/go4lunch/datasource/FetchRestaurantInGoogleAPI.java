package com.example.go4lunch.datasource;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.modelApiNearby.Restaurant;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.network.NearbyRestaurants;

import java.util.List;

public class FetchRestaurantInGoogleAPI {

    private static final float DISTANCE_MIN_FOR_REFRESH_DATA = 50;
    private static volatile FetchRestaurantInGoogleAPI instance;
    MutableLiveData<List<Result>> restaurantsList = new MutableLiveData<>();
    private Location oldLocation;

    private FetchRestaurantInGoogleAPI() {
    }

    public static FetchRestaurantInGoogleAPI getInstance() {
        FetchRestaurantInGoogleAPI result = instance;
        if (result != null) {
            return result;
        }
        synchronized (FetchRestaurantInGoogleAPI.class) {
            if (instance == null) {
                instance = new FetchRestaurantInGoogleAPI();
            }
            return instance;
        }
    }

    public MutableLiveData<List<Result>> getRestaurants(Location location, String apiKey) {

        if (location!= null && (restaurantsList.getValue() == null || oldLocation == null || location.distanceTo(oldLocation) > DISTANCE_MIN_FOR_REFRESH_DATA)) {

            oldLocation = location;

            NearbyRestaurants.fetchRestaurants(new NearbyRestaurants.Callbacks() {

                @Override
                public void onResponse(@Nullable Restaurant restaurant) {
                    restaurantsList.postValue(restaurant.getResults());
                    Log.e(TAG, "onResponse in restaurant Repo: " + restaurant.getResults());
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure **********************: ");
                }
            }, location, apiKey);
        }
        return restaurantsList;
    }
}
