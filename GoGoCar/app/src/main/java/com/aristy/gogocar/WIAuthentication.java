package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;
import static com.aristy.gogocar.SHAHash.generateSalt;
import static com.aristy.gogocar.SHAHash.hashPassword;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Web Interface for Authentication screen
 */
public class WIAuthentication extends WICommon {

    private static final String path = "file:///android_asset/";
    public static final String LOGIN = path + "login.html";

    Context context;
    UserPreferences userPreferences;
    Handler fragmentHandler;
    ThreadManager thread;

    public WIAuthentication(Context context, WebView webView, UserPreferences userPreferences, Handler fragmentHandler) {
        super(webView);

        this.context = context;

        this.thread = ThreadManager.getInstance();

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

        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultUser(DBModelUser user) {
                // Verify user exist for this email and password
                DBModelUser userVerified = verify(user, hash);

                // If user doesn't exist
                if (userVerified == null) {
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
        });
        thread.getUserByEmail(email);
    }

    /**
     * Verify if user exist
     * @param user      user retrieved
     * @param hash      user password hash
     * @return user if success,<br>
     *         null if not.
     */
    private DBModelUser verify(DBModelUser user, String hash){
        //DBModelUser user = databaseHelper.getUserByEmail(email);
        // If user exist
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

        // Generate salt
        String salt = generateSalt();
        Log.d(TAG_Auth, "pw= \"" + password + "\", hash= \"" + hash + "\", salt= \"" + salt + "\"");

        // Create user
        DBModelUser user = new DBModelUser(fullName, email, phoneNumber, hash, salt);

        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultTableUpdated(boolean success) {
                Log.d(TAG_Auth, "success=" + success);
                if (!success) {
                    // print error on web
                    Log.e(TAG_Auth, "onResultTableUpdated: An error occured.");

                    // crash: Can't toast on a thread that has not called Looper.prepare()
                    //Toast.makeText(context, "An error occured.", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserInApp(email);
                }
            }
        });
        // Add user into user table
        thread.addUser(user);
    }

    private void saveUserInApp(String email){
        // Retrieve user id
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultUser(DBModelUser user) {
                // Set user in app & Save user for the application (user id)
                userPreferences.setUser(user);

                // Load home page
                fragmentHandler.obtainMessage(GOTO_HOME_FRAGMENT).sendToTarget();
                fragmentHandler.obtainMessage(STATUS_BAR_COLOR, (int) HexColor.TRANSPARENT).sendToTarget();
            }
        });
        thread.getUserByEmail(email);
    }

    /**
     * Verify email isn't in the database
     * @param email         new email to verify
     * @param successCode   code to return in success
     * @param errorCode     code to return in failure
     */
    @JavascriptInterface
    public void verifyEmail(String email, int successCode, int errorCode){
        Log.d(TAG_Auth, "verifyEmail: ");
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultUser(DBModelUser user) {
                Log.d(TAG_Auth, "verifyEmail: " + user.toString());

                // If != null (exist) -> error
                if (user.getEmail() == null) androidToWeb("success", String.valueOf(successCode));
                else androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
            }
        });
        Log.d(TAG_Auth, "verifyEmail: get");
        thread.getUserByEmail(email);
    }

    /**
     * Verify phone isn't in the database
     * @param phone         new phone to verify
     * @param successCode   code to return in success
     * @param errorCode     code to return in failure
     */
    @JavascriptInterface
    public void verifyPhone(String phone, int successCode, int errorCode){
        Log.d(TAG_Auth, "verifyPhone: ");
        thread.setResultCallback(new ThreadResultCallback() {
            @Override
            public void onResultUser(DBModelUser user) {
                Log.d(TAG_Auth, "verifyPhone: " + user.toString());

                // If exist -> error
                if (user.getPhoneNumber() == null) androidToWeb("success", String.valueOf(successCode));
                else androidToWeb("errorAuthenticationRegistration", String.valueOf(errorCode));
            }
        });
        thread.getUserByPhone(phone);
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
