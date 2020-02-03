package com.example.drinkup.objects;

public class User {
    private static final User ourInstance = new User();
    private String id;
    public static User getInstance() {
        return ourInstance;
    }

    private User() {
    }

    public void setID(String n_id) {id=n_id;}

    public String getID() {
        return id;
    }
}
