package com.example.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String useremail;
    private String urlPicture;
    private String restaurantPlaceId;

    public User() {
    }

    public User(String uid, String email, String name, @Nullable String urlPicture, String restaurantPlaceId) {
        this.uid = uid;
        this.username = name;
        this.useremail = email;
        this.urlPicture = urlPicture;
        this.restaurantPlaceId = restaurantPlaceId;

    }

    //-----GETTERS-----
    public String getUid() {
        return uid;
    }

    public String getName() {
        return username;
    }

    public String getEmail() {
        return useremail;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }

    //-----SETTERS-----
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.username = name;
    }

    public void setEmail(String useremail) {
        this.useremail = useremail;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }


}
