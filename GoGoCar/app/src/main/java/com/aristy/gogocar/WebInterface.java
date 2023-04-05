package com.aristy.gogocar;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

import java.sql.Connection;

public class WebInterface {


    // Constructor
    WebInterface(Activity activity, Context context, WebView webView, Connection connection, UserPreferences userPreferences, Handler [] handlers){
    }


    /*  ---------------- *
     *  -- Interfaces -- *
     *  ---------------- */

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
