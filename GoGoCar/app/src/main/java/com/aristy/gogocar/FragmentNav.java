package com.aristy.gogocar;


import static com.aristy.gogocar.WINavigation.SET_PAGE;
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


public class FragmentNav extends Fragment {


    WebView web;
    boolean isDriving;
    WINavigation webInterfaceWeb;

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

        isDriving = false;
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
        webInterfaceWeb = new WINavigation(webNav, web, handlerNavigation);
        webNav.addJavascriptInterface(webInterfaceWeb, "Android");

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //web.addJavascriptInterface(, "Android");

        return view;
    }

    Handler handlerNavigation = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == SET_PAGE) {
                if (!isDriving)
                    webInterfaceWeb.setPage();
            }
            return false;
        }
    });

}
