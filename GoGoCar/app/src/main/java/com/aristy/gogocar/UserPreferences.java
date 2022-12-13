package com.aristy.gogocar;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences extends Application {

    // Storage
    private int userID;
    private String userName;
    private String userEmail;
    private String userPhone;

    public UserPreferences (){
    }

    public void setUser(DBModelUser user){
        this.userID = user.getId();
        this.userName = user.getFullName();
        this.userEmail = user.getEmail();
        this.userPhone = user.getPhoneNumber();
    }

    public int getUserID(){
        return userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}

class UserSharedPreference {

    // Section DATA
    public static final String DATA = "data";

    // USER : (ID)
    public static final String USER = "user";
    public static int ID;

    // Section PREFERENCE
    // Custom Theme : (Light, dark, system)

    SharedPreferences userdata;

    UserSharedPreference (Context context){
        userdata = context.getSharedPreferences(DATA, MODE_PRIVATE);
    }

    int readUserID(){
        return userdata.getInt(USER, ID);
    }

    public DBModelUser readUser() {
        DBModelUser user = new DBModelUser();
        user.setId(userdata.getInt(USER, ID));
        //user.setFullName();
        //user.setEmail();
        //user.setPhoneNumber();
        return user;
    }

    void writeUser(DBModelUser user){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putInt(USER, user.getId());
        editor.apply();
    }

    void resetData(){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putInt(USER, 0);
        editor.apply();
    }


}
