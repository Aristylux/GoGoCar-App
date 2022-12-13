package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.CodesTAG.TAG_Info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    WebView web;
    ConnectionHelper connectionHelper;
    Connection SQLConnection;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to database (do it in other thread)
        connectionHelper = new ConnectionHelper();
        SQLConnection = connectionHelper.openConnection();

        // find items
        web = findViewById(R.id.web_view);
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new Callback());

        // Get user id (by default (unset) int=0, first element in database by default: 1)
        UserSharedPreference userdata = new UserSharedPreference(this);
        int userID = userdata.readUserID();
        Log.d(TAG_Auth, "userID: " + userID);

        UserPreferences userPreferences = new UserPreferences();
        //constraintLayout.setFitsSystemWindows(false);

        // If user if is equal to 0, the user is not logged
        if(userID == 0) {
            constraintLayout.setFitsSystemWindows(true);
            web.loadUrl("file:///android_asset/login.html");
        } else {
            // Load page
            web.loadUrl("file:///android_asset/pages/home.html");

            // Retrieve user from data in app
            DBModelUser user = userdata.readUser();
            userPreferences.setUser(user);
        }
        // Interface
        web.addJavascriptInterface(new WebInterface(this, this, web, constraintLayout, SQLConnection, userPreferences), "Android");

        // For top bar and navigation bar
        setWindowVersion();
    }

    // First time, appear after onCreate
    @Override
    protected void onStart() {
        super.onStart();
        try {
            // If the connection to the server is close, open it
            if (SQLConnection != null) {
                if (SQLConnection.isClosed()) {
                    Log.d(TAG_Database, "onStart: open SQL Connection");
                    SQLConnection = connectionHelper.openConnection();
                }
            } else {
                Log.e(TAG_Database, "onStart: ERROR open SQL Connection: null");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Database, "onStart: ERROR open SQL Connection", exception);
            exception.printStackTrace();
        }
    }

    // Quit app without kill process
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // The user leave application, close connection to the server.
            if (SQLConnection != null) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: null");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
    }

    public void setWindowVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <  Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG_Debug, "setWindowVersion: 1");
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
            Log.d(TAG_Debug, "setWindowVersion: 2");
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG_Debug, "setWindowVersion: 3");
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //open in app
    public static class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }

        public void onPageFinished(WebView view, String url){
            //Here you want to use .loadUrl again
            //on the webView object and pass in
            //"javascript:<your javaScript function"
            //Set<BluetoothDevice> bluetoothDevice = getBluetoothPairedDevices();
            //error here when bt is not activated
            //populateSpinner(bluetoothDevice);
        }

    }
}