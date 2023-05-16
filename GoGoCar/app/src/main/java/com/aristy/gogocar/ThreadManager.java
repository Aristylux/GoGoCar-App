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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ThreadManager {

    // Thread objects
    private static ThreadManager instance;

    private Thread thread;
    private ThreadResultCallback callback;
    private long threadID;
    private boolean isExpired;

    // Database
    private DatabaseHelper databaseHelper;
    private boolean connected;
    private Method method;
    private Object[] params;

    // Constructor
    private ThreadManager() {
        databaseHelper = null;
        connected = false;
        method = null;
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
        thread = new Thread(() -> {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connectionHelper.openConnection();
            databaseHelper = new DatabaseHelper(connectionHelper.getConnection());
            Log.d(TAG_THREAD, "setConnection: " + connectionHelper.getConnection());

            if (connectionHelper.getConnection() != null) connected = true;
            getQueueLastMethod();
        });
        thread.start();
    }

    public void setResultCallback(ThreadResultCallback callback) {
        this.callback = callback;
    }

    /**
     * Verify if the current thread is running<br>
     * If yes, set it expired.
     */
    public void verifyThread(){
        Log.d(TAG_THREAD, "printThread: " + threadID + " " + thread.getId() + " | " + (threadID == thread.getId()) + " " + thread.getState());
        if (thread.getState().toString().equals("RUNNABLE")){
            isExpired = true;
        }
    }

    /**
     * Verify if the current thread is expired
     * @return isExpired
     */
    private boolean isExpired(){
        if (isExpired){
            isExpired = false;
            return true;
        }
        return false;
    }

    /**
     * Verify all the elements to avoid unexpected error
     * @param methodName    caller method which call this method
     * @param params        arguments of the caller method
     * @return true if error
     */
    private boolean checkStateError(String methodName, Object... params) {
        if (!connected){
            // Set last method called
            setQueueLastMethod(methodName, params);
            return true;
        }
        if (callback == null) {
            Log.e(TAG_THREAD, "[ERROR THREAD](" + methodName + "): no callback");
            return true;
        }
        if (databaseHelper == null){
            Log.e(TAG_THREAD, "[ERROR THREAD](" + methodName + "): no database helper");
            return true;
        }
        return false;
    }

    /**
     * Execute last method called
     */
    private void getQueueLastMethod(){
        try {
            if (method == null) return;
            Log.d(TAG_THREAD, "getQueueLastMethod: invoke method: " + method.getName() );
            method.invoke(this, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setQueueLastMethod(String methodName, Object... params){
        try {
            // Get the list of declared methods in the class
            Method[] methods = getClass().getDeclaredMethods();

            // Loop through the methods and find the one with the correct name and parameter types
            method = null;
            for (Method method : methods) {
                if (method.getName().equals(methodName) && parametersMatch(method.getParameterTypes(), params)) {
                    this.method = method;
                    break;
                }
            }

            if (method == null) {
                throw new NoSuchMethodException("Method " + methodName + " not found with parameter types " + Arrays.toString(params));
            }

            // Get the method object using the method name
            this.params = params;

            // Make the method accessible
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean parametersMatch(Class<?>[] parameterTypes, Object[] arguments) {
        if (parameterTypes.length != arguments.length) return false;

        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isPrimitive()) {
                if (!primitiveTypesMatch(parameterTypes[i], arguments[i])) return false;
            } else if (!parameterTypes[i].isAssignableFrom(arguments[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    private boolean primitiveTypesMatch(Class<?> parameterType, Object argument) {
        if (parameterType == int.class && argument instanceof Integer) {
            return true;
        } else if (parameterType == long.class && argument instanceof Long) {
            return true;
        } else if (parameterType == float.class && argument instanceof Float) {
            return true;
        } else if (parameterType == double.class && argument instanceof Double) {
            return true;
        } else if (parameterType == byte.class && argument instanceof Byte) {
            return true;
        } else if (parameterType == short.class && argument instanceof Short) {
            return true;
        } else if (parameterType == char.class && argument instanceof Character) {
            return true;
        } else return parameterType == boolean.class && argument instanceof Boolean;
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
        if (checkStateError("addUser")) return;
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
        if (checkStateError("deleteUser")) return;
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
        if (checkStateError("getUserByEmail")) return;
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
        if (checkStateError("getUserByPhone")) return;
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
        if (checkStateError("addVehicle")) return;
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
        if (checkStateError("deleteVehicle")) return;
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
        if (checkStateError("updateVehicle")) return;
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
        if (checkStateError("setBookedVehicle")) return;
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
        if (checkStateError("getVehiclesAvailable")) return;
        thread = new Thread(() -> {
            threadID = thread.getId();
            Log.d(TAG_THREAD, "run: getVehiclesAvailable: " + thread.getName() + " " + thread.getId() + " " + thread.getState());

            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userID);

            for (DBModelVehicle vehicle : vehicles){
                if (isExpired()) return;
                callback.onResultVehicle(vehicle);
            }
        });
        thread.start();
    }

    // TODO
    public void getVehiclesAvailable(int userID, String city, int distance){
        if (checkStateError("getVehiclesAvailable", userID)) return;
        thread = new Thread(() -> {

            DBModelVehicle vh = new DBModelVehicle();

            DBModelVehicle [] vehicles = {vh}; //= databaseHelper.getVehiclesAvailable(userID);

            for (DBModelVehicle vehicle : vehicles){
                if (isExpired()) return;
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
        if (checkStateError("getVehiclesBooked", userID)) return;
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
        if (checkStateError("getVehiclesByUser")) return;
        thread = new Thread(() -> {
            Log.d(TAG_THREAD, "run: getVehiclesByUser: " + thread.getName() + " " + thread.getId() + " " + thread.getState());
            List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userID);

            if (isExpired()) return;
            callback.onResultEmpty(vehicles.size() == 0);

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
        if (checkStateError("getVehicleByModule")) return;
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
        if (checkStateError("getModuleByName")) return;
        thread = new Thread(() -> {
            DBModelModule module = databaseHelper.getModuleByName(moduleCode);
            callback.onResultModule(module);
        });
        thread.start();
    }

    /*  ---------------------------------- *
     *  --             CITY             -- *
     *  ---------------------------------- */


    public void getMatchingCities(String firstChar){
        if (checkStateError("getMatchingCities")) return;
        thread = new Thread(() -> {
            String[] matchingCities = databaseHelper.getMatchingCities(firstChar);
            callback.onResultStringArray(matchingCities);
        });
        thread.start();

    }

}
