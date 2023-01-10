package com.aristy.gogocar;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class UserPreferences extends Application implements Parcelable {

    // Storage
    private int userID;
    private String userName;
    private String userEmail;
    private String userPhone;

    UserSharedPreference userSharedPreference;
    
    public UserPreferences(Context context){
        this.userSharedPreference = new UserSharedPreference(context);
    }

    protected UserPreferences(Parcel in) {
        userID = in.readInt();
        userName = in.readString();
        userEmail = in.readString();
        userPhone = in.readString();
    }

    public static final Creator<UserPreferences> CREATOR = new Creator<UserPreferences>() {
        @Override
        public UserPreferences createFromParcel(Parcel in) {
            return new UserPreferences(in);
        }

        @Override
        public UserPreferences[] newArray(int size) {
            return new UserPreferences[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        JSONObject map = new JSONObject();
        try {
            map.put("id", userID);
            map.put("name", userName);
            map.put("email", userEmail);
            map.put("phone", userPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map.toString();
    }

    public void setUser(DBModelUser user){
        this.userID = user.getId();
        this.userName = user.getFullName();
        this.userEmail = user.getEmail();
        this.userPhone = user.getPhoneNumber();
        writeUserInShared();
    }

    public void setUser(Context context, DBModelUser user){
        this.userID = user.getId();
        this.userName = user.getFullName();
        this.userEmail = user.getEmail();
        this.userPhone = user.getPhoneNumber();
        writeUserInShared(context);
    }
    
    public DBModelUser getUser(){
        DBModelUser user = new DBModelUser();
        user.setId(userID);
        user.setFullName(userName);
        user.setEmail(userEmail);
        user.setPhoneNumber(userPhone);
        return user;
    }

    public void readUser(){
        DBModelUser user = userSharedPreference.readUser();
        this.userID = user.getId();
        this.userName = user.getFullName();
        this.userEmail = user.getEmail();
        this.userPhone = user.getPhoneNumber();
    }
    
    private void writeUserInShared(){
        userSharedPreference.writeUser(getUser());
    }

    private void writeUserInShared(Context context){
        UserSharedPreference user = new UserSharedPreference(context);
        user.writeUser(getUser());
    }

    /**
     * Delete user in shared preferences
     */
    public void resetUser(){
        userSharedPreference.resetData();
    }

    public void resetUser(Context context){
        UserSharedPreference user = new UserSharedPreference(context);
        user.resetData();
    }
    
    // Setters & Getters 
    
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(userID);
        parcel.writeString(userName);
        parcel.writeString(userEmail);
        parcel.writeString(userPhone);
    }
}

class UserSharedPreference {

    // Section DATA (file name)
    private static final String DATA = "user";

    // USER : (ID)
    private static final String USER_ID = "id";
    private int ID;

    private static final String USER_NAME = "name";
    private String NAME;

    private static final String USER_EMAIL = "email";
    private String EMAIL;

    private static final String USER_PHONE = "phone";
    private String PHONE;

    // Section PREFERENCE
    // Custom Theme : (Light, dark, system)

    private static SharedPreferences userdata;

    public UserSharedPreference (Context context){
        userdata = context.getSharedPreferences(DATA, MODE_PRIVATE);
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
