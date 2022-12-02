package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Info;

import androidx.appcompat.app.AppCompatActivity;

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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView web;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find items
        web = findViewById(R.id.web_view);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // useful ?
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);

        web.setWebChromeClient(new WebChromeClient());

        // Result state page
        web.setWebViewClient(new Callback());
        //web.loadUrl("file:///android_asset/index.html");

        // Get user id
        SharedPreferences userdata = getSharedPreferences(UserPreferences.DATA, MODE_PRIVATE);
        int userID = userdata.getInt(UserPreferences.USER, UserPreferences.ID);
        if(userID == 0)
            web.loadUrl("file:///android_asset/login.html");
        else
            web.loadUrl("file:///android_asset/pages/home.html");

        // Interface
        web.addJavascriptInterface(new WebInterface(this, this, web), "Android");

        // For top bar and navigation bar
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        if (Build.VERSION.SDK_INT >= 21) {
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