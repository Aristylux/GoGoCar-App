package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_THREAD;

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
     * @param user user to add
     */
    public void addUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean success = databaseHelper.addUser(user);
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    /**
     * Delete a user
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param user user to delete
     */
    public void deleteUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean isDeleted = databaseHelper.deleteUser(user);
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
     */
    public void addVehicle(DBModelVehicle vehicle){
        thread = new Thread(() -> {
            boolean success = databaseHelper.addVehicle(vehicle);
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    /**
     * Delete a vehicle
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicleID vehicle id to delete
     */
    public void deleteVehicle(int vehicleID){
        thread = new Thread(() -> {
            DBModelVehicle vehicle = new DBModelVehicle();
            vehicle.setId(vehicleID);
            boolean isDeleted = databaseHelper.deleteVehicle(vehicle);
            callback.onResultTableUpdated(isDeleted);
        });
        thread.start();
    }

    /**
     * Update vehicle's information
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicle vehicle with new information
     */
    public void updateVehicle(DBModelVehicle vehicle){
        thread = new Thread(() -> {
            boolean isUpdated = databaseHelper.updateVehicle(vehicle);
            callback.onResultTableUpdated(isUpdated);
        });
        thread.start();
    }

    /**
     * Set a vehicle booked or not
     * <strong>CALLBACK: <i>onResultTableUpdated()</i></strong>
     * @param vehicleID vehicle id
     * @param userID    user id
     * @param isBooked  booked (true -> yes, false -> no)
     */
    public void setBookedVehicle(int vehicleID, int userID, boolean isBooked){
        thread = new Thread(() -> {
            boolean isUpdated = databaseHelper.setBookedVehicle(vehicleID, userID, isBooked);
            callback.onResultTableUpdated(isUpdated);
        });
        thread.start();
    }

    // Todo make it individual with multiple call of callback [onResultVehicle()]
    /**
     * Get vehicles available
     * <strong>CALLBACK: <i>onResultVehicles()</i></strong>
     * @param userID user id (to avoid duplicate)
     */
    public void getVehiclesAvailable(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesAvailable");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userID);
            callback.onResultVehicles(vehicles);
        });
        thread.start();
    }

    // Todo make it individual with multiple call of callback [onResultVehicle()]
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

    // Todo make it individual with multiple call of callback [onResultVehicle()]
    /**
     * Get user vehicles
     * <strong>CALLBACK: <i>onResultVehicles()</i></strong>
     * @param userID user id
     */
    public void getVehiclesByUser(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesByUser");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userID);
            callback.onResultVehicles(vehicles);
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
