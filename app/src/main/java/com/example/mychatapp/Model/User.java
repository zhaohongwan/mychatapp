package com.example.mychatapp.Model;

//Building Encapsulation and Constructor for the user class
public class User {

    private String id;
    private String username;

    public User(String id, String username){
        this.id = id;
        this.username = username;
    }

    public User(){ }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
