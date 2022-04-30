package com.example.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.RestaurantFavorite;
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

    private static final String COLLECTION_NAME = "restaurantFavorites";
    private static final String LIKE_NUMBER = "likeNumber";
    private static final String USER_ID = "userId";
    private MutableLiveData <Integer> numberLikeByRestaurant;
    private MutableLiveData<List<RestaurantFavorite>> restaurantList;
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
    public void createRestaurant(String uid, int likeNumber) {

        RestaurantFavorite restaurantFavoriteToCreate = new RestaurantFavorite(uid, likeNumber);

        Task<DocumentSnapshot> restaurantData = getRestaurantData(uid);
        // If the restaurant already exist in Firestore, we get his data
        restaurantData.addOnSuccessListener(documentSnapshot -> {
            this.getRestaurantsCollection().document(uid).set(restaurantFavoriteToCreate);
        });

    }

    // Get Restaurant Data from Firestore
    public Task<DocumentSnapshot> getRestaurantData(String uid) {
        return this.getRestaurantsCollection().document(uid).get();
    }

    // Get List Restaurant from firestore
    public LiveData<List<RestaurantFavorite>> getRestaurantsList() {
        List<RestaurantFavorite> restaurantFavorites = new ArrayList<>();
        if (restaurantList == null) {
            restaurantList = new MutableLiveData<>();
        }
        this.getRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RestaurantFavorite restaurantFavorite = document.toObject(RestaurantFavorite.class);
                            restaurantFavorites.add(restaurantFavorite);
                            // MutableLivedata
                            restaurantList.setValue(restaurantFavorites);
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
