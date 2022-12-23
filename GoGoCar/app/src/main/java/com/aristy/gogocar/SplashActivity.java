package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_SPLASH;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Connection;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG_SPLASH, "onCreate: splash");
        Intent intent =new Intent(SplashActivity.this, MainActivity.class);

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent);
            finish();
        }, 1100);

        // Operations
        // Connect to database (do it in other thread)
        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection SQLConnection = connectionHelper.openConnection();

        UserSharedPreference userdata = new UserSharedPreference(SplashActivity.this);
        int userID = userdata.readUserID();
        intent.putExtra("USER_ID", userID);
        intent.putExtra("CONNECTION", (Parcelable) SQLConnection);

        //https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents


    }
}