package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
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

        // Result state page
        web.setWebViewClient(new Callback());
        web.loadUrl("file:///android_asset/index.html");

        // Interface
        web.addJavascriptInterface(new WebInterface(this, this, web), "Android");
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