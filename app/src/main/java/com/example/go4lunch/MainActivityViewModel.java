package com.example.go4lunch;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.repository.ApiRepository;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public String apiKey;

    public String restaurantPlaceId;

    public List <User> usersList = new ArrayList<>();

    public MutableLiveData <String> restaurantIdKeyLiveData = new MutableLiveData<>();

    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    private UserRepository userRepository = UserRepository.getInstance();

    private RestaurantRepository restaurantRepository =RestaurantRepository.getInstance();

    public ApiRepository apiRepository = ApiRepository.getInstance();

    public MutableLiveData<Location> getLocation() {
        return locationLiveData;
    }

    public void setLocation(Location location) {
        this.locationLiveData.postValue(location);
    }

    public LiveData<List<Result>> getRestaurants() {
        return apiRepository.getRestaurants(locationLiveData.getValue(), getApiKey());
    }

    /*public LiveData<String> getRestaurantBooking() {
        return restaurantIdKeyLiveData;
    }*/

    public void setRestaurantBooking(String restaurantIdKey) {
        restaurantIdKeyLiveData.postValue(restaurantIdKey);
    }

    public String getApiKey(){
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    // Why return LiveData because I don't want change the data outside the viewModel
    public LiveData<List<User>> getUsers() {
        return userRepository.getUsersList();
    }

    public String getCurrentUserUid(){
        return userRepository.getCurrentUserUID();
    }


    public String getRestaurantBooking() {
        return restaurantPlaceId;
    }

//------------------------------------------------------------------------------------------------
//RestaurantRepository

    public void createRestaurant(String uid,String name, String address, List<String> usersWhoChoseRestaurant,
                                 List <String> favoriteRestaurantUsers, int likeNumber){
        restaurantRepository.createRestaurant(uid, name, address, usersWhoChoseRestaurant, favoriteRestaurantUsers, likeNumber);
    }

    public void deleteRestaurant(String uid){
        restaurantRepository.deleteRestaurantFromFirestore(uid);
    }

    public void updateRestaurantPlaceId(String placeId){
        //restaurantPlaceId = placeId;
        userRepository.updatePlaceId(placeId);
    }


    public void likeIncrement(String uid) {
        restaurantRepository.likeIncrement(uid);
    }
    public void likeDecrement(String uid) {
        restaurantRepository.likeDecrement(uid);
    }


}
