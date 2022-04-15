package com.example.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String LIKE_NUMBER = "likeNumber";
    private static final String USER_ID = "userId";
    private MutableLiveData<List<Restaurant>> restaurantList;
    private static volatile RestaurantRepository instance;
    private CollectionReference collectionReference;


    private RestaurantRepository() {
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    public CollectionReference getRestaurantsCollection() {
        collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
        return collectionReference;
    }

    // Create Restaurant in Firestore
    public void createRestaurant(String uid, int likeNumber, List<String> userId) {

        Restaurant restaurantToCreate = new Restaurant(uid, likeNumber, userId);

        Task<DocumentSnapshot> restaurantData = getRestaurantData(uid);
        // If the restaurant already exist in Firestore, we get his data
        restaurantData.addOnSuccessListener(documentSnapshot -> {
            this.getRestaurantsCollection().document(uid).set(restaurantToCreate);
        });

    }

    // Get Restaurant Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(String uid) {
        return this.getRestaurantsCollection().document(uid).get();
    }

    // Get List Restaurant from firestore
    public LiveData<List<Restaurant>> getRestaurantsList() {
        List<Restaurant> restaurants = new ArrayList<>();
        if (restaurantList == null) {
            restaurantList = new MutableLiveData<>();
        }
        this.getRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            restaurants.add(restaurant);
                            // MutableLivedata
                            restaurantList.setValue(restaurants);
                            Log.e(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.e("error", "Task is not successful");
                    }
                });

        return restaurantList;
    }

    // Delete the Restaurant from Firestore
    public void deleteRestaurantFromFirestore(String uid) {
        this.getRestaurantsCollection().document(uid).delete();
    }

    public void likeIncrement(String uid) {
        DocumentReference likeIncrement = collectionReference.document(uid);
        // Automatically increment the like number.
        likeIncrement.update(LIKE_NUMBER, FieldValue.increment(1));
    }

    public void likeDecrement(String uid) {
        DocumentReference likeDecrement = collectionReference.document(uid);
        // Automatically decrement the like number.
        likeDecrement.update(LIKE_NUMBER, FieldValue.increment(-1));

    }

    public void addUserIdToRestaurant(String uid, String userId) {
        DocumentReference addUserId = collectionReference.document(uid);
        // Atomically add a new region to the "regions" array field.
        addUserId.update(USER_ID, FieldValue.arrayUnion(userId));

    }

    public void deleteUserIdToRestaurant(String uid, String userId) {
        DocumentReference deleteUserId = collectionReference.document(uid);
        // Atomically remove a region from the "regions" array field.
        deleteUserId.update(USER_ID, FieldValue.arrayRemove(userId));
    }
}
