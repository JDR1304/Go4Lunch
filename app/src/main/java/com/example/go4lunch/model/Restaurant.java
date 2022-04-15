package com.example.go4lunch.model;

import java.util.List;

public class Restaurant {

    private String uid;
    private int likeNumber;
    private List <String> userId;

    public Restaurant(String uid, int likeNumber, List<String> userId) {
        this.uid = uid;
        this.likeNumber = likeNumber;
        this.userId = userId;
    }

    public String getUid(){
        return uid;
    }
    public int getLikeNumber() {
        return likeNumber;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
}
