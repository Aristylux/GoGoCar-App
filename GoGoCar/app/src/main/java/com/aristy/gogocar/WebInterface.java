package com.aristy.gogocar;

import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_DOWN;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_LEFT;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_RIGHT;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.BLUETOOTH_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_STATE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.DATA_SET_VEHICLE;
import static com.aristy.gogocar.HandlerCodes.FRAGMENT_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.GOTO_ADD_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_DRIVE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_EDIT_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.PermissionHelper.checkPermission;
import static com.aristy.gogocar.PermissionHelper.isBluetoothEnabled;
import static com.aristy.gogocar.PermissionHelper.isLocationEnabled;
import static com.aristy.gogocar.WebInterface.FunctionNames.DRIVING_REQUEST;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.sql.Connection;
import java.util.List;

public class WebInterface {

    private static final String path = "file:///android_asset/pages/";
    public static final String HOME = path + "home.html";
    public static final String DRIVE = path + "drive.html";
    public static final String BOOK_VEHICLE = path + "drive_book.html";
    public static final String VEHICLE = path + "vehicles.html";
    public static final String ADD_VEHICLE = path + "vehicles_add.html";
    public static final String EDIT_VEHICLE = path + "vehicles_edit.html";

    Activity activity;
    Context context;
    WebView webView;

    UserPreferences userPreferences;

    Handler fragmentHandler;
    Handler bluetoothHandler;

    DatabaseHelper databaseHelper;

    // Constructor
    WebInterface(Activity activity, Context context, WebView webView, Connection connection, UserPreferences userPreferences, Handler [] handlers){
        this.activity = activity;
        this.context = context;
        this.webView = webView;

        this.databaseHelper = new DatabaseHelper(connection);
        this.userPreferences = userPreferences;
        this.userPreferences.setContext(context);   // Update context

        this.fragmentHandler = handlers[FRAGMENT_HANDLER_POS];
        this.bluetoothHandler = handlers[BLUETOOTH_HANDLER_POS];
    }

    /*  ---------------------------------- *
     *  --           home.html          -- *
     *  ---------------------------------- */

    /**
     * Can block to home fragment during driving
     */
    boolean isDriving;

    /**
     * Request data:
     * Name of the actual user
     * Get vehicles booked by the user
     * Set switch activated or not from bluetooth
     */
    @JavascriptInterface
    public void requestData(){
        requestUserName();

        // List booked vehicle for this user
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesBooked(userPreferences.getUserID());
        androidToWeb("setVehicleBooked", vehicles.toString());

        // Set state of switch
        bluetoothHandler.obtainMessage(BT_REQUEST_STATE).sendToTarget();
    }

    /**
     * Ask to app do connect to the bluetooth
     * Verify connection
     * Check bluetooth enabled
     * Check location enabled
     * @param vehicleID id vehicle to drive
     */
    @JavascriptInterface
    public void requestDrive(int vehicleID){
        Log.d(TAG_Web, "requestDrive: ");
        // Check if coarse location must be asked
        if (!checkPermission(activity)){
            // re-init operation
            Toast.makeText(context, "ask.", Toast.LENGTH_SHORT).show();
            androidToWeb(DRIVING_REQUEST, ErrorCodes.DRIVING_REQUEST_PERMISSION_ERROR);
            return;
        }

        // Check if elements are activated
        if(!isBluetoothEnabled()){
            Toast.makeText(context, "Please enable bluetooth.", Toast.LENGTH_SHORT).show();
            bluetoothHandler.obtainMessage(BT_REQUEST_ENABLE).sendToTarget();
            androidToWeb(DRIVING_REQUEST, ErrorCodes.DRIVING_REQUEST_BLUETOOTH_DISABLED);
            return;
        }

        if(!isLocationEnabled(context)) {
            Toast.makeText(context, "Please enable location.", Toast.LENGTH_SHORT).show();
            androidToWeb(DRIVING_REQUEST, ErrorCodes.DRIVING_REQUEST_LOCALISATION_DISABLE);
            return;
        }

        // Block user to home fragment during the journey (yes)
        isDriving = true;

        //Intent enableBtIntent
        bluetoothHandler.obtainMessage(BT_STATE_DISCOVERING).sendToTarget();
    }

    @JavascriptInterface
    public void requestStopDrive(){
        isDriving = false;
        bluetoothHandler.obtainMessage(BT_STATE_DISCONNECTING).sendToTarget();
    }

    @JavascriptInterface
    public void requestCancelJourney(int vehicleID){
        boolean isUpdate = databaseHelper.setBookedVehicle(vehicleID, 0, false);

        if(!isUpdate) Toast.makeText(context, "ERROR: Can't cancel.", Toast.LENGTH_SHORT).show();
        else androidToWeb("journeyDelete", "true");
    }

    /*  ---------------------------------- *
     *  --          drive.html          -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void requestDatabase(){
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userPreferences.getUserID());
        Log.d(TAG_Web, "requestDatabase: " + vehicles.toString());
        androidToWeb("setDatabase", vehicles.toString());
    }

    @JavascriptInterface
    public void requestOpenBook(String vehicle){
        Log.d(TAG_Web, "requestOpenBook: " + vehicle);
        fragmentHandler.obtainMessage(GOTO_BOOK_VEHICLE_FRAGMENT, vehicle).sendToTarget();
    }

    /* -- drive Book -- */

    @JavascriptInterface
    public void requestReturnToDrive(){
        fragmentHandler.obtainMessage(GOTO_DRIVE_FRAGMENT).sendToTarget();
    }

    @JavascriptInterface
    public void requestBookVehicle(int vehicleID, String pickupDate, String dropDate, int capacity){
        Log.d(TAG_Web, "requestBookVehicle: " + vehicleID + ", " + pickupDate + ", " + dropDate + ", " + capacity);

        // Check if the vehicle is available for these dates

        // If everything is ok, update database
        boolean isUpdate = databaseHelper.setBookedVehicle(vehicleID, userPreferences.getUserID(), true);

        if(!isUpdate) Toast.makeText(context, "ERROR: Can't update.", Toast.LENGTH_SHORT).show();
        else fragmentHandler.obtainMessage(GOTO_DRIVE_FRAGMENT).sendToTarget();
    }

    /*  ---------------------------------- *
     *  --        vehicles.html         -- *
     *  ---------------------------------- */

    /**
     * Ask all vehicles owned by the current user
     */
    @JavascriptInterface
    public void requestUserVehicles(){
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userPreferences.getUserID());
        androidToWeb("setDatabase", vehicles.toString());
    }

    /**
     * Remove a vehicle
     * @param vehicleID id vehicle for identification<br>
     * <br>
     * return: true to webView if success<br>
     *         else, show error
     */
    @JavascriptInterface
    public void requestRemoveVehicle(int vehicleID){
        DBModelVehicle vehicle = new DBModelVehicle();
        vehicle.setId(vehicleID);
        boolean isDeleted = databaseHelper.deleteVehicle(vehicle);

        if (!isDeleted) Toast.makeText(context, "ERROR: Can't delete.", Toast.LENGTH_SHORT).show();
        else androidToWeb("vehicleDelete", "true");
    }

    /**
     * Return to the main fragment
     * @param from fragment origin: <strong>add</strong> or <strong>edit</strong>
     */
    @JavascriptInterface
    public void requestReturnToHome(String from){
        int animation = ANIMATE_SLIDE_LEFT;
        if (from.equals("ADD_VEHICLE"))
            animation = ANIMATE_SLIDE_DOWN;
        if (from.equals("EDIT_VEHICLE"))
            animation = ANIMATE_SLIDE_RIGHT;
        fragmentHandler.obtainMessage(GOTO_VEHICLE_FRAGMENT, animation).sendToTarget();
    }

    /* -- vehicle edit -- */

    /**
     * Open Edit fragment
     * @param vehicle String JSON vehicle
     */
    @JavascriptInterface
    public void requestOpenEditVehicle(String vehicle){
        fragmentHandler.obtainMessage(GOTO_EDIT_VEHICLE_FRAGMENT, vehicle).sendToTarget();
    }

    /**
     * Send vehicle (in main) to the new fragment
     */
    @JavascriptInterface
    public void requestGetVehicle(){
        fragmentHandler.obtainMessage(DATA_SET_VEHICLE).sendToTarget();
    }

    /**
     * request an update to the database
     * @param id vehicle id
     * @param model vehicle model
     * @param licencePlate vehicle licence plate
     * @param address main address
     * @param moduleCode code mi carro es tu carro module
     * @param isAvailable if vehicle is available for booking
     */
    @JavascriptInterface
    public void requestUpdateVehicle(int id, String model, String licencePlate, String address, String moduleCode, boolean isAvailable){

        // Check if the module code is correct
        DBModelModule module = databaseHelper.getModuleByName(moduleCode);
        if(module.getId() == 0){
            Toast.makeText(context, "module code incorrect", Toast.LENGTH_SHORT).show();
            androidToWeb("updateVehicleResult", "2");  // Error code 2
            return;
        }

        DBModelVehicle vehicle = new DBModelVehicle();
        vehicle.setId(id);
        vehicle.setModel(model);
        vehicle.setLicencePlate(licencePlate);
        vehicle.setAddress(address);
        vehicle.setIdModule(module.getId());
        vehicle.setAvailable(isAvailable);

        boolean success = databaseHelper.updateVehicle(vehicle);
        if (!success) {
            Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
            androidToWeb("updateVehicleResult", "3");  // Error code 3
            return;
        }

        // Return top vehicle fragment
        fragmentHandler.obtainMessage(GOTO_VEHICLE_FRAGMENT, ANIMATE_SLIDE_RIGHT).sendToTarget();
    }

    /* -- vehicle add -- */

    /**
     * Open Add a vehicle fragment
     */
    @JavascriptInterface
    public void requestOpenAddVehicle(){
        fragmentHandler.obtainMessage(GOTO_ADD_VEHICLE_FRAGMENT).sendToTarget();
    }

    /**
     * Request to the database, add a new vehicle
     * @param model vehicle name
     * @param licencePlate vehicle licence plate
     * @param address main address
     * @param moduleCode mi carro es tu carro module code
     * @param isAvailable if the vehicle is available for booking
     */
    @JavascriptInterface
    public void requestAddVehicle(String model, String licencePlate, String address, String moduleCode, boolean isAvailable){
        // Check address
        if (address.isEmpty()){
            androidToWeb("addVehicleResult", "4");  // Error code 4
            return;
        }

        // Check if the model exist
        //Toast.makeText(context, "error model doesn't exist", Toast.LENGTH_SHORT).show();
        //androidToWeb("addVehicleResult", "1");  // Error code 1

        // Check if the module code is correct
        DBModelModule module = databaseHelper.getModuleByName(moduleCode);
        if(module.getId() == 0){
            Toast.makeText(context, "module code incorrect", Toast.LENGTH_SHORT).show();
            androidToWeb("addVehicleResult", "2");  // Error code 2
            return;
        }

        // Success: add vehicle & quit page
        // Create vehicle
        DBModelVehicle vehicle = new DBModelVehicle();
        vehicle.setModel(model);
        vehicle.setLicencePlate(licencePlate);
        vehicle.setAddress(address);
        vehicle.setIdOwner(userPreferences.getUserID());
        vehicle.setAvailable(isAvailable);
        vehicle.setBooked(false);
        vehicle.setIdModule(module.getId());

        // Add vehicle into vehicle table
        boolean success = databaseHelper.addVehicle(vehicle);
        Log.d(TAG_Database, "success=" + success);
        if (!success) {
            Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
            androidToWeb("addVehicleResult", "3");  // Error code 3
            return;
        }

        // Return top vehicle fragment
        fragmentHandler.obtainMessage(GOTO_VEHICLE_FRAGMENT, ANIMATE_SLIDE_DOWN).sendToTarget();
    }


    /*  ---------------------------------- *
     *  --        settings.html         -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void requestUserName(){
        androidToWeb("setUserName", userPreferences.getUserName());
    }

    // ---- Personal information container ----

    @JavascriptInterface
    public void requestPersonalInformation() {
        androidToWeb("setUserInformation", userPreferences.toString());
    }

    @JavascriptInterface
    public void openSlider(String panelName) {
        //Log.d(TAG_Web, "openSlider: " + path + containerName + ".html");
        fragmentHandler.obtainMessage(OPEN_SLIDER, path + "settings_" + panelName + ".html").sendToTarget();
    }

    // ----

    @JavascriptInterface
    public void deleteUserAccount() {
        // Get user
        DBModelUser user = userPreferences.getUser();
        Log.d(TAG_Web, "deleteUserAccount: user=" + user);

        // Remove user from database
        boolean isDeleted = databaseHelper.deleteUser(user);

        // Logout
        if (isDeleted) logout();
        else Log.d(TAG_Web, "deleteUserAccount: error");
    }

    @JavascriptInterface
    public void logout(){
        // Reset user to default

        userPreferences.resetUser();

        // Load page of login
        fragmentHandler.obtainMessage(GOTO_LOGIN_FRAGMENT).sendToTarget();
    }

    /*  ---------------------------------- *
     *  --          Navigation          -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void changePage(String page){
        if (!isDriving)
            loadNewPage(page);
    }

    /*  ---------------------------------- *
     *  -- Methods send data to webPage -- *
     *  ---------------------------------- */

    public void dataReceived(){
        androidToWeb("dataReceived", "red");
    }

    /**
     *  Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void sendData(String action){
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();

        dataReceived();
    }

    /*  ---------------- *
     *  -- Interfaces -- *
     *  ---------------- */


    private void loadNewPage(String page){
        webView.post(() -> webView.loadUrl("file:///android_asset/pages/" + page + ".html"));
    }

    private void androidToWeb(String functionName, String... data){
        StringBuilder builder = new StringBuilder();
        if(data.length != 0) {
            builder.append(data[0]);
            for (int i = 1; i < data.length ; i++){
                builder.append("','");
                builder.append(data[i]);
            }
        }

        webView.post(() -> webView.loadUrl("javascript:" + functionName + "('" + builder + "')"));
    }

    /**
     * List all function available to call in web.
     */
    static class FunctionNames {
        public static final String DRIVING_REQUEST = "requestDriveCallback";

        public static final String SET_VEHICLE_EDIT = "setVehicle";
    }

    static class ErrorCodes {
        public static final String DRIVING_REQUEST_PERMISSION_ERROR = "1";
        public static final String DRIVING_REQUEST_BLUETOOTH_DISABLED = "2";
        public static final String DRIVING_REQUEST_LOCALISATION_DISABLE = "3";
        public static final String DRIVING_REQUEST_CAR_NOT_FOUND = "4";
        public static final String DRIVING_CONNECTION_FAILED = "5";
        public static final String DRIVING_CONNECTION_DISCONNECTED = "6";
    }

    static class Boolean {
        public static final String TRUE = "true";
        //public static final String FALSE = "false";
    }

}
