package com.aristy.gogocar;

import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_DOWN;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_LEFT;
import static com.aristy.gogocar.Animation.ANIMATE_SLIDE_RIGHT;
import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.BLUETOOTH_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.DATA_SET_VEHICLE;
import static com.aristy.gogocar.HandlerCodes.FRAGMENT_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.GOTO_ADD_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_EDIT_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;
import static com.aristy.gogocar.PermissionHelper.checkPermission;
import static com.aristy.gogocar.PermissionHelper.isBluetoothEnabled;
import static com.aristy.gogocar.PermissionHelper.isLocationEnabled;
import static com.aristy.gogocar.SHAHash.hashPassword;
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

    public static final String HOME = "file:///android_asset/pages/home.html";
    public static final String VEHICLE = "file:///android_asset/pages/vehicles.html";
    public static final String ADD_VEHICLE = "file:///android_asset/pages/vehicles_add.html";
    public static final String EDIT_VEHICLE = "file:///android_asset/pages/vehicles_edit.html";

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
     *  --          login.html          -- *
     *  ---------------------------------- */

    /**
     * When Click on login button
     * @param email     input user email
     * @param password  input user password
     */
    @JavascriptInterface
    public void AuthenticationLogin(String email, String password){
        // Hash password
        String hash = hashPassword(password, SHAHash.DOMAIN);

        // Verify user exist for this email and password
        DBModelUser user = verify(email, hash);

        // If user doesn't exist
        if (user == null) {
            Log.e(TAG_Web, "AuthenticationLogin: no this user in our database");
            // Send error to the page
            androidToWeb("errorAuthenticationLogin");
        } else {
            // Set user in app & Save user for the application (user id)
            userPreferences.setUser(user);

            // Go to home
            fragmentHandler.obtainMessage(GOTO_HOME_FRAGMENT).sendToTarget();
            fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) HexColor.TRANSPARENT).sendToTarget();
        }
    }

    /**
     * Verify if user exist
     * @param email     user email enter in login
     * @param hash      user password hash
     * @return user if success,<br>
     *         null if not.
     */
    private DBModelUser verify(String email, String hash){
        DBModelUser user = databaseHelper.getUserByEmail(email);
        // if user exist
        if(user.getPassword() != null){
            // Compare passwords, if hash_password == hash
            if(hash.equals(user.getPassword())) return user;    // Ok
        }
        // If password and email are different: Not ok
        return null;
    }

    /**
     * When Click on register button
     * @param fullName      input name
     * @param email         input email
     * @param phoneNumber   input phone number
     * @param password      input password
     */
    @JavascriptInterface
    public void AuthenticationRegister(String fullName, String email, String phoneNumber, String password){
        // Hash password
        String hash = hashPassword(password, SHAHash.DOMAIN);
        Log.d(TAG_Web, "pw= \"" + password + "\", hash= \"" + hash + "\"");

        // Create user
        DBModelUser user = new DBModelUser(-1, fullName, email, phoneNumber, hash);

        // Add user into user table
        boolean success = databaseHelper.addUser(user);
        Log.d(TAG_Database, "success=" + success);
        if (!success) {
            Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve user id
        DBModelUser user_refresh = databaseHelper.getUserByEmail(email);

        // Set user in app & Save user for the application (user id)
        userPreferences.setUser(user_refresh);

        // Load home page
        fragmentHandler.obtainMessage(GOTO_HOME_FRAGMENT).sendToTarget();
        fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) HexColor.TRANSPARENT).sendToTarget();
    }

    @JavascriptInterface
    public void verifyEmail(String email, int successCode, int errorCode){
        // Check in database if email exist
        DBModelUser user = databaseHelper.getUserByEmail(email);
        Log.d(TAG_Auth, "verifyEmail: " + user.toString());

        // If exist -> error
        if (user.getEmail() == null) // not exist
            androidToWeb("success", String.valueOf(successCode));
        else
            androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
    }

    @JavascriptInterface
    public void verifyPhone(String phone, int successCode, int errorCode){
        DBModelUser user = databaseHelper.getUserByPhone(phone);
        Log.d(TAG_Auth, "verifyPhone: " + user.toString());

        // If exist -> error
        if (user.getPhoneNumber() == null) // not exist
            androidToWeb("success", String.valueOf(successCode));
        else
            androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
    }

    /*  ---------------------------------- *
     *  --           home.html          -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void requestDrive(){
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

        //Intent enableBtIntent
        bluetoothHandler.obtainMessage(BT_STATE_DISCOVERING).sendToTarget();
    }


    /*  ---------------------------------- *
     *  --        vehicles.html         -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void requestUserVehicles(){
        //DatabaseHelper databaseHelper = new DatabaseHelper(connection);
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesByUser(userPreferences.getUserID());
        androidToWeb("setDatabase", vehicles.toString());
    }

    @JavascriptInterface
    public void requestRemoveVehicle(int vehicleID){
        DBModelVehicle vehicle = new DBModelVehicle();
        vehicle.setId(vehicleID);
        boolean isDeleted = databaseHelper.deleteVehicle(vehicle);

        if (!isDeleted) Toast.makeText(context, "ERROR: Can't delete.", Toast.LENGTH_SHORT).show();
        else androidToWeb("vehicleDelete", "true");
    }

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

    @JavascriptInterface
    public void requestOpenAddVehicle(){
        fragmentHandler.obtainMessage(GOTO_ADD_VEHICLE_FRAGMENT).sendToTarget();
    }

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

    // -----

    @JavascriptInterface
    public void changeBackground(String webColor){
        Log.d(TAG_Web, "changeBackground : " + webColor);

        // Convert Color
        HexColor hexColor = new HexColor(webColor);
        hexColor.convertToAndroidColor();
        long colorSigned = hexColor.getDecSigned();

        // Request to change the color
        fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) colorSigned).sendToTarget();
    }

    @JavascriptInterface
    public void openPopupBook(int id_vehicle){
        Log.d(TAG_Web, "id vehicle: " + id_vehicle);

        DBModelVehicle vehicle = databaseHelper.getVehicleById(id_vehicle);

        androidToWeb("openPopupBook", vehicle.getModel(), vehicle.getAddress());
    }


    @JavascriptInterface
    public void requestDatabase(){
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesAvailable(userPreferences.getUserID());
        Log.d(TAG_Web, "requestDatabase: " + vehicles.toString());
        androidToWeb("setDatabase", vehicles.toString());
    }

    /*  ---------------------------------- *
     *  --          Navigation          -- *
     *  ---------------------------------- */

    @JavascriptInterface
    public void changePage(String page){
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
