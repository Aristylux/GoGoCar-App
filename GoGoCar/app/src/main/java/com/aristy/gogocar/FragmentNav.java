package com.aristy.gogocar;


import static com.aristy.gogocar.CodesTAG.TAG_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.REMOVE_MODAL;
import static com.aristy.gogocar.HandlerCodes.SET_DRIVING;
import static com.aristy.gogocar.HandlerCodes.SET_MODAL;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE_FROM_HOME;
import static com.aristy.gogocar.WebInterface.DRIVE;
import static com.aristy.gogocar.WebInterface.VEHICLE;
import static com.aristy.gogocar.WebInterface.path;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    private static final String ARG_FRG_HANDLER = "FRGHandler";
    private static final String ARG_BLE_HANDLER = "BLEHandler";
    private static final String ARG_LINK = "webLink";

    private UserPreferences userPreferences;
    private Handler[] handlers;
    private String link;

    WebView web;
    boolean isDriving;
    WINavigation webInterfaceWeb;
    WIMainScreen webInterfaceMS;

    public FragmentNav () {
        // Required empty public constructor
    }

    public static FragmentNav newInstance(UserPreferences userPreferences, Handler fragmentHandler, Handler bluetoothHandler, String webLink){
        FragmentNav fragment = new FragmentNav();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_PREF, userPreferences);
        args.putSerializable(ARG_FRG_HANDLER, new HandlerWrapper(fragmentHandler));
        args.putSerializable(ARG_BLE_HANDLER, new HandlerWrapper(bluetoothHandler));
        args.putString(ARG_LINK, webLink);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get user preferences
            this.userPreferences = getArguments().getParcelable(ARG_USER_PREF);

            // Get handler for fragments
            HandlerWrapper handlerWrapperFRG = (HandlerWrapper) getArguments().getSerializable(ARG_FRG_HANDLER);
            Handler fragmentHandler = handlerWrapperFRG.getHandler();

            // Get handler for bluetooth
            HandlerWrapper handlerWrapperBLE = (HandlerWrapper) getArguments().getSerializable(ARG_BLE_HANDLER);
            Handler bluetoothHandler = handlerWrapperBLE.getHandler();

            this.handlers = new Handler[]{fragmentHandler, bluetoothHandler, navigationHandler};

            // Get web link
            this.link = getArguments().getString(ARG_LINK);
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
        web.loadUrl(link);

        // Enable javascript and set Web Interface for Navigation
        WebSettings webSettingsNav = webNav.getSettings();
        webSettingsNav.setJavaScriptEnabled(true);
        webInterfaceWeb = new WINavigation(webNav, web, navigationHandler, link);
        webNav.addJavascriptInterface(webInterfaceWeb, "Android");

        //
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webInterfaceMS = new WIMainScreen(web, getContext(), getActivity(), userPreferences, handlers);
        web.addJavascriptInterface(webInterfaceMS, "Android");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // If on fragment drive, refresh the data.
        if (link.equals(DRIVE) || link.equals(VEHICLE)){
            Log.d(TAG_FRAGMENT, "onResume: Refresh");
            webInterfaceMS.refresh();
        }
    }

    Handler navigationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case SET_PAGE:
                    if (!isDriving) {
                        link = path + message.obj + ".html";
                        webInterfaceWeb.setPage();
                    }
                    break;
                case SET_PAGE_FROM_HOME:
                    if (!isDriving) webInterfaceWeb.setPage(String.valueOf(message.obj));
                    break;
                case SET_DRIVING:
                    isDriving = (boolean) message.obj;
                    break;
                case SET_MODAL:
                    webInterfaceWeb.setModal((boolean) message.obj);
                    break;
                case REMOVE_MODAL:
                    webInterfaceMS.removeModal();
                    break;
            }
            return false;
        }
    });

}
