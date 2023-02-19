package com.aristy.gogocar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
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

    // The fragment initialization parameters
    private static final String ARG_USER_PREF = "userPref";
    private static final String ARG_FRG_HANDLER = "FRGHandler";
    private static final String ARG_BLE_HANDLER = "BLEHandler";
    private static final String ARG_LINK = "webLink";


    // the fragment parameters name for execute javascript function on web
    public final static String ARG_FUNCTION_NAME = "func_name";
    public final static String ARG_FUNCTION_PARAMS = "func_param";

    Connection SQLConnection;
    UserPreferences userPreferences;
    Handler [] handlers;
    String link;

    WebView web;

    public FragmentApp(Connection SQLConnection){
        // Required empty public constructor
        this.SQLConnection = SQLConnection;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userPreferences user preferences
     * @param fragmentHandler fragment handler
     * @param bluetoothHandler bluetooth handler
     * @param webLink link web page
     * @param SQLConnection sql connection
     * @return A new instance of fragment FragmentApp.
     */
    public static FragmentApp newInstance(UserPreferences userPreferences, Handler fragmentHandler, Handler bluetoothHandler, String webLink, Connection SQLConnection){
        FragmentApp fragment = new FragmentApp(SQLConnection);
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRG_HANDLER, new HandlerWrapper(fragmentHandler));
        args.putSerializable(ARG_BLE_HANDLER, new HandlerWrapper(bluetoothHandler));
        args.putParcelable(ARG_USER_PREF, userPreferences);
        args.putString(ARG_LINK, webLink);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get handler for fragments
            HandlerWrapper handlerWrapperFRG = (HandlerWrapper) getArguments().getSerializable(ARG_FRG_HANDLER);
            Handler fragmentHandler = handlerWrapperFRG.getHandler();

            // Get handler for bluetooth
            HandlerWrapper handlerWrapperBLE = (HandlerWrapper) getArguments().getSerializable(ARG_BLE_HANDLER);
            Handler bluetoothHandler = handlerWrapperBLE.getHandler();

            this.handlers = new Handler[]{fragmentHandler, bluetoothHandler};

            // Get user preferences
            this.userPreferences = getArguments().getParcelable(ARG_USER_PREF);

            // Get web link
            this.link = getArguments().getString(ARG_LINK);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        // Find items & set url page
        web = view.findViewById(R.id.web_view);
        web.loadUrl(link);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new Callback());

        web.addJavascriptInterface(new WebInterface(getActivity(), getContext(), web, SQLConnection, userPreferences, handlers), "Android");

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

    public void putArguments(Bundle args){
        String functionName = args.getString(ARG_FUNCTION_NAME);
        String params = args.getString(ARG_FUNCTION_PARAMS);
        if (web != null)
            web.post(() -> web.loadUrl("javascript:" + functionName + "('" + params + "')"));
        else
            Log.e("GoGoCar_Fragments", "putArguments: error, web = null");
    }
    
}