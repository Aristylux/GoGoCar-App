package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;
import static com.aristy.gogocar.SHAHash.hashPassword;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.sql.Connection;

/**
 * Web Interface for Authentication screen
 */
public class WIAuthentication extends WICommon {

    private static final String path = "file:///android_asset/";
    public static final String LOGIN = path + "login.html";

    Context context;
    UserPreferences userPreferences;
    Handler fragmentHandler;
    DatabaseHelper databaseHelper;

    public WIAuthentication(Context context, WebView webView, Connection connection, UserPreferences userPreferences, Handler fragmentHandler) {
        super(webView);

        this.context = context;

        this.databaseHelper = new DatabaseHelper(connection);
        this.userPreferences = userPreferences;
        this.userPreferences.setContext(context);   // Update context

        this.fragmentHandler = fragmentHandler;
    }

    /*  ---------------------------------- *
     *  --          login.html          -- *
     *  ---------------------------------- */

    /**
     * When Click on login button
     * @param email     input user email
     * @param password  input user password
     */
    @JavascriptInterface
    public void AuthenticationLogin(String email, String password){
        // Hash password
        String hash = hashPassword(password, SHAHash.DOMAIN);

        // Verify user exist for this email and password
        DBModelUser user = verify(email, hash);

        // If user doesn't exist
        if (user == null) {
            Log.e(TAG_Auth, "AuthenticationLogin: this user isn't in our database");
            // Send error to the page
            androidToWeb("errorAuthenticationLogin");
        } else {
            // Set user in app & Save user for the application (user id)
            userPreferences.setUser(user);

            // Go to home
            fragmentHandler.obtainMessage(GOTO_HOME_FRAGMENT).sendToTarget();
            fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) HexColor.TRANSPARENT).sendToTarget();
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
        DBModelUser user = databaseHelper.getUserByEmail(email);
        // if user exist
        if(user.getPassword() != null){
            // Compare passwords, if hash_password == hash
            if(hash.equals(user.getPassword())) return user;    // Ok
        }
        // If password and email are different: Not ok
        return null;
    }

    /**
     * When Click on register button
     * @param fullName      input name
     * @param email         input email
     * @param phoneNumber   input phone number
     * @param password      input password
     */
    @JavascriptInterface
    public void AuthenticationRegister(String fullName, String email, String phoneNumber, String password){
        // Hash password
        String hash = hashPassword(password, SHAHash.DOMAIN);
        Log.d(TAG_Auth, "pw= \"" + password + "\", hash= \"" + hash + "\"");

        // Create user
        DBModelUser user = new DBModelUser(-1, fullName, email, phoneNumber, hash);

        // Add user into user table
        boolean success = databaseHelper.addUser(user);
        Log.d(TAG_Auth, "success=" + success);
        if (!success) {
            Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve user id
        DBModelUser user_refresh = databaseHelper.getUserByEmail(email);

        // Set user in app & Save user for the application (user id)
        userPreferences.setUser(user_refresh);

        // Load home page
        fragmentHandler.obtainMessage(GOTO_HOME_FRAGMENT).sendToTarget();
        fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) HexColor.TRANSPARENT).sendToTarget();
    }

    /**
     * Verify email isn't in the database
     * @param email         new email to verify
     * @param successCode   code to return in success
     * @param errorCode     code to return in failure
     */
    @JavascriptInterface
    public void verifyEmail(String email, int successCode, int errorCode){
        // Check in database if email exist
        DBModelUser user = databaseHelper.getUserByEmail(email);
        Log.d(TAG_Auth, "verifyEmail: " + user.toString());

        // If exist -> error
        if (user.getEmail() == null) // not exist
            androidToWeb("success", String.valueOf(successCode));
        else
            androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
    }

    /**
     * Verify phone isn't in the database
     * @param phone         new phone to verify
     * @param successCode   code to return in success
     * @param errorCode     code to return in failure
     */
    @JavascriptInterface
    public void verifyPhone(String phone, int successCode, int errorCode){
        DBModelUser user = databaseHelper.getUserByPhone(phone);
        Log.d(TAG_Auth, "verifyPhone: " + user.toString());

        // If exist -> error
        if (user.getPhoneNumber() == null) // not exist
            androidToWeb("success", String.valueOf(successCode));
        else
            androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
    }

    /**
     * Convert and change the color of the status bar
     * @param webColor web color using RRGGBBAA
     */
    @JavascriptInterface
    public void changeBackground(String webColor){
        // Convert Color
        HexColor hexColor = new HexColor(webColor);
        hexColor.convertToAndroidColor();
        long colorSigned = hexColor.getDecSigned();

        // Request to change the color
        fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) colorSigned).sendToTarget();
    }
}
