package com.example.go4lunch;

import android.content.SharedPreferences;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.repository.RestaurantRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public MutableLiveData <String> restaurantIdKeyLiveData = new MutableLiveData<>();

    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    public RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();

    public MutableLiveData<Location> getLocation() {
        return locationLiveData;
    }

    public void setLocation(Location location) {
        this.locationLiveData.postValue(location);
    }

    public LiveData<List<Result>> getRestaurants() {
        return restaurantRepository.getRestaurants(locationLiveData.getValue());
    }

    public LiveData<String> getRestaurantBooking() {
        return restaurantIdKeyLiveData;
    }

    public void setRestaurantBooking(String restaurantIdKey) {
        restaurantIdKeyLiveData.postValue(restaurantIdKey);
    }

    public void setSharedPreferences(String preference){

    }

    public String getSharedPreferences(){

        return null;
    }
}
