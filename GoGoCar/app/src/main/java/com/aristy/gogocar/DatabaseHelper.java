package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PHONE_NUMBER = "phone";
    private static final String COLUMN_USER_PASSWORD = "password";
    // id person (identity card table) (if null => not approved)

    private static final String TABLE_VEHICLE = "vehicles";
    private static final String COLUMN_VEHICLE_ID = "id";
    private static final String COLUMN_VEHICLE_MODEL = "model";
    private static final String COLUMN_VEHICLE_LICENCE_PLATE = "licence_plate";
    private static final String COLUMN_VEHICLE_ADDRESS = "address";
    private static final String COLUMN_VEHICLE_ID_OWNER = "id_owner";
    private static final String COLUMN_VEHICLE_IS_AVAILABLE = "is_available";
    private static final String COLUMN_VEHICLE_IS_BOOKED = "is_booked";
    private static final String COLUMN_VEHICLE_ID_USER_BOOK = "id_user_book";
    // id image
    // id module (stm 32 table)

    Connection connection;

    // Constructor
    public DatabaseHelper(Connection connection) {
        this.connection = connection;
    }

    public boolean addUser(DBModelUser userModel){
        String preparedQuery = "INSERT INTO " + TABLE_USER + "( " + COLUMN_USER_NAME + "," + COLUMN_USER_EMAIL + "," + COLUMN_USER_PHONE_NUMBER + "," + COLUMN_USER_PASSWORD + ") VALUES (?, ?, ?, ?)";
        try {
            if (connection != null) {
                PreparedStatement st = connection.prepareStatement(preparedQuery);
                // i is '?' position
                st.setString(1, userModel.getFullName());
                st.setString(2, userModel.getEmail());
                st.setString(3, userModel.getPhoneNumber());
                st.setString(4, userModel.getPassword());

                st.executeUpdate();
                st.close();
                return true;
            } else {
                // else: Failure. do not add anything to the list
                Log.e(TAG_Database, "addUser: connect is null");
                return false;
            }
        } catch (SQLException exception) {
            Log.e(TAG_Database, "addUser: ", exception);
            exception.printStackTrace();
            return false;
        }
    }

    public void deleteUser(DBModelUser userModel){
        // Find user in the database.
        String query = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + userModel.getId();

        try {
            if (connection != null) {
                Statement st = connection.createStatement();

                // If it found, delete it and return true.
                // If it is not found, return false.
                st.execute(query);

                // Close
                st.close();
            } else {
                // else: Failure. do not add anything to the list
                Log.e(TAG_Database, "deleteUser: connect is null");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Database, "deleteUser: ", exception);
            exception.printStackTrace();
        }
    }

    public List<DBModelUser> getAllUsers(){
        List<DBModelUser> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_USER;

        // Get data from database
        try {
            if (connection != null) {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    // Loop through the cursor (result set) and create new user objects. Put them into the return list.
                    int userID = rs.getInt(1);
                    String userName = rs.getString(2);
                    String userEmail = rs.getString(3);
                    String userPhone = rs.getString(4);
                    String userHash = rs.getString(5);
                    //int userIdentityID = rs.getInt(6);

                    DBModelUser user = new DBModelUser(userID, userName, userEmail, userPhone, userHash);
                    Log.i(TAG_Database, user.toString());
                    returnList.add(user);
                }

                // Close both result and the statement
                rs.close();
                st.close();
            } else {
                // else: Failure. do not add anything to the list
                Log.e(TAG_Database, "getAllUsers: is null");
            }
        }catch (Exception exception){
            Log.e(TAG_Database, "getAllUsers: ", exception);
        }

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

    private DBModelUser getUser(String query){
        DBModelUser user = new DBModelUser();

        try {
            if (connection != null) {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                if (rs.next()) {
                    int user_id = rs.getInt(1);
                    String name = rs.getString(2);
                    String email = rs.getString(3);
                    String phone = rs.getString(4);
                    String hash = rs.getString(5);
                    //int identity_id = rs.getInt(6);

                    user = new DBModelUser(user_id, name, email, phone, hash);
                    Log.i(TAG_Database, "getUser: " + user);
                }

                rs.close();
                st.close();
            } else {
                // else: Failure. do not add anything to the list
                Log.e(TAG_Database, "getUser: connect is null");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Database, "getUser: ", exception);
            exception.printStackTrace();
        }
        return user;
    }

    /* Vehicles */

    /**
     * @return List of all vehicles
     */
    public List<DBModelVehicle> getAllVehicles(){
        String query = "SELECT * FROM " + TABLE_VEHICLE;
        return getVehicles(query);
    }

    /**
     * @param IDUser actual user
     * @return List of vehicles which are not owned by the user and available.
     */
    public List<DBModelVehicle> getVehiclesAvailable(int IDUser) {
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_IS_AVAILABLE + " = true AND " + COLUMN_VEHICLE_ID_OWNER + " != " + IDUser;
        return getVehicles(query);
    }

    /**
     * @param IDUser actual user
     * @return List of all vehicles owned by the user
     */
    public List<DBModelVehicle> getVehiclesByUser(int IDUser){
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID_OWNER + " = " + IDUser;
        return getVehicles(query);
    }

    /**
     * @param ID id vehicle
     * @return return vehicle matching id
     */
    public DBModelVehicle getVehicleById(int ID){
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID + " = " + ID;
        return getVehicle(query);
    }

    /**
     * @param query sql query
     * @return list of vehicles
     */
    private List<DBModelVehicle> getVehicles(String query){
        List<DBModelVehicle> returnList = new ArrayList<>();

        // Get data from database
        try {
            if (connection != null) {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    int vehicle_id = rs.getInt(1);
                    String model = rs.getString(2);
                    String licencePlate = rs.getString(3);
                    String address = rs.getString(4);
                    int idOwner = rs.getInt(5);
                    boolean isAvailable = rs.getBoolean(6);
                    boolean isBooked = rs.getBoolean(7);
                    int idUser = rs.getInt(8);

                    DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, address, idOwner, isAvailable, isBooked, idUser);
                    Log.i(TAG_Database, vehicle.toString());
                    returnList.add(vehicle);
                }

                // Close both cursor and the database
                rs.close();
                st.close();
            } else {
                Log.e(TAG_Database, "getAllVehicles: connect is null");
            }
        }catch (Exception exception){
            Log.e(TAG_Database, "getAllVehicles: " , exception);
            exception.printStackTrace();
        }
        return returnList;
    }

    private DBModelVehicle getVehicle(String query){
        DBModelVehicle vehicle = new DBModelVehicle();

        try {
            if (connection == null) {
                Log.e(TAG_Database, "getVehicle: connect is null");
                return vehicle;
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            if (rs.next()){
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                String address = rs.getString(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);

                vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, address, idOwner, isAvailable, isBooked, idUser);
                Log.i(TAG_Database, vehicle.toString());
            }

            rs.close();
            st.close();
        } catch (SQLException exception) {
            Log.e(TAG_Database, "getVehicle: " , exception);
            exception.printStackTrace();
        }
        return vehicle;
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
    @NonNull
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
    public DBModelVehicle(int id, String model, String licencePlate, String address, int idOwner, boolean isAvailable, boolean isBooked, int idUser) {
        this.id = id;
        this.model = model;
        this.licencePlate = licencePlate;
        this.address = address;
        this.idOwner = idOwner;
        this.isAvailable = isAvailable;
        this.isBooked = isBooked;
        this.idUser = idUser;
    }

    public DBModelVehicle() {
    }

    // toString is necessary for printing the contents of a class object
    @NonNull
    @Override
    public String toString() {
        JSONObject map = new JSONObject();
        try {
            map.put("id", id);
            map.put("name", model);
            map.put("licencePlate", licencePlate);
            map.put("address", address);
            map.put("idOwner", idOwner);
            map.put("isAvailable", isAvailable);
            map.put("isBooked", isBooked);
            map.put("idUser", idUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map.toString();
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}