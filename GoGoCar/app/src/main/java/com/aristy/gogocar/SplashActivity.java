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

        Log.d(TAG_SPLASH, "SPLASH: START");

        // Create Intent
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        // After 1.1 second, Launch app
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            overridePendingTransition(R.anim.to_left, R.anim.from_right);
            startActivity(intent);
            finish();
        }, 1100);

        // Operations
        UserPreferences userPreferences = new UserPreferences(SplashActivity.this);

        // Retrieve user from data in app
        userPreferences.readUser();

        // Get user id (by default (unset) int=0, first element in database by default: 1)
        int userID = userPreferences.getUserID();
        boolean isLogged = userID != 0;

        // Send user
        intent.putExtra("IS_USER_LOGGED", isLogged);
        intent.putExtra("USER", userPreferences);

        Log.d(TAG_SPLASH, "SPLASH: END");
    }
}