package com.aristy.gogocar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class SliderActivity extends AppCompatActivity {

    public static final String ARG_LINK = "link";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        String link = getIntent().getStringExtra(ARG_LINK);

        // Set web content
        WebView web = findViewById(R.id.web_view);

        web.loadUrl(link);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new FragmentApp.Callback());

        //web.addJavascriptInterface(new WebInterface(getActivity(), getContext(), web, SQLConnection, userPreferences, handlers), "Android");


        // Set slider
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .scrimColor(Color.TRANSPARENT)
                .scrimStartAlpha(0f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(false)
                .build();

        Slidr.attach(this, config);
    }
}
