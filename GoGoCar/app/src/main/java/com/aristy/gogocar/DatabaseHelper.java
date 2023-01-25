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

    /* USER */
    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PHONE_NUMBER = "phone";
    private static final String COLUMN_USER_PASSWORD = "password";
    // id person (identity card table) (if null => not approved)

    /* VEHICLE */
    private static final String TABLE_VEHICLE = "vehicles";
    private static final String COLUMN_VEHICLE_ID = "id";
    private static final String COLUMN_VEHICLE_MODEL = "model";
    private static final String COLUMN_VEHICLE_LICENCE_PLATE = "licence_plate";
    private static final String COLUMN_VEHICLE_ADDRESS = "address";
    private static final String COLUMN_VEHICLE_ID_OWNER = "id_owner";
    private static final String COLUMN_VEHICLE_IS_AVAILABLE = "is_available";
    private static final String COLUMN_VEHICLE_IS_BOOKED = "is_booked";
    private static final String COLUMN_VEHICLE_ID_USER_BOOK = "id_user_book";
    private static final String COLUMN_VEHICLE_ID_MODULE = "id_module";
    // id image

    /* MODULE */
    private static final String TABLE_MODULE = "modules";
    private static final String COLUMN_MODULE_ID = "id";
    private static final String COLUMN_MODULE_NAME = "name";
    private static final String COLUMN_MODULE_MAC_ADDRESS = "mac_address";

    Connection connection;

    // Constructor
    public DatabaseHelper(Connection connection) {
        this.connection = connection;
    }

    /**
     * @param preparedQuery query to execute
     * @param elements element for '?' in preparedQuery
     * @return success
     */
    private boolean executeQuery(String preparedQuery, Object... elements){
        // Test if the connection is ok
        if (connection == null) {
            // Failure. do not add anything to the list
            Log.e(TAG_Database, "executeQuery: connect is null");
            return false;
        }

        try {
            PreparedStatement st = connection.prepareStatement(preparedQuery);
            for(int i = 1; i < elements.length + 1; i++ ){
                st.setObject(i, elements[i-1]);
            }

            // If it found, delete it and return true.
            // If it is not found, return false.
            int rowAffected = st.executeUpdate();

            // Close
            st.close();

            // If almost 1 row is affected, return true
            return rowAffected != 0;
        } catch (SQLException exception) {
            Log.e(TAG_Database, "executeQuery: ", exception);
            exception.printStackTrace();
            return false;
        }
    }

    /*  ---------------------------------- *
     *  --             USER             -- *
     *  ---------------------------------- */

    /**
     * @param userModel user with name, phone, ...
     * @return the success:<br>
     *         - true  - if success<br>
     *         - false - if not connection or exception
     */
    public boolean addUser(DBModelUser userModel){
        String query = "INSERT INTO " + TABLE_USER +
                "( " + COLUMN_USER_NAME + "," + COLUMN_USER_EMAIL + "," + COLUMN_USER_PHONE_NUMBER + "," + COLUMN_USER_PASSWORD + ") " +
                "VALUES (?,?,?,?)";
        return executeQuery(query, userModel.getFullName(), userModel.getEmail(), userModel.getPhoneNumber(), userModel.getPassword());
    }

    /**
     * @param userModel user to delete
     * @return the success
     */
    public boolean deleteUser(DBModelUser userModel){
        String query = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ?";
        return executeQuery(query, userModel.getId());
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

    /**
     * @param ID user id
     * @return the user that matches this ID
     */
    public DBModelUser getUserById(int ID){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + ID;
        return getUser(query);
    }

    /**
     * @param email user email
     * @return the user that matches this email
     */
    public DBModelUser getUserByEmail(String email){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "';";
        return getUser(query);
    }

    /**
     * @param phone user phone
     * @return the user that matches this phone
     */
    public DBModelUser getUserByPhone(String phone){
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_PHONE_NUMBER + " = '" + phone + "';";
        return getUser(query);
    }

    /**
     * @param query query to execute
     * @return one user (the first row result)
     */
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

    /*  ---------------------------------- *
     *  --            VEHICLES          -- *
     *  ---------------------------------- */

    /**
     * add a vehicle into the database
     * @param modelVehicle the vehicle
     * @return the success:<br>
     *         - true  - if success<br>
     *         - false - if not connection or exception
     */
    public boolean addVehicle(DBModelVehicle modelVehicle){
        String query = "INSERT INTO " + TABLE_VEHICLE +
                "( " + COLUMN_VEHICLE_MODEL + "," + COLUMN_VEHICLE_LICENCE_PLATE + "," + COLUMN_VEHICLE_ADDRESS + "," + COLUMN_VEHICLE_ID_OWNER + "," + COLUMN_VEHICLE_IS_AVAILABLE + "," + COLUMN_VEHICLE_ID_MODULE + ") " +
                "VALUES (?,?,?,?,?,?)";
        return executeQuery(query, modelVehicle.getModel(), modelVehicle.getLicencePlate(), modelVehicle.getAddress(), modelVehicle.getIdOwner(), modelVehicle.isAvailable(), modelVehicle.getIdModule());
    }

    /**
     * @param modelVehicle Vehicle to delete
     * @return the success
     */
    public boolean deleteVehicle(DBModelVehicle modelVehicle){
        String query = "DELETE FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID + " = ?";
        return executeQuery(query, modelVehicle.getId());
    }

    public boolean updateVehicle(DBModelVehicle vehicle){
        String query = "UPDATE " + TABLE_VEHICLE + " SET " +
                COLUMN_VEHICLE_MODEL + " = ?, " + COLUMN_VEHICLE_LICENCE_PLATE + " = ?, " + COLUMN_VEHICLE_ADDRESS + " = ?, " +
                COLUMN_VEHICLE_IS_AVAILABLE + " = ?, " + COLUMN_VEHICLE_ID_MODULE + " = ? " +
                "WHERE " + COLUMN_VEHICLE_ID + " = ?";
        return executeQuery(query, vehicle.getModel(), vehicle.getLicencePlate(), vehicle.getAddress(), vehicle.isAvailable(), vehicle.getIdModule(), vehicle.getId());
    }

    /**
     * set or reset a vehicle for booking
     * @param vehicleID the vehicle id
     * @param userID the user who book
     * @param isBooked if the user book or not
     * @return success
     */
    public boolean setBookedVehicle(int vehicleID, int userID, boolean isBooked){
        String query = "UPDATE " + TABLE_VEHICLE + " SET " +
                COLUMN_VEHICLE_ID_USER_BOOK + " = ?, " + COLUMN_VEHICLE_IS_BOOKED + " = ? " +
                "WHERE " + COLUMN_VEHICLE_ID + " = ?";
        return executeQuery(query,userID, isBooked, vehicleID);
    }

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
        //String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID_OWNER + " = " + IDUser;
        String query = "SELECT " + TABLE_VEHICLE + ".*, " + TABLE_MODULE + "." + COLUMN_MODULE_NAME + " FROM " + TABLE_VEHICLE +
                " JOIN " + TABLE_MODULE + " ON " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_MODULE + " = " + TABLE_MODULE + "." + COLUMN_MODULE_ID +
                " WHERE " + TABLE_VEHICLE + "." + COLUMN_VEHICLE_ID_OWNER + " = " + IDUser ;
        return getVehiclesJoin(query);
    }

    private List<DBModelVehicle> getVehiclesJoin(String query){
        List<DBModelVehicle> returnList = new ArrayList<>();

        // Get data from database
        try {
            if (connection == null) {
                Log.e(TAG_Database, "getVehicle: connect is null");
                return returnList;
            }

            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                String address = rs.getString(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);
                int idModule = rs.getInt(9);
                String codeModule = rs.getString(10);

                // Create object and add it to the list
                DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, address, idOwner, isAvailable, isBooked, idUser, idModule);
                vehicle.setCodeModule(codeModule);
                Log.d(TAG_Database, "getVehicles: " + vehicle);
                returnList.add(vehicle);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        }catch (Exception exception){
            Log.e(TAG_Database, "getVehicles: " , exception);
            exception.printStackTrace();
        }

        return returnList;
    }

    /**
     * @param ID id vehicle
     * @return return vehicle matching id
     */
    public DBModelVehicle getVehicleById(int ID){
        String query = "SELECT * FROM " + TABLE_VEHICLE + " WHERE " + COLUMN_VEHICLE_ID + " = " + ID;
        return getVehicles(query).get(0);
    }

    /**
     * @param query sql query
     * @return list of vehicles
     */
    private List<DBModelVehicle> getVehicles(String query){
        List<DBModelVehicle> returnList = new ArrayList<>();

        // Get data from database
        try {
            if (connection == null || connection.isClosed()) {
                Log.e(TAG_Database, "getVehicle: connect is null");
                return returnList;
            }

            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // For each row returned
            while (rs.next()) {
                // Get values
                int vehicle_id = rs.getInt(1);
                String model = rs.getString(2);
                String licencePlate = rs.getString(3);
                String address = rs.getString(4);
                int idOwner = rs.getInt(5);
                boolean isAvailable = rs.getBoolean(6);
                boolean isBooked = rs.getBoolean(7);
                int idUser = rs.getInt(8);
                int idModule = rs.getInt(9);

                // Create object and add it to the list
                DBModelVehicle vehicle = new DBModelVehicle(vehicle_id, model, licencePlate, address, idOwner, isAvailable, isBooked, idUser, idModule);
                Log.d(TAG_Database, "getVehicles: " + vehicle);
                returnList.add(vehicle);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        }catch (Exception exception){
            Log.e(TAG_Database, "getVehicles: " , exception);
            exception.printStackTrace();
        }

        return returnList;
    }

    /*  ---------------------------------- *
     *  --            MODULES           -- *
     *  ---------------------------------- */

    /**
     * @param ID module id
     * @return module
     */
    public DBModelModule getModuleById(int ID){
        String query = "SELECT * FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_ID + " = " + ID;
        //return getModules(query).get(0);
        List<DBModelModule> modules = getModules(query);
        if (modules.size() == 0)
            return new DBModelModule();
        else
            return getModules(query).get(0);
    }

    /**
     * @param name identification name
     * @return module
     */
    public DBModelModule getModuleByName(String name){
        String query = "SELECT * FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_NAME + " = '" + name + "';";
        List<DBModelModule> modules = getModules(query);
        if (modules.size() == 0)
            return new DBModelModule();
        else
            return getModules(query).get(0);
    }

    /**
     * @param query query
     * @return list of modules
     */
    private List<DBModelModule> getModules(String query){
        List<DBModelModule> returnList = new ArrayList<>();

        // Test connection
        if (connection == null) {
            Log.e(TAG_Database, "getModules: connect is null");
            return returnList;
        }

        try {
            // Execute query
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            // Get data
            while (rs.next()) {
                // Get values
                int module_id = rs.getInt(1);
                String name = rs.getString(2);
                String mac_address = rs.getString(3);

                // Create object and add it to the list
                DBModelModule module = new DBModelModule(module_id, name, mac_address);
                Log.d(TAG_Database, "getModules: " + module);
                returnList.add(module);
            }

            // Close both cursor and the database
            rs.close();
            st.close();
        } catch (Exception exception){
            Log.e(TAG_Database, "getModules: " , exception);
            exception.printStackTrace();
        }
        return returnList;
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
    private int idModule;
    private String codeModule;

    // Constructor
    public DBModelVehicle(int id, String model, String licencePlate, String address, int idOwner, boolean isAvailable, boolean isBooked, int idUser, int idModule) {
        this.id = id;
        this.model = model;
        this.licencePlate = licencePlate;
        this.address = address;
        this.idOwner = idOwner;
        this.isAvailable = isAvailable;
        this.isBooked = isBooked;
        this.idUser = idUser;
        this.idModule = idModule;
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
            map.put("idModule", idModule);
            map.put("codeModule", codeModule);
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

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

    public String getCodeModule() {
        return codeModule;
    }

    public void setCodeModule(String codeModule) {
        this.codeModule = codeModule;
    }
}

class DBModelModule {

    private int id;
    private String name;
    private String macAddress;

    public DBModelModule(int id, String name, String macAddress) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
    }

    public DBModelModule() {
    }

    @NonNull
    @Override
    public String toString() {
        return "DBModelModules{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}