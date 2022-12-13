package com.aristy.gogocar;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

    public UserPreferences (){

    }
    public UserPreferences (Context context){
        SharedPreferences userdata = context.getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE);
        this.userID = userdata.getInt(UserPreferences.USER, UserPreferences.ID);
    }

    public int getUserID(){
        return userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }
}
