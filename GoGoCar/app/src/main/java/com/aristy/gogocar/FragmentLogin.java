package com.aristy.gogocar;

import static com.aristy.gogocar.WIAuthentication.LOGIN;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

    // The fragment initialization parameters
    private static final String ARG_USER_PREF = "userPref";
    private static final String ARG_FRG_HANDLER = "FRGHandler";

    Connection SQLConnection;

    private UserPreferences userPreferences;
    private Handler fragmentHandler;

    public FragmentLogin(Connection SQLConnection){
        // Required empty public constructor
        this.SQLConnection = SQLConnection;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userPreferences user preferences
     * @param fragmentHandler fragment handler
     * @param SQLConnection sql connection
     * @return A new instance of fragment FragmentLogin.
     */
    public static FragmentLogin newInstance(UserPreferences userPreferences, Handler fragmentHandler, Connection SQLConnection){
        FragmentLogin fragment = new FragmentLogin(SQLConnection);
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRG_HANDLER, new HandlerWrapper(fragmentHandler));
        args.putParcelable(ARG_USER_PREF, userPreferences);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Get arguments
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get handler
            HandlerWrapper handlerWrapper = (HandlerWrapper) getArguments().getSerializable(ARG_FRG_HANDLER);
            this.fragmentHandler = handlerWrapper.getHandler();

            // Get user preferences
            this.userPreferences = getArguments().getParcelable(ARG_USER_PREF);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Find items
        WebView web = view.findViewById(R.id.web_view);
        web.loadUrl(LOGIN);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.addJavascriptInterface(new WIAuthentication(getContext(), web, SQLConnection, userPreferences, fragmentHandler), "Android");

        return view;
    }
}