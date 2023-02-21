package com.aristy.gogocar;

import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;
import static com.aristy.gogocar.WindowHelper.setWindowVersion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class SliderActivity extends AppCompatActivity {

    // The activity initialization parameters
    public static final String ARG_LINK = "webLink";
    public static final String ARG_USER_PREF = "userPref";
    public static final String ARG_MESSENGER_HANDLER = "messenger";

    String link;
    UserPreferences userPreferences;
    Messenger messenger;

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     *
     * @param mainActivity principal activity
     * @param fragmentHandler handler
     * @param userPreferences user preferences
     * @param link web link
     * @return A new intent of this activity.
     */
    public static Intent newInstance(Activity mainActivity, Handler fragmentHandler, UserPreferences userPreferences, String link){
        Intent intent = new Intent(mainActivity, SliderActivity.class);
        Messenger messenger = new Messenger(fragmentHandler);
        intent.putExtra(ARG_MESSENGER_HANDLER, messenger);
        intent.putExtra(ARG_USER_PREF, userPreferences);
        intent.putExtra(ARG_LINK, link);
        return intent;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);


        if (getIntent() != null) {
            // communication between activity (not used for the moment)
            this.messenger = getIntent().getParcelableExtra(ARG_MESSENGER_HANDLER);
            this.userPreferences = getIntent().getParcelableExtra(ARG_USER_PREF);
            this.link = getIntent().getStringExtra(ARG_LINK);
        }

        // Set web content
        WebView web = findViewById(R.id.web_view);

        web.loadUrl(link);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        web.setWebViewClient(new FragmentApp.Callback());

        web.addJavascriptInterface(new WIPanels(web, userPreferences, handler), "Android");

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

        Slidr.attach(SliderActivity.this, config);

        setWindowVersion(SliderActivity.this, getWindow());
    }

    Handler handler = new Handler(message -> {
        if (message.what == CLOSE_SLIDER) {
            onBackPressed();
        }
        return true;
    });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animate_slide_right_enter, R.anim.animate_slide_right_exit);
    }
}