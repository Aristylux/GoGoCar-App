package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.BLUETOOTH_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.FRAGMENT_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.GOTO_ADD_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_EDIT_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.NAVIGATION_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.HandlerCodes.SET_DRIVING;
import static com.aristy.gogocar.HandlerCodes.SET_MODAL;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE_FROM_HOME;
import static com.aristy.gogocar.PermissionHelper.checkPermission;
import static com.aristy.gogocar.PermissionHelper.isBluetoothEnabled;
import static com.aristy.gogocar.PermissionHelper.isLocationEnabled;
import static com.aristy.gogocar.WICommon.JSFunction.DRIVING_REQUEST;
import static com.aristy.gogocar.WICommon.Pages.pathPage;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.List;

/**
 * Web Interface for Screen (home, vehicles, drive, settings)
 */
public class WIMainScreen extends WICommon {

    Context context;
    Activity activity;
    UserPreferences userPreferences;
    Handler fragmentHandler;
    Handler bluetoothHandler;
    Handler navigationHandler;

    ThreadManager thread;

    public WIMainScreen(WebView webView, Context context, Activity activity, UserPreferences userPreferences, Handler [] handlers) {
        super(webView);

        this.context = context;
        this.activity = activity;

        this.userPreferences = userPreferences;
        this.userPreferences.setContext(context);

        this.fragmentHandler = handlers[FRAGMENT_HANDLER_POS];
        this.bluetoothHandler = handlers[BLUETOOTH_HANDLER_POS];
        this.navigationHandler = handlers[NAVIGATION_HANDLER_POS];

        this.thread = ThreadManager.getInstance();
    }

    /**
     * <i>Call in</i>: <code>popup.js</code><br>
     * <i>Call in</i>: <code>popup_home_cancel.js</code><br>
     * @param isActive true or false
     */
    @JavascriptInterface
    public void setModal(boolean isActive){
        navigationHandler.obtainMessage(SET_MODAL, isActive).sendToTarget();
    }

    /**
     * The user has clicked inside Navigation fragment<br>
     * remove modal in th main screen fragment too.
     */
    public void removeModal(){
        androidToWeb("closePopup");
    }

    /**
     * Call when return from a panel, refresh the page (or data)<br>
     * <i>Call in</i>: <code>drive.html</code><br>
     * <i>Call in</i>: <code>vehicle.js</code><br>
     */
    public void refresh(){
        androidToWeb("resetDatabase");
    }

    /**
     * [MOVER METHOD]<br>
     * Request to open the panel<br>
     * <i>Call in</i>: <code>vehicle.js</code><br>
     * @param pageSource    source
     * @param panelName     specified panel name
     */
    @JavascriptInterface
    public void openSlider(String pageSource, String panelName) {
        // If the page source is 'settings', enable swipe
        boolean activeSwipe = !pageSource.equals("settings");
        Object[] param = {pathPage + pageSource + "_" + panelName + ".html", activeSwipe};
        Log.d(TAG_Web, "openSlider: " + param[0]);
        fragmentHandler.obtainMessage(OPEN_SLIDER, param).sendToTarget();
    }

    /**
     * [MOVER METHOD]<br>
     * Request to open the panel, transfer data.<br>
     * <i>Call in</i>: <code>vehicle.js</code><br>
     * <i>Call in</i>: <code>popup_drive.js</code><br>
     * @param pageSource    source
     * @param panelName     specified panel name
     * @param data          data in json
     */
    @JavascriptInterface
    public void openSlider(String pageSource, String panelName, String data) {
        // If the page source is 'settings', enable swipe
        boolean activeSwipe = !pageSource.equals("settings");
        Object[] param = {pathPage + pageSource + "_" + panelName + ".html", activeSwipe, data};
        fragmentHandler.obtainMessage(OPEN_SLIDER, param).sendToTarget();
    }

    /*  ---------------------------------- *
     *  --           home.html          -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request data:<br>
     * - Name of the actual user <br>
     * - Get vehicles booked by the user<br>
     * <i>Call in</i>: <code>home.js</code><br>
     */
    @JavascriptInterface
    public void requestData(){
        androidToWeb("setUserName", userPreferences.getUserName());

        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultVehicles(List<DBModelVehicle> vehicles) {
                androidToWeb("setVehicleBooked", vehicles.toString());
            }
        });
        thread.getVehiclesBooked(userPreferences.getUserID());
    }

    /**
     * [MOVER METHOD]<br>
     * Request to change the page from home.html<br>
     * <i>Call in</i>: <code>home.js</code><br>
     * @param page new page to load ('drive' or 'vehicle')
     */
    @JavascriptInterface
    public void requestChangePage(String page){
        navigationHandler.obtainMessage(SET_PAGE_FROM_HOME, page).sendToTarget();
    }

    /**
     * Ask to app do connect to the bluetooth
     * Verify connection
     * Check bluetooth enabled
     * Check location enabled<br>
     * <i>Call in</i>: <code>home.js</code><br>
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
        navigationHandler.obtainMessage(SET_DRIVING, true).sendToTarget();
        //Intent enableBtIntent
        bluetoothHandler.obtainMessage(BT_STATE_DISCOVERING).sendToTarget();
    }

    /**
     * When the user want to stop driving<br>
     * <i>Call in</i>: <code>home.js</code><br>
     */
    @JavascriptInterface
    public void requestStopDrive(){
        navigationHandler.obtainMessage(SET_DRIVING, false).sendToTarget();
        bluetoothHandler.obtainMessage(BT_STATE_DISCONNECTING).sendToTarget();
    }

    /**
     * When the user want to remove his trip<br>
     * <i>Call in</i>: <code>home_popup_cancel.js</code><br>
     * @param vehicleID vehicle id
     */
    @JavascriptInterface
    public void requestCancelJourney(int vehicleID){
        // Reset
        //boolean isUpdate = databaseHelper.setBookedVehicle(vehicleID, 0, false);

        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean isUpdate) {
                if(!isUpdate) Toast.makeText(context, "ERROR: Can't cancel.", Toast.LENGTH_SHORT).show();
                else androidToWeb("journeyDelete", "true");
            }
        });
        //Reset vehicle
        thread.setBookedVehicle(vehicleID, 0, false);
    }

    /*  ---------------------------------- *
     *  --          drive.html          -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request available vehicle
     * In: <code>drive.js</code><br>
     */
    @JavascriptInterface
    public void requestDatabase(){
        thread.setResultCallback(new ThreadResultCallback() {
            // Not called
            @Override
            public void onResultVehicles(List<DBModelVehicle> vehicles) {
                androidToWeb("setDatabase", vehicles.toString());
            }

            @Override
            public void onResultVehicle(DBModelVehicle vehicle) {
                androidToWeb("addVehicle", vehicle.toString());
            }
        });
        thread.getVehiclesAvailable(userPreferences.getUserID());
    }

    // [NEVER USED] -> openSlider()
    /**
     * [MOVER METHOD]<br>
     * Called when the user want to book a vehicle<br>
     * In: <code>popup.js</code><br>
     * @param vehicle the vehicle wanted parsed in json format
     */
    @JavascriptInterface
    public void requestOpenBook(String vehicle){
        Log.d(TAG_Web, "requestOpenBook: " + vehicle);
        fragmentHandler.obtainMessage(GOTO_BOOK_VEHICLE_FRAGMENT, vehicle).sendToTarget();
    }

    /*  ---------------------------------- *
     *  --        vehicles.html         -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Ask all vehicles owned by the current user
     */
    @JavascriptInterface
    public void requestUserVehicles(){
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultVehicles(List<DBModelVehicle> vehicles) {
                androidToWeb("setDatabase", vehicles.toString());
            }
        });
        thread.getVehiclesByUser(userPreferences.getUserID());
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
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean isDeleted) {
                if (!isDeleted) Toast.makeText(context, "ERROR: Can't delete.", Toast.LENGTH_SHORT).show();
                else androidToWeb("vehicleDelete", "true");
            }
        });
        thread.deleteVehicle(vehicleID);
    }

    // [NEVER USED] -> openSlider()
    /**
     * [MOVER METHOD]<br>
     * Open Edit fragment & save vehicle for
     * @param vehicle String JSON vehicle
     */
    @JavascriptInterface
    public void requestOpenEditVehicle(String vehicle){
        fragmentHandler.obtainMessage(GOTO_EDIT_VEHICLE_FRAGMENT, vehicle).sendToTarget();
    }

    // [NEVER USED] -> openSlider()
    /**
     * [MOVER METHOD]<br>
     * Open Add a vehicle fragment
     */
    @JavascriptInterface
    public void requestOpenAddVehicle(){
        fragmentHandler.obtainMessage(GOTO_ADD_VEHICLE_FRAGMENT).sendToTarget();
    }

    /*  ---------------------------------- *
     *  --        settings.html         -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request the user name of the current user
     */
    @JavascriptInterface
    public void requestUserName(){
        androidToWeb("setUserName", userPreferences.getUserName());
    }

    /**
     * Remove data from database<br>
     * Check that the user is deleted<br>
     * Log out
     */
    @JavascriptInterface
    public void deleteUserAccount() {
        // Get user
        DBModelUser user = userPreferences.getUser();
        Log.d(TAG_Web, "deleteUserAccount: user=" + user);

        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean isDeleted) {
                // Logout
                if (isDeleted) logout();
                else Log.d(TAG_Web, "deleteUserAccount: error");
            }
        });
        // Remove user from database
        thread.deleteUser(user);
    }

    /**
     * Remove user from data application<br>
     * And move to login screen
     */
    @JavascriptInterface
    public void logout(){
        // Reset user to default
        userPreferences.resetUser();

        // Load page of login
        fragmentHandler.obtainMessage(GOTO_LOGIN_FRAGMENT).sendToTarget();
    }

}
