package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.BLUETOOTH_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.FRAGMENT_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.NAVIGATION_HANDLER_POS;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.WINavigation.SET_PAGE_FROM_HOME;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Web Interface for Screen (home, vehicles, drive, settings)
 */
public class WIMainScreen extends WICommon {

    private static final String path = "file:///android_asset/pages/";
    public static final String HOME = path + "home.html";

    UserPreferences userPreferences;
    Handler fragmentHandler;
    Handler bluetoothHandler;
    Handler navigationHandler;

    public WIMainScreen(WebView webView, UserPreferences userPreferences, Handler [] handlers) {
        super(webView);

        this.userPreferences = userPreferences;
        this.fragmentHandler = handlers[FRAGMENT_HANDLER_POS];
        this.bluetoothHandler = handlers[BLUETOOTH_HANDLER_POS];
        this.navigationHandler = handlers[NAVIGATION_HANDLER_POS];
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

        // TODO 'Get vehicles booked by the user'
    }

    /**
     * Request to change the page from home.html
     * @param page new page to load ('drive' or 'vehicle')
     */
    @JavascriptInterface
    public void requestChangePage(String page){
        navigationHandler.obtainMessage(SET_PAGE_FROM_HOME, page).sendToTarget();
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
