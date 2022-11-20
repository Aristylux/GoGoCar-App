package com.aristy.gogocar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1100);
    }
}