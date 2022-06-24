package com.example.go4lunch.injection;

import android.content.Context;

import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

public class Injection {

    public static UserRepository userRepositoryDataSource() {
        return UserRepositoryInjection.userRepositoryDataSource();
    }

    public static RestaurantRepository restaurantRepositoryDataSource() {
        return RestaurantRepositoryInjection.RestaurantRepositoryDataSource();
    }

    public static FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPIDataSource() {
        return FetchRestaurantInGoogleAPI.getInstance();
    }

    public static ViewModelFactory provideViewModelFactory(){
        UserRepository userRepository = userRepositoryDataSource();
        RestaurantRepository restaurantRepository = restaurantRepositoryDataSource();
        FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI = fetchRestaurantInGoogleAPIDataSource();
        return new ViewModelFactory(userRepository, restaurantRepository, fetchRestaurantInGoogleAPI);
    }
}
