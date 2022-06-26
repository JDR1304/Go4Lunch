package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.ui.MainActivityViewModel;
import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI;

    public ViewModelFactory(UserRepository userRepository, RestaurantRepository restaurantRepository,
                                 FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.fetchRestaurantInGoogleAPI = fetchRestaurantInGoogleAPI;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(userRepository, restaurantRepository, fetchRestaurantInGoogleAPI);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
