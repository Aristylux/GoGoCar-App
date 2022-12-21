package com.aristy.gogocar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.sql.Connection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentApp} factory method to
 * create an instance of this fragment.
 */
public class FragmentApp extends Fragment {

    Connection SQLConnection;
    UserPreferences userPreferences;
    Handler fragmentHandler;

    public FragmentApp(Connection SQLConnection, UserPreferences userPreferences, Handler fragmentHandler){
        this.SQLConnection = SQLConnection;
        this.userPreferences = userPreferences;
        this.fragmentHandler = fragmentHandler;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        // Find items
        WebView web = view.findViewById(R.id.web_view);
        web.loadUrl("file:///android_asset/pages/home.html");

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new Callback());

        web.addJavascriptInterface(new WebInterface(getActivity(), getContext(), web, SQLConnection, userPreferences, fragmentHandler), "Android");

        return view;
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