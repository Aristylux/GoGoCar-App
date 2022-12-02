package com.aristy.gogocar;

import android.app.Application;

public class UserPreferences extends Application {
    // Section DATA
    public static final String DATA = "data";

    // USER : (ID)
    public static final String USER = "user";
    public static int ID;

    // Section PREFERENCE
    // Custom Theme : (Light, dark, system)

    // Storage
    private int userID;

    public int getUserID(){
        return userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }
}
