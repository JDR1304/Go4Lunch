package com.example.go4lunch.injection;

import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantRepositoryInjection {
    static RestaurantRepository sRestaurantRepository;

    public static RestaurantRepository RestaurantRepositoryDataSource() {
        RestaurantRepository result = sRestaurantRepository;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (sRestaurantRepository == null) {
                sRestaurantRepository = new RestaurantRepository(FirebaseFirestore.getInstance());
            }
            return sRestaurantRepository;
        }
    }
}
