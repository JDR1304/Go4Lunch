package com.example.go4lunch;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkManager;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Geometry;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public WorkManager workManager;

    public MutableLiveData<String> sortingType;

    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    public MutableLiveData<List<String>> establishmentPrediction = new MutableLiveData<>();

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
        return fetchRestaurantInGoogleAPI.getRestaurants(locationLiveData.getValue(), BuildConfig.ApiKey);
    }


    // Why return LiveData because I don't want change the data outside the viewModel
    public LiveData<List<User>> getUsers() {
        return userRepository.getUsersList();
    }

    public String getCurrentUserUid() {
        return userRepository.getCurrentUserUID();
    }

    public String getCurrentUserName() {
        return userRepository.getCurrentUserName();
    }

    public String updatePlaceIdChoseByCurrentUserInFirestore(String placeId) {
        userRepository.updatePlaceIdChoseByCurrentUserInFirestore(placeId);
        return placeId;
    }

    public String updateRestaurantNameChoseByCurrentUserInFirestore(String name) {
        userRepository.updateRestaurantNameChoseByCurrentUserInFirestore(name);
        return name;
    }

    public LiveData<String> getChosenRestaurantByUserFromFirestore(String userUid) {
        return userRepository.getChosenRestaurantIdFromUser(userUid);
    }


//------------------------------------------------------------------------------------------------
//RestaurantRepository

    public Restaurant createRestaurantInFirestore(String uid, String name, String address, String pictureUrl, List<String> usersWhoChoseRestaurantById,
                                                  List<String> usersWhoChoseRestaurantByName, List<String> favoriteRestaurantUsers, int likeNumber, Geometry geometry) {
        return restaurantRepository.createRestaurant(uid, name, address, pictureUrl, usersWhoChoseRestaurantById, usersWhoChoseRestaurantByName, favoriteRestaurantUsers, likeNumber, geometry);
    }

    public LiveData<List<Restaurant>> getRestaurantListFromFirestore() {
        return restaurantRepository.getRestaurantsList();
    }

    public LiveData<Restaurant> getRestaurantByIdFromFirestore(String restaurantPlaceId) {
        return restaurantRepository.getRestaurantById(restaurantPlaceId);
    }

    public void deleteRestaurantInFirestore(String uid) {
        restaurantRepository.deleteRestaurantFromFirestore(uid);
    }

    public void likeIncrement(String uid) {
        restaurantRepository.likeIncrement(uid);
    }

    public void likeDecrement(String uid) {
        restaurantRepository.likeDecrement(uid);
    }


    // Management of the prediction list
    public void setPredictionEstablishmentList(List<String> predictionList) {
        establishmentPrediction.setValue(predictionList);
    }

    public LiveData<List<String>> getPredictionEstablishmentList() {
        return establishmentPrediction;
    }

    // WorkManager and notification
    public void getNotification(Context context) {
        if (workManager == null) {
            workManager = WorkManager.getInstance(context);
        }
        UploadWorker.scheduleWorker(workManager);
    }


    public void cancelNotification(Context context) {
        if (workManager == null) {
            workManager = WorkManager.getInstance(context);
        }
        workManager.cancelAllWorkByTag("WORKER_ID");
    }

    public void setSortingType(String typeOfSorting) {
        if (sortingType==null){
            sortingType = new MutableLiveData<>();
        }
        sortingType.postValue(typeOfSorting);
    }

    public LiveData<String>  getSortingType(){
        return sortingType;
    }

}

