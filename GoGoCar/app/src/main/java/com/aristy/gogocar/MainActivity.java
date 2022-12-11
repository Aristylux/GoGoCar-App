package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Error;
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
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            Connection connect = connectionHelper.openConnection();

            if (connect != null) {
                String query = "SELECT * FROM users";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
/*
                while (rs.next()) {
                    Log.d(TAG_Database, "User:id=" + rs.getInt(1) + ",name=" + rs.getString(2) + ", phone=" + rs.getString(3) + ".");
                }
*/
                rs.close();
                st.close();
                connect.close();
            } else {
                Log.d(TAG_Error, "connect is null");
            }
        }catch (Exception exception){
            Log.e(TAG_Error, "Error :" + exception);
        }

        // find items
        web = findViewById(R.id.web_view);
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new Callback());

        // Get user id (by default (unset) int=0, first element in database : 1)
        SharedPreferences userdata = getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE);
        int userID = userdata.getInt(UserPreferences.USER, UserPreferences.ID);
        Log.d(TAG_Info, "userID: " + userID);
        //constraintLayout.setFitsSystemWindows(false);
        if(userID == 0) {
            constraintLayout.setFitsSystemWindows(true);
            web.loadUrl("file:///android_asset/login.html");
        } else {
            web.loadUrl("file:///android_asset/pages/home.html");
        }
        // Interface
        web.addJavascriptInterface(new WebInterface(this, this, web, constraintLayout), "Android");

        // For top bar and navigation bar
        setWindowVersion();
    }

    public void setWindowVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <  Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP) {
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