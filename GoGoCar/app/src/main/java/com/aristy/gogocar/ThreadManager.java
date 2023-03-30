package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_THREAD;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class ThreadManager {

    public static final int GET_BOOKED_VEHICLES = 1;
    public static final int GET_MODULE_BY_NAME = 2;


    private static ThreadManager instance;
    private Thread thread;

    private ThreadResultCallback callback;
    private int type;
    private Object param;

    private DatabaseHelper databaseHelper;

    // Constructor
    private ThreadManager() {
    }

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            Log.d(TAG_THREAD, "getInstance: null");
            instance = new ThreadManager();
        }
        return instance;
    }

    public void setConnection() {
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connectionHelper.openConnection();
        databaseHelper = new DatabaseHelper(connectionHelper.getConnection());
        Log.d(TAG_THREAD, "setConnection: " + connectionHelper.getConnection());
    }

    public void startThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkArguments()) return;
                Log.d(TAG_THREAD, "run: " + ThreadManager.this);

                switch (type) {
                    case GET_BOOKED_VEHICLES:
                        Log.d(TAG_THREAD, "run: GET_BOOKED_VEHICLES");
                        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesBooked((int) param);
                        callback.onResultVehicles(vehicles);
                        break;
                    case GET_MODULE_BY_NAME:
                        Log.d(TAG_THREAD, "run: GET_MODULE_BY_NAME");
                        DBModelModule module = databaseHelper.getModuleByName("#01-01-0001");
                        callback.onResultModule(module);
                        break;
                }
            }
        });
        thread.start();
    }

    public void getVehiclesBooked(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesBooked");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesBooked(userID);
            callback.onResultVehicles(vehicles);
        });
        thread.start();
    }

    public void getVehiclesAvailable(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesAvailable");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userID);
            callback.onResultVehicles(vehicles);
        });
        thread.start();
    }

    public void getVehiclesByUser(int userID){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesByUser");
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userID);
            callback.onResultVehicles(vehicles);
        });
        thread.start();
    }

    public void setBookedVehicle(int vehicleID, int userID, boolean isBooked){
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: setBookedVehicle");
            boolean isUpdated = databaseHelper.setBookedVehicle(vehicleID, userID, isBooked);
            callback.onResultTableUpdated(isUpdated);
        });
        thread.start();
    }

    public void addVehicle(DBModelVehicle vehicle){
        thread = new Thread(() -> {
            boolean success = databaseHelper.addVehicle(vehicle);
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    public void deleteVehicle(int vehicleID){
        thread = new Thread(() -> {
            DBModelVehicle vehicle = new DBModelVehicle();
            vehicle.setId(vehicleID);
            boolean isDeleted = databaseHelper.deleteVehicle(vehicle);
            callback.onResultTableUpdated(isDeleted);
        });
        thread.start();
    }

    // ---- User ----

    public void addUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean success = databaseHelper.addUser(user);
            callback.onResultTableUpdated(success);
        });
        thread.start();
    }

    public void deleteUser(DBModelUser user){
        thread = new Thread(() -> {
            boolean isDeleted = databaseHelper.deleteUser(user);
            callback.onResultTableUpdated(isDeleted);
        });
        thread.start();
    }

    public void getUserByEmail(String email){
        Log.d(TAG_THREAD, "getUserByEmail: get");
        thread = new Thread(() -> {
            DBModelUser user = databaseHelper.getUserByEmail(email);
            Log.d(TAG_THREAD, "getUserByEmail: " + user);
            callback.onResultUser(user);
        });
        thread.start();
    }

    public void getUserByPhone(String phone){
        thread = new Thread(() -> {
            DBModelUser user = databaseHelper.getUserByPhone(phone);
            callback.onResultUser(user);
        });
        thread.start();
    }

    // ---- Modules ----

    public void getModuleByName(String moduleCode){
        thread = new Thread(() -> {
            DBModelModule module = databaseHelper.getModuleByName(moduleCode);
            callback.onResultModule(module);
        });
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public void setQueryType(int type) {
        this.type = type;
    }

    public void setQueryParameters(Object param) {
        this.param = param;
    }

    public void setResultCallback(ThreadResultCallback callback) {
        this.callback = callback;
    }

    private boolean checkArguments() {
        if (callback == null) {
            Log.e(TAG_THREAD, "run: [ERROR]: no callback");
            return true;
        }

        if (type == 0) {
            Log.e(TAG_THREAD, "run: [ERROR]: no action selected");
            return true;
        }

        if (param == null) {
            Log.e(TAG_THREAD, "run: [ERROR]: no action parameters");
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "ThreadManager {" +
                "callback=" + callback.toString() +
                ", type='" + type + '\'' +
                ", param='" + param + '\'' +
                '}';
    }
}
