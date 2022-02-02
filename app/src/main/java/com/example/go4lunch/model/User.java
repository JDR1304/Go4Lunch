package com.example.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String useremail;
    private String urlPicture;

    public User() { }

    public User(String uid, String email, String name, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = name;
        this.useremail = email;
        this.urlPicture = urlPicture;
    }

    //-----GETTERS-----
    public String getUid() {return uid;}
    public String getName() {return username;}
    public String getEmail() {return useremail;}
    public String getUrlPicture() {return urlPicture;}

    //-----SETTERS-----
    public void setUid(String uid) {this.uid = uid;}
    public void setName(String name) {this.username = name;}
    public void setEmail(String useremail) {this.useremail = useremail;}
    public void setUrlPicture(String urlPicture) {this.urlPicture = urlPicture;}

}
