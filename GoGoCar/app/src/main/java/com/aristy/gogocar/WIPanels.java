package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;
import static com.aristy.gogocar.HandlerCodes.QUERY;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WIPanels extends WICommon {

    UserPreferences userPreferences;
    Handler handler;
    String data;

    ThreadManager thread;

    public WIPanels(WebView webView, UserPreferences userPreferences, Handler handler, String data) {
        super(webView);

        this.userPreferences = userPreferences;
        this.handler = handler;
        this.data = data;

        this.thread = ThreadManager.getInstance();
    }

    @JavascriptInterface
    public void requestClosePanel(){
        handler.obtainMessage(CLOSE_SLIDER).sendToTarget();
    }

    // ---- Book ----

    @JavascriptInterface
    public void requestBookVehicle(int vehicleID, String pickupDate, String dropDate, int capacity){
        Log.d(TAG_Web, "requestBookVehicle: " + vehicleID + ", " + pickupDate + ", " + dropDate + ", " + capacity);

        // Check if the vehicle is available for these dates

        // If everything is ok, update database
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean isUpdated) {
                if(!isUpdated) Log.e(TAG_Web, "onResultTableUpdated: ERROR: Can't update.");
                else handler.obtainMessage(CLOSE_SLIDER).sendToTarget();
            }
        });
        thread.setBookedVehicle(vehicleID, userPreferences.getUserID(), true);
    }

    // ---- Personal information container ----

    @JavascriptInterface
    public void requestPersonalInformation() {
        androidToWeb("setUserInformation", userPreferences.toString());
    }

    // ---- Add ----

    /**
     * Send vehicle (in main) to the new fragment
     */
    @JavascriptInterface
    public void requestGetVehicle(){
        Log.d("GoGoCar_T", "requestGetVehicle: get vehicle:" + data);
        androidToWeb("setVehicle", data);
        //fragmentHandler.obtainMessage(DATA_SET_VEHICLE).sendToTarget();
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
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultModule(DBModelModule module) {
                if(module.getId() == 0){
                    //Toast.makeText(context, "module code incorrect", Toast.LENGTH_SHORT).show();
                    androidToWeb("addVehicleResult", "2");  // Error code 2
                } else
                    addVehicle(model, licencePlate, address, module.getId(), isAvailable);
            }
        });
        thread.getModuleByName(moduleCode);
    }

    private void addVehicle(String model, String licencePlate, String address, int moduleID, boolean isAvailable){
        // Success: add vehicle & quit page
        // Create vehicle
        DBModelVehicle vehicle = new DBModelVehicle();
        vehicle.setModel(model);
        vehicle.setLicencePlate(licencePlate);
        vehicle.setAddress(address);
        vehicle.setIdOwner(userPreferences.getUserID());
        vehicle.setAvailable(isAvailable);
        vehicle.setBooked(false);
        vehicle.setIdModule(moduleID);

        // Add vehicle into vehicle table
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean success) {
                Log.d(TAG_Database, "success=" + success);
                if (!success) {
                    //Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
                    androidToWeb("addVehicleResult", "3");  // Error code 3
                } else {
                    // Return top vehicle fragment
                    handler.obtainMessage(CLOSE_SLIDER).sendToTarget();
                }
            }
        });
        thread.addVehicle(vehicle);
    }

}
