package com.example.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.modelApiNearby.Geometry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurant";
    private static final String LIKE_NUMBER = "likeNumber";
    private static final String USER_ID = "userId";
    private static final String RESTAURANT_ID = "restaurantId";
    private MutableLiveData <Restaurant> restaurantById;
    private MutableLiveData <Integer> numberLikeByRestaurant;
    private MutableLiveData <List<Restaurant>> restaurantList;
    private static volatile RestaurantRepository instance;
    private CollectionReference collectionReference;
    private MutableLiveData <List<String>> usersWhoJoinRestaurant;

    private FirebaseFirestore firebaseFirestore;


    public RestaurantRepository(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }


    public CollectionReference getRestaurantsCollection() {
        return firebaseFirestore.collection(COLLECTION_NAME);

    }

    // Create Restaurant in Firestore
    public Restaurant createRestaurant(String uid, String name, String address, String pictureUrl,
                                       List<String> usersWhoChoseRestaurantById,List<String> usersWhoChoseRestaurantByName,
                                       List <String> favoriteRestaurantUsers, int likeNumber, Geometry geometry) {

        Restaurant restaurantToCreate = new Restaurant(uid, name, address,pictureUrl, usersWhoChoseRestaurantById,usersWhoChoseRestaurantByName, favoriteRestaurantUsers, likeNumber, geometry);

        Task<DocumentSnapshot> restaurantData = getRestaurantData(uid);
        // If the restaurant already exist in Firestore, we get his data
        restaurantData.addOnSuccessListener(documentSnapshot -> {
            this.getRestaurantsCollection().document(uid).set(restaurantToCreate);
        });

        return restaurantToCreate;
    }

    // Get Restaurant Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(String uid) {
        return this.getRestaurantsCollection().document(uid).get();
    }

    public LiveData <Restaurant> getRestaurantById (String placeId) {
        if (restaurantById == null) {
            restaurantById = new MutableLiveData<>();
        }
        DocumentReference docRef = getRestaurantsCollection().document(placeId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        //restaurant = document.getData();
                        restaurantById.setValue(restaurant);
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        return restaurantById;
    }


    public LiveData <List<String>> getUsersWhoJoinRestaurant (String placeId) {
        final List<String> users = new ArrayList<>();
        if (usersWhoJoinRestaurant == null) {
            usersWhoJoinRestaurant = new MutableLiveData<>();
        }
        DocumentReference docRef = getRestaurantsCollection().document(placeId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        for (int i = 0; i<restaurant.getUsersWhoChoseRestaurantById().size(); i++ ){
                            users.add(restaurant.getUsersWhoChoseRestaurantById().get(i));
                        }
                        usersWhoJoinRestaurant.setValue(users);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return usersWhoJoinRestaurant;
    }

    // Get List Restaurant from firestore
    public LiveData <List<Restaurant>> getRestaurantsList() {
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
                            Log.e(TAG, document.getId() + " => Resto repo " + document.getData());
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
        // Atomically increment the like number.
        likeIncrement.update(LIKE_NUMBER, FieldValue.increment(1));
    }

    public void likeDecrement(String uid) {
        DocumentReference likeDecrement = collectionReference.document(uid);
        // Atomically decrement the like number.
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

    public LiveData <Integer> getLikeByRestaurant (String uid){
        DocumentReference likeByRestaurant = collectionReference.document(uid);
        likeByRestaurant.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 numberLikeByRestaurant.setValue((Integer) documentSnapshot.get(LIKE_NUMBER));
            }
        });
        return null;
    }
}
