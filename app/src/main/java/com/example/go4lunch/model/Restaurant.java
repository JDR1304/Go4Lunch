package com.example.go4lunch.model;

import android.net.Uri;

import androidx.lifecycle.Observer;

import com.example.go4lunch.modelApiNearby.Geometry;
import com.example.go4lunch.ui.RestaurantDetailsFragment;

import java.util.List;

public class Restaurant {

    private String uid;
    private String name;
    private String address;
    private String pictureUrl;
    private List <String> usersWhoChoseRestaurant;
    private List <String> favoriteRestaurantUsers;
    private Geometry geometry;
    private int likeNumber;

    public Restaurant() {}

    public Restaurant(String uid, String name, String address, String pictureUrl, List<String> usersWhoChoseRestaurant,
                      List <String> favoriteRestaurantUsers, int likeNumber, Geometry geometry ) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.usersWhoChoseRestaurant = usersWhoChoseRestaurant;
        this.favoriteRestaurantUsers = favoriteRestaurantUsers;
        this.likeNumber = likeNumber;
        this.pictureUrl = pictureUrl;
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPictureUrl() { return pictureUrl; }

    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

    public List<String> getUsersWhoChoseRestaurant() {
        return usersWhoChoseRestaurant;
    }

    public void setUsersWhoChoseRestaurant(List<String> usersWhoChoseRestaurant) {
        this.usersWhoChoseRestaurant = usersWhoChoseRestaurant;
    }

    public List<String> getFavoriteRestaurantUsers() {
        return favoriteRestaurantUsers;
    }

    public void setFavoriteRestaurantUsers(List<String> favoriteRestaurantUsers) {
        this.favoriteRestaurantUsers = favoriteRestaurantUsers;
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }


}
