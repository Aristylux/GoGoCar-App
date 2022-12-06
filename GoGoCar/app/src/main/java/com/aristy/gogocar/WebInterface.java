package com.aristy.gogocar;

import static android.content.Context.MODE_PRIVATE;
import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Web;
import static com.aristy.gogocar.SHAHash.hashPassword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.List;

public class WebInterface {

    Activity activity;
    Context context;
    WebView webView;
    ConstraintLayout layout;

    // Constructor
    WebInterface(Activity activity, Context context, WebView webView, ConstraintLayout layout){
        this.activity = activity;
        this.context = context;
        this.webView = webView;
        this.layout = layout;
    }

    /** ----------------------------- *
     *  -- Methods call by webPage -- *
     *  ----------------------------- */

    // Test
    @JavascriptInterface
    public void AuthenticationInit(){
        Log.d(TAG_Auth, "Auth init");
        // Get list of users
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<DBModelUser> users = databaseHelper.getAllUsers();
        Log.d(TAG_Auth, "database: fait.");
        Log.d(TAG_Database, users.toString());

        //databaseHelper.deleteUser(users.get(0));
    }

    // When Click on login button
    @JavascriptInterface
    public void AuthenticationLogin(String email, String password){
        // Hash password
        String hash = hashPassword(password);

        // Compare passwords
        int success = verify(email, hash);

        //if password and email are different
        if (success == -1) {
            // Send error to the page
            androidToWeb("errorAuthenticationLogin");
        } else {
            // Save user in app
            UserPreferences userdata = new UserPreferences();
            userdata.setUserID(success);

            SharedPreferences.Editor editor = context.getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE).edit();
            editor.putInt(UserPreferences.USER, userdata.getUserID());
            editor.apply();

            // Go to home
            webView.setFitsSystemWindows(false);
            loadNewPage("home");
        }
    }

    private int verify(String email, String hash){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        DBModelUser user = databaseHelper.getUserByEmail(email);
        // if user exist
        if(user.getPassword() != null){
            // if password == hash
            if(hash.equals(user.getPassword())){
                // Ok
                return user.getId();
            }
        }

        /*
        List<DBModelUser> users = databaseHelper.getAllUsers();
        for (DBModelUser user : users){
            Log.d(TAG_Auth, "verify: email='" + email + "', hash='" + hash + "'");
            Log.d(TAG_Auth, "user: email='" + user.getEmail() + "', hash='" + user.getPassword() + "'");
            if(email.equals(user.getEmail())){
                if (hash.equals(user.getPassword())){

                }
            }
        }

         */
        // Not ok
        return -1;
    }

    // When Click on register button
    @JavascriptInterface
    public void AuthenticationRegister(String fullName, String email, String phoneNumber, String password){
        // Open database
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        // Hash password
        String hash = hashPassword(password);
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


        // retrieve user id
        DBModelUser user_refresh = databaseHelper.getUserByEmail(email);
        UserPreferences userdata = new UserPreferences();
        userdata.setUserID(user_refresh.getId());

        // Save user for the application (user id)
        SharedPreferences.Editor editor = context.getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE).edit();
        editor.putInt(UserPreferences.USER, userdata.getUserID());
        editor.apply();

        webView.setFitsSystemWindows(false);
        loadNewPage("home");
    }

    @JavascriptInterface
    public void verifyEmail(String email, int successCode, int errorCode){
        // Check in database if email exist
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
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
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
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
        // Get user name
        androidToWeb("setUserName", "Axel");
    }

    @JavascriptInterface
    public void logout(){
        // logout
        SharedPreferences.Editor editor = context.getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE).edit();
        editor.putString(UserPreferences.USER, null);
        editor.apply();

        webView.post(() -> webView.loadUrl("file:///android_asset/login.html"));
    }

    /* Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast){
        //layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight(), layout.getPaddingBottom());
        //layout.setFitsSystemWindows(false);
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
        Database.selectRow(id_vehicle);
        //Log.d(TAG_Web, "Table: " + Arrays.deepToString(Database.getTable()));
        //Log.d(TAG_Web, "Table: " + Database.getVehicleName());
        //Log.d(TAG_Web, "Table: " + Database.getVehiclePosition());

        androidToWeb("openPopupBook", Database.getVehicleName(), Database.getVehiclePosition());
    }

    @JavascriptInterface
    public void changePage(String page){
        loadNewPage(page);
    }

    @JavascriptInterface
    public void requestDatabase(){
        //Log.d(TAG_Web, "Table: " + Arrays.deepToString(Database.getTable()));
        //webView.post(() -> webView.loadUrl("file:///android_asset/pages/drive.html"));
        webView.post(() -> webView.loadUrl("javascript:" + "setDatabase" + "('" + Database.getNewTable() + "')"));
        //androidToWeb("setDatabase", Arrays.deepToString(Database.getTable()));
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
        webView.post(() -> webView.loadUrl("javascript:" + function + "('" + data1 + "','" + data2 + "')"));     //webView.loadUrl("javascript:dataReceived('red')");
    }

}
