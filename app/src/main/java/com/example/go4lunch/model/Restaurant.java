package com.example.go4lunch.model;

import java.util.List;

public class Restaurant {

    private String uid;
    private String name;
    private String address;
    private List <String> usersWhoChoseRestaurant;
    private List <String> favoriteRestaurantUsers;
    private int likeNumber;


    public Restaurant(String uid,String name, String address, List<String> usersWhoChoseRestaurant,
                      List <String> favoriteRestaurantUsers, int likeNumber ) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.usersWhoChoseRestaurant = usersWhoChoseRestaurant;
        this.favoriteRestaurantUsers = favoriteRestaurantUsers;
        this.likeNumber = likeNumber;
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

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

}
