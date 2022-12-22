package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Auth;
import static com.aristy.gogocar.CodesTAG.TAG_Database;
import static com.aristy.gogocar.CodesTAG.TAG_Debug;
import static com.aristy.gogocar.HandlerCodes.GOTO_HOME_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.GOTO_LOGIN_FRAGMENT;
import static com.aristy.gogocar.HandlerCodes.STATUS_BAR_COLOR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    ConnectionHelper connectionHelper;
    Connection SQLConnection;

    UserPreferences userPreferences;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to database (do it in other thread)
        connectionHelper = new ConnectionHelper();
        SQLConnection = connectionHelper.openConnection();

        // Get user id (by default (unset) int=0, first element in database by default: 1)
        UserSharedPreference userdata = new UserSharedPreference(this);
        int userID = userdata.readUserID();
        Log.d(TAG_Auth, "userID: " + userID);

        userPreferences = new UserPreferences();

        Fragment selectedFragment;
        // If user if is equal to 0, the user is not logged
        if(userID == 0) {
            selectedFragment = new FragmentLogin(SQLConnection, userPreferences, fragmentHandler);
        } else {
            selectedFragment = new FragmentApp(SQLConnection, userPreferences, fragmentHandler);

            // Retrieve user from data in app
            DBModelUser user = userdata.readUser();
            userPreferences.setUser(user);
        }
        
        setFragment(selectedFragment, R.anim.from_left, R.anim.to_right);

        // For top bar and navigation bar
        setWindowVersion();
    }

    /*
    // First time, appear after onCreate
    @Override
    protected void onStart() {
        super.onStart();
        // If the connection to the server is close, open it
        if (!connectionValid()) {
            Log.d(TAG_Database, "onStart: open SQL Connection");
            SQLConnection = connectionHelper.openConnection();
        } else {
            Log.e(TAG_Database, "onStart: ERROR open SQL Connection: invalid");
        }
    }
    */
    public boolean connectionValid(){
        try {
            Log.d(TAG_Database, "connectionValid: SQLConnection=" + SQLConnection + ", close?=" + SQLConnection.isClosed());
            if (SQLConnection != null)
                return !SQLConnection.isClosed();
            else
                return false;
        } catch (SQLException exception) {
            Log.e(TAG_Database, "connectValid: ", exception);
            exception.printStackTrace();
            return false;
        }
    }
/*
    // Quit app without kill process
    @Override
    protected void onStop() {
        super.onStop();
        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
    }
*/
    @Override
    protected void onDestroy() {

        try {
            // The user leave application, close connection to the server.
            if (connectionValid()) {
                Log.d(TAG_Debug, "onStop: close SQL connection");
                SQLConnection.close();
            } else {
                Log.e(TAG_Debug, "onStop: ERROR close SQL connection: invalid");
            }
        } catch (SQLException exception) {
            Log.e(TAG_Debug, "onStop: ERROR close SQL connection: ", exception);
            exception.printStackTrace();
        }
        super.onDestroy();
    }



    public void setWindowVersion(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void setWindowFlag(Activity activity, final int bits) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~bits;
        win.setAttributes(winParams);
    }

    public void setFragment(Fragment fragment, int anim_enter, int anim_exit){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_enter, anim_exit);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    Handler fragmentHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case GOTO_HOME_FRAGMENT:
                    setFragment(new FragmentApp(SQLConnection, userPreferences, fragmentHandler), R.anim.from_right, R.anim.to_left);
                    break;
                case GOTO_LOGIN_FRAGMENT:
                    setFragment(new FragmentLogin(SQLConnection, userPreferences, fragmentHandler), R.anim.from_left, R.anim.to_right);
                    break;
                case STATUS_BAR_COLOR:
                    // Set color background
                    getWindow().setStatusBarColor((Integer) message.obj);
                    break;
            }
            return true;
        }
    });
}