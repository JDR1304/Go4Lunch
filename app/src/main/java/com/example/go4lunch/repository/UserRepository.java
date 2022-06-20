package com.example.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserRepository {


    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String EMAIL_FIELD = "useremail";
    private static final String RESTAURANT_PLACE_ID = "restaurantPlaceId";
    private static final String RESTAURANT_NAME = "restaurantName";
    private MutableLiveData<String> chosenRestaurantNameByUser;
    private MutableLiveData<List<User>> usersList;
    private MutableLiveData<String> chosenRestaurantByUser;
    private static volatile UserRepository instance;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public UserRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }

    /*public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }*/

    @Nullable
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    public CollectionReference getUsersCollection() {
        return firebaseFirestore.collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {

            String uid = user.getUid();
            String useremail = user.getEmail();
            String username = user.getDisplayName();
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            User userToCreate = new User(uid, useremail, username, urlPicture, null, null );

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });

        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public String getCurrentUserName() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getDisplayName() : null;
    }

    public Task<Void> updatePlaceIdChoseByCurrentUserInFirestore(String placeId){
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(RESTAURANT_PLACE_ID, placeId);
        } else {
            return null;
        }
    }
    public Task<Void> updateRestaurantNameChoseByCurrentUserInFirestore(String name){
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(RESTAURANT_NAME, name);
        } else {
            return null;
        }
    }

    // Update User Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }


    public LiveData <List<User>> getUsersList() {
        List<User> users = new ArrayList<>();
        if (usersList == null) {
            usersList = new MutableLiveData<>();
        }
        this.getUsersCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users.add(user);
                            // MutableLivedata
                            usersList.setValue(users);
                            //Log.e(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.e("error", "Task is not successful");
                    }
                });

        return usersList;
    }

    public LiveData <String> getChosenRestaurantNameFromUser(String userUid){
        if (chosenRestaurantNameByUser == null) {
            chosenRestaurantNameByUser = new MutableLiveData<>();
        }
        DocumentReference docRef = getUsersCollection().document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        chosenRestaurantNameByUser.setValue(document.getString(RESTAURANT_NAME));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return chosenRestaurantByUser;
    }

    public LiveData <String> getChosenRestaurantIdFromUser(String userUid){
        if (userUid != null){
            if (chosenRestaurantByUser == null) {
                chosenRestaurantByUser = new MutableLiveData<>();
            }
            DocumentReference docRef = getUsersCollection().document(userUid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            chosenRestaurantByUser.setValue(document.getString(RESTAURANT_PLACE_ID));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        return chosenRestaurantByUser;
    }
    // Delete the User from Firestore
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }

}
