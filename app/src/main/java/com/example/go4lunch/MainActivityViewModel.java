package com.example.go4lunch;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Geometry;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public String apiKey;

    public Restaurant restaurantBookedByUser;

    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    private UserRepository userRepository = UserRepository.getInstance();

    private RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();

    public FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI = FetchRestaurantInGoogleAPI.getInstance();

    public MutableLiveData<Location> getLocation() {
        return locationLiveData;
    }

    public void setLocation(Location location) {
        this.locationLiveData.postValue(location);
    }

    public LiveData<List<Result>> getRestaurants() {
        return fetchRestaurantInGoogleAPI.getRestaurants(locationLiveData.getValue(), getApiKey());
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

    public String updatePlaceIdChoseByCurrentUserInFirestore(String placeId){
        userRepository.updatePlaceIdChoseByCurrentUserInFirestore(placeId);
        return placeId;
    }

    public String updateRestaurantNameChoseByCurrentUserInFirestore(String name){
        userRepository.updateRestaurantNameChoseByCurrentUserInFirestore(name);
        return name;
    }


    public LiveData <String> getChosenRestaurantByUserFromFirestore (String userUid) {
        return userRepository.getChosenRestaurantIdFromUser(userUid);
    }

    public LiveData <String> getChosenRestaurantNameUserFromFirestore (String userUid) {
        return userRepository.getChosenRestaurantNameFromUser(userUid);
    }


//------------------------------------------------------------------------------------------------
//RestaurantRepository

    public Restaurant createRestaurantInFirestore(String uid, String name, String address, String pictureUrl, List<String> usersWhoChoseRestaurant,
                                            List<String> favoriteRestaurantUsers, int likeNumber, Geometry geometry){
        return restaurantRepository.createRestaurant(uid, name, address,pictureUrl, usersWhoChoseRestaurant, favoriteRestaurantUsers, likeNumber, geometry);
    }

    public LiveData<List<Restaurant>> getRestaurantListFromFirestore() {
        return restaurantRepository.getRestaurantsList();
    }

    public LiveData <Restaurant> getRestaurantByIdFromFirestore(String restaurantPlaceId){
        return  restaurantRepository.getRestaurantById(restaurantPlaceId);
    }

    public void deleteRestaurantInFirestore(String uid){
        restaurantRepository.deleteRestaurantFromFirestore(uid);
    }

    public Restaurant setRestaurantBooking(Restaurant restaurant) {
        restaurantBookedByUser = restaurant;
        return restaurantBookedByUser;
    }

    public Restaurant getRestaurantBooking() {
        return restaurantBookedByUser;
    }

    public LiveData <List<String>> getUsersWhoJoinRestaurantFromFirestore(String restaurantPlaceId){
        return restaurantRepository.getUsersWhoJoinRestaurant(restaurantPlaceId);
    }

    public void likeIncrement(String uid) {
        restaurantRepository.likeIncrement(uid);
    }
    public void likeDecrement(String uid) {
        restaurantRepository.likeDecrement(uid);
    }

}

