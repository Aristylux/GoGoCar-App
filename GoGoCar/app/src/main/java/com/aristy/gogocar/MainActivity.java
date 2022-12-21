package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.CodesTAG.TAG_Info;
import static com.aristy.gogocar.CodesTAG.TAG_Web;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    //WebView web;
    ConnectionHelper connectionHelper;
    Connection SQLConnection;

    UserPreferences userPreferences;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Connect to database (do it in other thread)
        connectionHelper = new ConnectionHelper();
        SQLConnection = connectionHelper.openConnection();

        //ConstraintLayout constraintLayout = findViewById(R.id.layout);


        /*
        // find items
        web = findViewById(R.id.web_view);


        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new Callback());
         */

        // Get user id (by default (unset) int=0, first element in database by default: 1)
        UserSharedPreference userdata = new UserSharedPreference(this);
        int userID = userdata.readUserID();
        Log.d(TAG_Auth, "userID: " + userID);

        userPreferences = new UserPreferences();

        Fragment selectedFragment;

        // If user if is equal to 0, the user is not logged
        if(userID == 0) {
            //constraintLayout.setFitsSystemWindows(true);
            //web.loadUrl("file:///android_asset/login.html");
            //Window window = getWindow();
            //Log.d(TAG_Web, "changeBackground : window" + window);

            // Finally change the color
            //window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
            selectedFragment = new FragmentLogin(MainActivity.this, SQLConnection, userPreferences, fragmentHandler);
        } else {
            selectedFragment = new FragmentApp(SQLConnection, userPreferences, fragmentHandler);
            // Load page
            //web.loadUrl("file:///android_asset/pages/home.html");

            // Retrieve user from data in app
            DBModelUser user = userdata.readUser();
            userPreferences.setUser(user);
        }


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.commit();

        // Interface
        //web.addJavascriptInterface(new WebInterface(this, this, web, constraintLayout, SQLConnection, userPreferences), "Android");

        // For top bar and navigation bar
        setWindowVersion();
    }

    // First time, appear after onCreate
    @Override
    protected void onStart() {
        super.onStart();
        // If the connection to the server is close, open it
        if (!connectionValid()) {
            Log.d(TAG_Database, "onStart: open SQL Connection");
            SQLConnection = connectionHelper.openConnection();
        } else {
            Log.e(TAG_Database, "onStart: ERROR open SQL Connection: invalid");
        }
    }

    public boolean connectionValid(){
        try {
            Log.d(TAG_Database, "connectionValid: SQLConnection=" + SQLConnection + ", close?=" + SQLConnection.isClosed());
            if (SQLConnection != null)
                return !SQLConnection.isClosed();
            else
                return false;
        } catch (SQLException exception) {
            Log.e(TAG_Database, "connectValid: ", exception);
            exception.printStackTrace();
            return false;
        }
    }

    // Quit app without kill process
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
    }

    public void setWindowVersion(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void setWindowFlag(Activity activity, final int bits) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~bits;
        win.setAttributes(winParams);
    }

    Handler fragmentHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            //setWindowFlag(MainActivity.this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (message.what){
                case 1:
                    fragmentTransaction.replace(R.id.fragment_container, new FragmentApp(SQLConnection, userPreferences, fragmentHandler));
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.commit();
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);  // = 0
                    break;
                case 2:
                    fragmentTransaction.replace(R.id.fragment_container, new FragmentLogin(MainActivity.this, SQLConnection, userPreferences, fragmentHandler));
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                    fragmentTransaction.commit();
                    break;
                case 3:
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);
                    break;
            }
            return true;
        }
    });

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