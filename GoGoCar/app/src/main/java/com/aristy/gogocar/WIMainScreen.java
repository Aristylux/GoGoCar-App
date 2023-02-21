package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.GOTO_BOOK_VEHICLE_FRAGMENT;
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
    Handler handlerNavigation;

    public WIMainScreen(WebView webView, UserPreferences userPreferences, Handler handlerNavigation) {
        super(webView);

        this.userPreferences = userPreferences;
        this.handlerNavigation = handlerNavigation;
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
        handlerNavigation.obtainMessage(SET_PAGE_FROM_HOME, page).sendToTarget();
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

}
