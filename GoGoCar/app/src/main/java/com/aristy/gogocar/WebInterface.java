package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.SHAHash.hashPassword;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.sql.Connection;
import java.util.List;

public class WebInterface {

    Activity activity;
    Context context;
    WebView webView;
    ConstraintLayout layout;
    
    Connection connection;
    UserPreferences userPreferences;

    // Constructor
    WebInterface(Activity activity, Context context, WebView webView, ConstraintLayout layout, Connection connection, UserPreferences userPreferences){
        this.activity = activity;
        this.context = context;
        this.webView = webView;
        this.layout = layout;

        this.connection = connection;
        this.userPreferences = userPreferences;
    }

    /* ----------------------------- *
       -- Methods call by webPage -- *
       ----------------------------- */

    /** Login */

    // When Click on login button
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

            // Set user in app
            userPreferences.setUser(user);

            //
            UserSharedPreference userdata = new UserSharedPreference(context);
            userdata.writeUser(user);

            // Go to home
            webView.setFitsSystemWindows(false);
            loadNewPage("home");
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
        DatabaseHelper databaseHelper = new DatabaseHelper(connection);

        DBModelUser user = databaseHelper.getUserByEmail(email);
        // if user exist
        if(user.getPassword() != null){
            // Compare passwords, if hash_password == hash
            if(hash.equals(user.getPassword())){
                // Ok
                return user;
            }
        }
        // If password and email are different: Not ok
        return null;
    }

    // When Click on register button
    @JavascriptInterface
    public void AuthenticationRegister(String fullName, String email, String phoneNumber, String password){
        // Open database
        DatabaseHelper databaseHelper = new DatabaseHelper(connection);

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

        // Set user in app
        userPreferences.setUser(user_refresh);

        // Save user for the application (user id)
        UserSharedPreference userdata = new UserSharedPreference(context);
        userdata.writeUser(user_refresh);

        webView.setFitsSystemWindows(false);
        loadNewPage("home");
    }

    @JavascriptInterface
    public void verifyEmail(String email, int successCode, int errorCode){
        // Check in database if email exist
        DatabaseHelper databaseHelper = new DatabaseHelper(connection);
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
        DatabaseHelper databaseHelper = new DatabaseHelper(connection);
        DBModelUser user = databaseHelper.getUserByPhone(phone);
        Log.d(TAG_Auth, "verifyPhone: " + user.toString());

        // If exist -> error
        if (user.getPhoneNumber() == null) // not exist
            androidToWeb("success", String.valueOf(successCode));
        else
            androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
    }

    /** home.html */

    @JavascriptInterface
    public void requestDrive(){

        //Intent enableBtIntent


        androidToWeb("requestDriveCallback", "true");
    }

    /** Settings.html */

    @JavascriptInterface
    public void requestUserName(){
        androidToWeb("setUserName", userPreferences.getUserName());
    }

    @JavascriptInterface
    public void logout(){
        // Reset user to default
        UserSharedPreference userdata = new UserSharedPreference(context);
        userdata.resetData();

        // Load page of login
        webView.post(() -> webView.loadUrl("file:///android_asset/login.html"));
    }

    /* Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void sendData(String action){
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();

        dataReceived();
    }

    // Not used
    @JavascriptInterface
    public void changeBackground(String webColor){
        Log.d(TAG_Web, "changeBackground : " + webColor);

        HexColor hexColor = new HexColor(webColor);
        hexColor.convertToAndroidColor();
        long colorSigned = hexColor.getDecSigned();
        //String colorAndroid = convertWebColor(webColor);²
        //long colorSigned = hexToSignedDec(colorAndroid);

        Window window = activity.getWindow();
        Log.d(TAG_Web, "changeBackground : window" + window);

        // Finally change the color
        //window.setStatusBarColor(ContextCompat.getColor(context, R.color.my_statusbar_color));
        window.setStatusBarColor((int) colorSigned);

        Log.d(TAG_Web, "changeBackground : color: " + ContextCompat.getColor(context, R.color.my_statusbar_color));
        Log.d(TAG_Web, "changeBackground : color: " + Integer.toHexString(ContextCompat.getColor(context, R.color.my_statusbar_color)));
        Log.d(TAG_Web, "changeBackground : color: " + ContextCompat.getColor(context, R.color.white));
        Log.d(TAG_Web, "changeBackground : color: " + Integer.toHexString(ContextCompat.getColor(context, R.color.white)));
        Log.d(TAG_Web, "changeBackground : color: " + "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.my_statusbar_color)));
    }

    @JavascriptInterface
    public void openPopupBook(int id_vehicle){
        Log.d(TAG_Web, "id vehicle: " + id_vehicle);

        DatabaseHelper databaseHelper = new DatabaseHelper(connection);
        DBModelVehicle vehicle = databaseHelper.getVehicleById(id_vehicle);

        androidToWeb("openPopupBook", vehicle.getModel(), vehicle.getAddress());
    }

    @JavascriptInterface
    public void changePage(String page){
        loadNewPage(page);
    }

    @JavascriptInterface
    public void requestDatabase(){
        DatabaseHelper databaseHelper = new DatabaseHelper(connection);
        List<DBModelVehicle> vehicles = databaseHelper.getAllVehicles();
        Log.d(TAG_Web, "requestDatabase: " + vehicles.toString());
        androidToWeb("setDatabase", vehicles.toString());
    }

    /** ---------------------------------- *
     *  -- Methods send data to webPage -- *
     *  ---------------------------------- */

    public void dataReceived(){
        androidToWeb("dataReceived", "red");
    }

    /** ---------------- *
     *  -- Interfaces -- *
     *  ---------------- */
    private void loadNewPage(String page){
        webView.post(() -> webView.loadUrl("file:///android_asset/pages/" + page + ".html"));
    }

    private void androidToWeb(String function){
        androidToWeb(function, "");
    }

    private void androidToWeb(String function, String data){
        webView.post(() -> webView.loadUrl("javascript:" + function + "('" + data + "')"));     //webView.loadUrl("javascript:dataReceived('red')");
    }

    private void androidToWeb(String function, String data1, String data2){
        webView.post(() -> webView.loadUrl("javascript:" + function + "('" + data1 + "','" + data2 + "')"));
    }

}
