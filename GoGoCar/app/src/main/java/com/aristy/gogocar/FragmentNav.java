package com.aristy.gogocar;


import static com.aristy.gogocar.WIMainScreen.HOME;
import static com.aristy.gogocar.WINavigation.SET_PAGE;
import static com.aristy.gogocar.WINavigation.SET_PAGE_FROM_HOME;

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

    // The fragment initialization parameters
    private static final String ARG_USER_PREF = "userPref";

    private UserPreferences userPreferences;

    WebView web;
    boolean isDriving;
    WINavigation webInterfaceWeb;

    public FragmentNav () {
        // Required empty public constructor
    }

    public static FragmentNav newInstance(UserPreferences userPreferences){
        FragmentNav fragment = new FragmentNav();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_PREF, userPreferences);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get user preferences
            this.userPreferences = getArguments().getParcelable(ARG_USER_PREF);
        }
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

        // Enable javascript and set Web Interface for Navigation
        WebSettings webSettingsNav = webNav.getSettings();
        webSettingsNav.setJavaScriptEnabled(true);
        webInterfaceWeb = new WINavigation(webNav, web, handlerNavigation);
        webNav.addJavascriptInterface(webInterfaceWeb, "Android");

        //
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.addJavascriptInterface(new WINavigation(webNav, web, handlerNavigation), "Android");

        web.addJavascriptInterface(new WIMainScreen(web, userPreferences, handlerNavigation), "Android");

        return view;
    }

    Handler handlerNavigation = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case SET_PAGE:
                    if (!isDriving) webInterfaceWeb.setPage();
                    break;
                case SET_PAGE_FROM_HOME:
                    if (!isDriving) webInterfaceWeb.setPage(String.valueOf(message.obj));
                    break;
            }
            return false;
        }
    });

}
