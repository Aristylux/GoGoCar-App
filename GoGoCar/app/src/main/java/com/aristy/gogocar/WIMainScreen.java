package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.BLUETOOTH_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.BT_REQUEST_ENABLE;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCONNECTING;
import static com.aristy.gogocar.HandlerCodes.BT_STATE_DISCOVERING;
import static com.aristy.gogocar.HandlerCodes.FRAGMENT_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.NAVIGATION_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.HandlerCodes.SET_DRIVING;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE_FROM_HOME;
import static com.aristy.gogocar.PermissionHelper.checkPermission;
import static com.aristy.gogocar.PermissionHelper.isBluetoothEnabled;
import static com.aristy.gogocar.PermissionHelper.isLocationEnabled;
import static com.aristy.gogocar.WebInterface.FunctionNames.DRIVING_REQUEST;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Interface for Screen (home, vehicles, drive, settings)
 */
public class WIMainScreen extends WICommon {

    private static final String path = "file:///android_asset/pages/";
    public static final String HOME = path + "home.html";

    Context context;
    Activity activity;
    UserPreferences userPreferences;
    Handler fragmentHandler;
    Handler bluetoothHandler;
    Handler navigationHandler;
    DatabaseHelper databaseHelper;

    public WIMainScreen(WebView webView, Context context, Activity activity, UserPreferences userPreferences, Handler [] handlers, Connection connection) {
        super(webView);

        this.context = context;
        this.activity = activity;

        this.userPreferences = userPreferences;
        this.fragmentHandler = handlers[FRAGMENT_HANDLER_POS];
        this.bluetoothHandler = handlers[BLUETOOTH_HANDLER_POS];
        this.navigationHandler = handlers[NAVIGATION_HANDLER_POS];

        this.databaseHelper = new DatabaseHelper(connection);
    }


    /*  ---------------------------------- *
     *  --           home.html          -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request data:<br>
     * - Name of the actual user <br>
     * - Get vehicles booked by the user
     */
    @JavascriptInterface
    public void requestData(){
        androidToWeb("setUserName", userPreferences.getUserName());

        // List booked vehicle for this user
        List<DBModelVehicle> vehicles = databaseHelper.getVehiclesBooked(userPreferences.getUserID());
        androidToWeb("setVehicleBooked", vehicles.toString());
    }

    /**
     * Request to change the page from home.html
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
            androidToWeb(DRIVING_REQUEST, WebInterface.ErrorCodes.DRIVING_REQUEST_PERMISSION_ERROR);
            return;
        }

        // Check if elements are activated
        if(!isBluetoothEnabled()){
            Toast.makeText(context, "Please enable bluetooth.", Toast.LENGTH_SHORT).show();
            bluetoothHandler.obtainMessage(BT_REQUEST_ENABLE).sendToTarget();
            androidToWeb(DRIVING_REQUEST, WebInterface.ErrorCodes.DRIVING_REQUEST_BLUETOOTH_DISABLED);
            return;
        }

        if(!isLocationEnabled(context)) {
            Toast.makeText(context, "Please enable location.", Toast.LENGTH_SHORT).show();
            androidToWeb(DRIVING_REQUEST, WebInterface.ErrorCodes.DRIVING_REQUEST_LOCALISATION_DISABLE);
            return;
        }

        // Block user to home fragment during the journey (yes)
        navigationHandler.obtainMessage(SET_DRIVING, true).sendToTarget();
        //Intent enableBtIntent
        bluetoothHandler.obtainMessage(BT_STATE_DISCOVERING).sendToTarget();
    }

    /**
     * When the user want to stop driving
     */
    @JavascriptInterface
    public void requestStopDrive(){
        navigationHandler.obtainMessage(SET_DRIVING, false).sendToTarget();
        bluetoothHandler.obtainMessage(BT_STATE_DISCONNECTING).sendToTarget();
    }

    /**
     * When the user want to remove his trip
     * @param vehicleID vehicle id
     */
    @JavascriptInterface
    public void requestCancelJourney(int vehicleID){
        boolean isUpdate = databaseHelper.setBookedVehicle(vehicleID, 0, false);

        if(!isUpdate) Toast.makeText(context, "ERROR: Can't cancel.", Toast.LENGTH_SHORT).show();
        else androidToWeb("journeyDelete", "true");
    }

    /*  ---------------------------------- *
     *  --          drive.html          -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request available vehicle
     */
    @JavascriptInterface
    public void requestDatabase(){
        // Simulation
        List<DBModelVehicle> vehicles = new ArrayList<>();
        androidToWeb("setDatabase", vehicles.toString());
    }

    /**
     * Called when the user want to book a vehicle<br>
     * In: <code>popup.js</code><br>
     * @param vehicle the vehicle wanted parsed in json format
     */
    @JavascriptInterface
    public void requestOpenBook(String vehicle){
        Log.d(TAG_Web, "requestOpenBook: " + vehicle);
        //fragmentHandler.obtainMessage(GOTO_BOOK_VEHICLE_FRAGMENT, vehicle).sendToTarget();
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
        // Simulation
        List<DBModelVehicle> vehicles = new ArrayList<>();
        androidToWeb("setDatabase", vehicles.toString());
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
     * Request to open the panel
     * @param panelName specified panel name
     */
    @JavascriptInterface
    public void openSlider(String panelName) {
        //Log.d(TAG_Web, "openSlider: " + path + containerName + ".html");
        fragmentHandler.obtainMessage(OPEN_SLIDER, path + "settings_" + panelName + ".html").sendToTarget();
    }

}
