package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GoGoCar.db";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PHONE_NUMBER = "phoneNumber";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // This is called the first time a database is accessed. There should be code in here to create a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUser = "CREATE TABLE " + TABLE_USER + " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_USER_PHONE_NUMBER + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT)";

        db.execSQL(createTableUser);
    }

    // This is called if the database version number change.
    // It pretend previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addUser(DBModelUser userModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_NAME, userModel.getFullName());
        cv.put(COLUMN_USER_EMAIL, userModel.getEmail());
        cv.put(COLUMN_USER_PHONE_NUMBER, userModel.getPhoneNumber());
        cv.put(COLUMN_USER_PASSWORD, userModel.getPassword());

        long result = db.insert(TABLE_USER, null, cv);
        if(result == -1) return false;
        else return true;
    }

    public boolean deleteUser(DBModelUser userModel){
        // Find user in the database.
        String query = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + userModel.getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // If it found, delete it and return true.
        // If it is not found, return false.
        boolean result = cursor.moveToFirst();

        // Close both cursor and the database
        cursor.close();
        db.close();
        return result;
    }

    public List<DBModelUser> getAllUsers(){
        List<DBModelUser> returnList = new ArrayList<>();

        // Get data from database
        String query = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // else: Failure. do not add anything to the list
        if (cursor.moveToFirst()){
            // Loop through the cursor (result set) and create new user objects. Put them into the return list.
            do {
                int userID = cursor.getInt(0);
                String userName = cursor.getString(1);
                String userEmail = cursor.getString(2);
                String userPhone = cursor.getString(3);
                String userHash = cursor.getString(4);

                DBModelUser user = new DBModelUser(userID, userName, userEmail, userPhone, userHash);
                returnList.add(user);

            } while (cursor.moveToNext());
        }

        // Close both cursor and the database
        cursor.close();
        db.close();
        return returnList;
    }

    public DBModelUser getUserById(int ID){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + ID;
        return getUser(query);
    }

    public DBModelUser getUserByEmail(String email){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "';";
        return getUser(query);
    }

    public DBModelUser getUserByPhone(String phone){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_PHONE_NUMBER + " = '" + phone + "';";
        return getUser(query);
    }

    public DBModelUser getUser(String query){
        // Get data from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        DBModelUser user = new DBModelUser();

        if (cursor.moveToFirst()){
            int user_id = cursor.getInt(0);
            String userName = cursor.getString(1);
            String userEmail = cursor.getString(2);
            String userPhone = cursor.getString(3);
            String userHash = cursor.getString(4);
            user = new DBModelUser(user_id, userName, userEmail, userPhone, userHash);
        }

        // Close both cursor and the database
        cursor.close();
        db.close();
        return user;
    }
    
}

class DBModelUser {

    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    // Constructor
    public DBModelUser(int id, String fullName, String email, String phoneNumber, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public DBModelUser(){}

    // toString is necessary for printing the contents of a class object
    @Override
    public String toString() {
        return "DBModelUser{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class DBModelVehicle {

    private int id;
    private String model;
    private String licencePlate;
    private String address;
    private int idOwner;    // User Id
    private boolean isAvailable;
    private boolean isBooked;
    private int idUser;

    // Constructor
    public DBModelVehicle(int id, String model, String licencePlate, String address, int idOwner) {
        this.id = id;
        this.model = model;
        this.licencePlate = licencePlate;
        this.address = address;
        this.idOwner = idOwner;
    }

    public DBModelVehicle() {
    }

    // toString is necessary for printing the contents of a class object

    @Override
    public String toString() {
        return "DBModelVehicle{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", address='" + address + '\'' +
                ", idOwner=" + idOwner +
                '}';
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }
}