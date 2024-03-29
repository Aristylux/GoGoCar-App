package com.aristy.gogocar.WebInterfaces;

import android.webkit.WebView;

/**
 * Web Interface Common regroup all common methods for Web Interfaces
 */
public class WICommon {

    private final WebView webView;

    public WICommon (WebView webView){
        this.webView = webView;
    }

    /**
     * Send data to web
     * @param functionName javascript function name
     * @param data data in string
     */
    public void androidToWeb(String functionName, String... data){
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
     * Load a new page
     * @param webView custom webView
     * @param page page name to load
     */
    public void loadNewPage(WebView webView, String page){
        webView.post(() -> webView.loadUrl("file:///android_asset/pages/" + page + ".html"));
    }

    public static class Pages {
        public static final String path = "file:///android_asset/";
        public static final String pathPage = path + "pages/";
        public static final String BOOK_VEHICLE = pathPage + "drive_book.html";

        public static class JS {
            public static final String CLOSE_POPUP = "closePopup";
            public static final String RESET_DATA = "resetDatabase";
            public static final String SET_USER_INFO = "setUserInformation";

            public static final String CHANGE_PAGE = "pageChanged";
            public static final String SET_MODAL = "setModal";
        }

        public static class Login {
            public static final String LOGIN = path + "login.html";
            static class JS {
                public static final String ERROR_AUTH_LOGIN = "errorAuthenticationLogin";
                public static final String ERROR_AUTH_REGIS = "errorAuthenticationRegistration";
                public static final String SUCCESS = "success";
            }
        }

        public static class Home {
            public static final String HOME = pathPage + "home.html";
            public static class JS {
                public static final String SET_USER_NAME = "setUserName";
                public static final String SET_VEHICLE_BOOKED = "setVehicleBooked";
                public static final String DRIVING_REQUEST = "requestDriveCallback";
                public static final String DELETE_JOURNEY = "journeyDelete";
                public static final String UP_CAN_SPEED_VALUE = "updateSpeedValue";
                public static final String UP_CAN_FUEL_LEVEL = "updateFuelLevel";
                public static final String UP_CAN_FUEL_CONSUMPTION = "updateFuelConsumption";
                public static final String UP_CAN_ENGINE_COOLANT = "updateEngineCoolant";
                public static final String UP_CAN_ENGINE_WATER = "updateEngineWater";
                public static final String UP_CAN_ENGINE_OIL = "updateEngineOil";
            }
            public static class ErrorCodes {
                // home.js
                public static final String DRIVING_REQUEST_PERMISSION_ERROR = "1";
                public static final String DRIVING_REQUEST_BLUETOOTH_DISABLED = "2";
                public static final String DRIVING_REQUEST_LOCALISATION_DISABLE = "3";
                public static final String DRIVING_REQUEST_CAR_NOT_FOUND = "4";
                public static final String DRIVING_CONNECTION_FAILED = "5";
                public static final String DRIVING_CONNECTION_DISCONNECTED = "6";
            }
        }

        public static class Drive {
            public static final String DRIVE = pathPage + "drive.html";
            static class JS {
                public static final String ADD_VEHICLE = "cbDriveAddVehicle";
            }
        }

        public static class Vehicle {
            public static final String VEHICLE = pathPage + "vehicles.html";
            static class JS {
                public static final String SET_RESULT = "setResult";
                public static final String ADD_VEHICLE = "cbVehicleAddVehicle";
                public static final String DELETE_VEHICLE = "vehicleDelete";
            }
        }

        public static class VehicleAdd {
            public static final String ADD_VEHICLE = pathPage + "vehicles_add.html";
            static class JS {
                public static final String SET_VEHICLE = "setVehicle";
                public static final String ADD_VEHICLE = "addVehicleResult";
                public static final String SET_QRCODE_VALUE = "setQRCode";
            }
            public static class ErrorCodes {
                // vehicle_add.js
                public static final String ADD_VEHICLE_NO_ADDRESS = "1";
                public static final String ADD_VEHICLE_CAR_UNKNOWN = "2";
                public static final String ADD_VEHICLE_MODULE_CODE_UNKNOWN = "3";
                public static final String ADD_VEHICLE_FAILED = "4";
            }
        }

        public static class VehicleEdit {
            public static final String EDIT_VEHICLE = pathPage + "vehicles_edit.html";
            static class JS {
                public static final String EDIT_VEHICLE = "updateVehicleResult";
            }
            public static class ErrorCodes {
                // vehicle_edit.js
                public static final String EDIT_VEHICLE_MODULE_CODE_INCORRECT = "1";
                public static final String EDIT_VEHICLE_MODULE_CODE_USED = "2";
                public static final String EDIT_VEHICLE_FAILED = "3";
            }
        }

        public static class Setting {
            static class JS {
                public static final String SET_USER_NAME = "setUserName";
            }
        }
    }

    public static class Boolean {
        public static final String TRUE = "true";
        public static final String FALSE = "false";
    }

}
