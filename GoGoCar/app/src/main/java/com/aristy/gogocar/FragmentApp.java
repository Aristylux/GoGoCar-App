package com.aristy.gogocar;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        WebView web = view.findViewById(R.id.web_view);
        web.loadUrl("file:///android_asset/pages/home.html");

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.addJavascriptInterface(new WebInterface(getActivity(), getContext(), web, null, SQLConnection, userPreferences, fragmentHandler), "Android");

        return view;
    }
}