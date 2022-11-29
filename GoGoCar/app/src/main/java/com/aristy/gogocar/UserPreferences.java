package com.aristy.gogocar;

import android.app.Application;

public class UserPreferences extends Application {
    // Section DATA
    public static final String DATA = "data";

    // USER : (ID)
    public static final String USER = "user";
    public static String ID;

    // Section PREFERENCE
    // Custom Theme : (Light, dark, system)

    // Storage
    private String userID;

    public String getUserID(){
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }
}
