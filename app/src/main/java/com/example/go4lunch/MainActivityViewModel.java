package com.example.go4lunch;


import static android.content.ContentValues.TAG;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.ui.listview.ListViewRecyclerViewAdapter;

import java.util.List;
import java.util.Observable;

public class MainActivityViewModel extends ViewModel {


    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    public RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();

    public Location location = new Location("");

    // Permet de récuperer la location dans le map
    public MutableLiveData<Location> getLocation() {
        return locationLiveData;
    }
    // Location prise dans mainActivité
    public void setLocation(Location location) {
        this.locationLiveData.postValue(location);
    }

    public LiveData<List<Result>> getRestaurants() {
        return restaurantRepository.getRestaurants(locationLiveData.getValue());
    }

   /* public void setLocationFromMapView(Location location){
        this.location = location;
    }

    public Location getLocationFromMapView(){
        return location;
    }*/
}
