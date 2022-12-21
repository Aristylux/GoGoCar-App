package com.aristy.gogocar;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
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

    Activity activity;
    Connection SQLConnection;
    UserPreferences userPreferences;
    Handler fragmentHandler;

    public FragmentLogin(Activity activity, Connection SQLConnection, UserPreferences userPreferences, Handler fragmentHandler){
        this.activity = activity;
        this.SQLConnection = SQLConnection;
        this.userPreferences = userPreferences;
        this.fragmentHandler = fragmentHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        WebView web = view.findViewById(R.id.web_view);
        web.loadUrl("file:///android_asset/login.html");

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.addJavascriptInterface(new WebInterface(activity, getContext(), web, SQLConnection, userPreferences, fragmentHandler), "Android");

        return view;
    }
}