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

    // Section DATA (file name)
    public static final String DATA = "user";

    // USER : (ID)
    public static final String USER_ID = "id";
    public static int ID;

    public static final String USER_NAME = "name";
    public static String NAME;

    public static final String USER_EMAIL = "email";
    public static String EMAIL;

    public static final String USER_PHONE = "phone";
    public static String PHONE;

    // Section PREFERENCE
    // Custom Theme : (Light, dark, system)

    SharedPreferences userdata;

    UserSharedPreference (Context context){
        userdata = context.getSharedPreferences(DATA, MODE_PRIVATE);
    }

    int readUserID(){
        return userdata.getInt(USER_ID, ID);
    }

    public DBModelUser readUser() {
        DBModelUser user = new DBModelUser();
        user.setId(userdata.getInt(USER_ID, ID));
        user.setFullName(userdata.getString(USER_NAME, NAME));
        user.setEmail(userdata.getString(USER_EMAIL, EMAIL));
        user.setPhoneNumber(userdata.getString(USER_PHONE, PHONE));
        return user;
    }

    public void writeUser(DBModelUser user){
        write(user.getId(), user.getFullName(), user.getEmail(), user.getPhoneNumber());
    }

    public void resetData(){
        write(0, null, null, null);
    }

    private void write(int id, String name, String email, String phone){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putInt(USER_ID, id);
        editor.putString(USER_NAME, name);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_PHONE, phone);
        editor.apply();
    }
}
