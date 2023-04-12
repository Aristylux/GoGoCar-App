package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_THREAD;
import static com.aristy.gogocar.DatabaseHelper.ADD_USER_QUERY;
import static com.aristy.gogocar.DatabaseHelper.ADD_VEHICLE_QUERY;
import static com.aristy.gogocar.DatabaseHelper.DELETE_USER_QUERY;
import static com.aristy.gogocar.DatabaseHelper.DELETE_VEHICLE_QUERY;
import static com.aristy.gogocar.DatabaseHelper.SET_VEHICLE_BOOKED_QUERY;
import static com.aristy.gogocar.DatabaseHelper.UPDATE_VEHICLE_QUERY;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class ThreadManager {

    // Thread objects
    private static ThreadManager instance;

    private Thread thread;
    private ThreadResultCallback callback;

    // Database
    private DatabaseHelper databaseHelper;

    // Constructor
    private ThreadManager() {
    }

    /**
     * Get thread from all activities
     * @return Thread instance
     */
    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            Log.d(TAG_THREAD, "getInstance: null");
            instance = new ThreadManager();
        }
        return instance;
    }

    /**
     * Set and create new connection to the database
     */
    public void setConnection() {
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connectionHelper.openConnection();
        databaseHelper = new DatabaseHelper(connectionHelper.getConnection());
        Log.d(TAG_THREAD, "setConnection: " + connectionHelper.getConnection());
    }

    public Thread getThread() {
        return thread;
    }

    public void setResultCallback(ThreadResultCallback callback) {
        this.callback = callback;
    }

    private boolean checkArguments() {
        if (callback == null) {
            Log.e(TAG_THREAD, "run: [ERROR]: no callback");
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "ThreadManager {" +
                "callback=" + callback.toString() +
                '}';
    }

    /*  ---------------------------------- *
     *  --             USER             -- *
     *  ---------------------------------- */

    /**
     * Add user to the database<br>
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param user user to add (user with name, phone, ...)
     * @Callback-Return: The success:<br>
     *      - true  - if success<br>
     *      - false - if not connection or exception
     */
    public void addUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean success = databaseHelper.executeQuery(ADD_USER_QUERY, user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.getSalt());
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    /**
     * Delete a user
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param user user to delete
     * @Callback-Return: The success
     */
    public void deleteUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean isDeleted = databaseHelper.executeQuery(DELETE_USER_QUERY, user.getId());
            callback.onResultTableUpdated(isDeleted);
        });
        thread.start();
    }

    /**
     * Get user by email
     * <strong>CALLBACK: <i>onResultUser()</i></strong>
     * @param email user email
     */
    public void getUserByEmail(String email){
        thread = new Thread(() -> {
            DBModelUser user = databaseHelper.getUserByEmail(email);
            callback.onResultUser(user);
        });
        thread.start();
    }

    /**
     * Get user by phone number
     * <strong>CALLBACK: <i>onResultUser()</i></strong>
     * @param phone user phone number
     */
    public void getUserByPhone(String phone){
        thread = new Thread(() -> {
            DBModelUser user = databaseHelper.getUserByPhone(phone);
            callback.onResultUser(user);
        });
        thread.start();
    }

    /*  ---------------------------------- *
     *  --            VEHICLES          -- *
     *  ---------------------------------- */

    /**
     * Add a vehicle to the database
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicle vehicle to add
     * @Callback-Return: The success:<br>
     *         - true  - if success<br>
     *         - false - if not connection or exception
     */
    public void addVehicle(DBModelVehicle vehicle){
        thread = new Thread(() -> {
            boolean success = databaseHelper.executeQuery(ADD_VEHICLE_QUERY, vehicle.getModel(), vehicle.getLicencePlate(), vehicle.getAddress(), vehicle.getIdOwner(), vehicle.isAvailable(), vehicle.getIdModule());
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    /**
     * Delete a vehicle
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicleID vehicle id to delete
     * @Callback-Return: The success
     */
    public void deleteVehicle(int vehicleID){
        thread = new Thread(() -> {
            boolean isDeleted = databaseHelper.executeQuery(DELETE_VEHICLE_QUERY, vehicleID);
            callback.onResultTableUpdated(isDeleted);
        });
        thread.start();
    }

    /**
     * Update vehicle's information
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicle vehicle with new information
     * @Callback-Return: The success
     */
    public void updateVehicle(DBModelVehicle vehicle){
        thread = new Thread(() -> {
            boolean isUpdated = databaseHelper.executeQuery(UPDATE_VEHICLE_QUERY, vehicle.getModel(), vehicle.getLicencePlate(), vehicle.getAddress(), vehicle.isAvailable(), vehicle.getIdModule(), vehicle.getId());
            callback.onResultTableUpdated(isUpdated);
        });
        thread.start();
    }

    /**
     * Set a vehicle booked or not
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicleID vehicle id
     * @param userID    user id who book
     * @param isBooked  booked (true -> yes, false -> no)
     * @Callback-Return: The success
     */
    public void setBookedVehicle(int vehicleID, int userID, boolean isBooked){
        thread = new Thread(() -> {
            boolean isUpdated = databaseHelper.executeQuery(SET_VEHICLE_BOOKED_QUERY, userID, isBooked, vehicleID);
            callback.onResultTableUpdated(isUpdated);
        });
        thread.start();
    }

    /**
     * Get vehicles available
     * <strong>CALLBACK: <i>onResultVehicles()</i></strong>
     * @param userID user id (to avoid duplicate)
     */
    public void getVehiclesAvailable(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesAvailable");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userID);
            for (DBModelVehicle vehicle : vehicles){
                callback.onResultVehicle(vehicle);
            }
        });
        thread.start();
    }

    /**
     * Get only the vehicles booked by the user
     * <strong>CALLBACK: <i>onResultVehicles()</i></strong>
     * @param userID user id
     */
    public void getVehiclesBooked(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesBooked");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesBooked(userID);
            callback.onResultVehicles(vehicles);
        });
        thread.start();
    }

    /**
     * Get user vehicles
     * <strong>CALLBACK: <i>onResultVehicles()</i></strong>
     * @param userID user id
     */
    public void getVehiclesByUser(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesByUser");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userID);
            for (DBModelVehicle vehicle : vehicles){
                callback.onResultVehicle(vehicle);
            }
        });
        thread.start();
    }

    /**
     * Get a vehicle by a module
     * <strong>CALLBACK: <i>onResultVehicle()</i></strong>
     * @param moduleID module id
     */
    public void getVehicleByModule(int moduleID){
        thread = new Thread(() -> {
            DBModelVehicle vehicle = databaseHelper.getVehicleByModule(moduleID);
            callback.onResultVehicle(vehicle);
        });
        thread.start();
    }

    /*  ---------------------------------- *
     *  --            MODULES           -- *
     *  ---------------------------------- */

    /**
     * Get module by name (by code "#XX-XX-XXXX")
     * <strong>CALLBACK: <i>onResultModule()</i></strong>
     * @param moduleCode module code
     */
    public void getModuleByName(String moduleCode){
        thread = new Thread(() -> {
            DBModelModule module = databaseHelper.getModuleByName(moduleCode);
            callback.onResultModule(module);
        });
        thread.start();
    }
}
