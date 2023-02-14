package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.sql.Connection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentLogin extends Fragment {

    Connection SQLConnection;
    UserPreferences userPreferences;
    Handler [] handlers;

    public FragmentLogin(Connection SQLConnection, UserPreferences userPreferences, Handler [] handlers){
        this.SQLConnection = SQLConnection;
        this.userPreferences = userPreferences;
        //this.handlers = handlers;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Find items
        WebView web = view.findViewById(R.id.web_view);
        web.loadUrl("file:///android_asset/login.html");

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.addJavascriptInterface(new WebInterface(getActivity(), getContext(), web, SQLConnection, userPreferences, handlers), "Android");

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HandlerWrapper handlerWrapperFRG = (HandlerWrapper) getArguments().getSerializable("FRGHandler");
            Handler fragmentHandler = handlerWrapperFRG.getHandler();
            //Log.d(TAG_Auth, "onCreate: handler: " + fragmentHandler);
            HandlerWrapper handlerWrapperBLE = (HandlerWrapper) getArguments().getSerializable("BLEHandler");
            Handler bluetoothHandler = handlerWrapperBLE.getHandler();
            handlers = new Handler[]{fragmentHandler, bluetoothHandler};
        }
    }
}