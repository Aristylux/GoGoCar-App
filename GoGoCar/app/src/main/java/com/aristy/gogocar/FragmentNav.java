package com.aristy.gogocar;


import static com.aristy.gogocar.WebInterface.HOME;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;

public class FragmentNav extends Fragment {


    WebView web;

    public FragmentNav () {
        // Required empty public constructor
    }

    public static FragmentNav newInstance(){
        FragmentNav fragment = new FragmentNav();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav, container, false);

        // Find items
        WebView webNav = view.findViewById(R.id.web_view_nav);
        web = view.findViewById(R.id.web_view_content);
        webNav.loadUrl("file:///android_asset/nav.html");
        web.loadUrl(HOME);

        // Enable javascript
        WebSettings webSettingsNav = webNav.getSettings();
        webSettingsNav.setJavaScriptEnabled(true);
        //webNav.addJavascriptInterface(, "Android");

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //web.addJavascriptInterface(, "Android");

        return view;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            return false;
        }
    });

}
