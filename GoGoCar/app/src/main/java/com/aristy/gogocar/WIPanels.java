package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;

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
    // Parent screen : drive.html

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
    // Parent screen: vehicles.html

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

    // ---- Edit vehicle ----
    // Parent screen: vehicle.html

    /**
     * Request an update to the database<br>
     * Verify the module code is correct (exist)<br>
     * Verify the module code is available (not used by another vehicle)<br>
     *
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
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultModule(DBModelModule module) {
                if (module.getId() == 0){
                    //Toast.makeText(context, "module code incorrect", Toast.LENGTH_SHORT).show();
                    Log.e(TAG_Web, "onResultModule: module code incorrect");
                    androidToWeb("updateVehicleResult", "2");  // Error code 2
                } else {
                    // Modify the vehicle
                    DBModelVehicle vehicle = new DBModelVehicle();
                    vehicle.setId(id);
                    vehicle.setModel(model);
                    vehicle.setLicencePlate(licencePlate);
                    vehicle.setAddress(address);
                    vehicle.setIdModule(module.getId());
                    vehicle.setAvailable(isAvailable);

                    // Check if the module code is available
                    verifyAvailability(vehicle);
                }
            }
        });
        thread.getModuleByName(moduleCode);
    }

    /**
     * Verify the module code is not used by an another vehicle
     * @param vehicle vehicle updated
     */
    private void verifyAvailability(DBModelVehicle vehicle){
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultVehicle(DBModelVehicle vehicleResulted) {
                if (vehicleResulted.getId() != vehicle.getId()){
                    Log.e(TAG_Web, "onResultVehicles: module code already used");
                    androidToWeb("updateVehicleResult", "3");  // Error code 3
                } else {
                    updateVehicle(vehicle);
                }
            }
        });
        thread.getVehicleByModule(vehicle.getIdModule());
    }

    /**
     * Update the vehicle in the database
     * @param vehicle vehicle updated
     */
    private void updateVehicle(DBModelVehicle vehicle){
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean success) {
                if (!success) {
                    //Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG_Web, "onResultVehicles: An error occured during update.");
                    androidToWeb("updateVehicleResult", "4");  // Error code 4
                } else {
                    // Return top vehicle fragment
                    handler.obtainMessage(CLOSE_SLIDER).sendToTarget();
                }
            }
        });
        thread.updateVehicle(vehicle);
    }

}
