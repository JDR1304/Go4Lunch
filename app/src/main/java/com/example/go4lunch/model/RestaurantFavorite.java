package com.example.go4lunch.model;

import java.util.List;

public class RestaurantFavorite {

    private String uid;
    private int likeNumber;


    public RestaurantFavorite(String uid, int likeNumber) {
        this.uid = uid;
        this.likeNumber = likeNumber;
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
