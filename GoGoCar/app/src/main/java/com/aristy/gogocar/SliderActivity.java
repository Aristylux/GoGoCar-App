package com.aristy.gogocar;

import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;
import static com.aristy.gogocar.HandlerCodes.QUERY;
import static com.aristy.gogocar.WindowHelper.setWindowVersion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

public class SliderActivity extends AppCompatActivity {

    // The activity initialization parameters
    private static final String ARG_MESSENGER_HANDLER = "messenger";
    private static final String ARG_USER_PREF = "userPref";
    private static final String ARG_LINK = "webLink";
    private static final String ARG_LOCKED = "locked";
    private static final String ARG_DATA = "data";

    Messenger messenger;
    private UserPreferences userPreferences;
    private String link;
    private boolean locked;
    private String data;

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
    public static Intent newInstance(Activity mainActivity, Handler fragmentHandler, UserPreferences userPreferences, String link, boolean locked){
        Intent intent = new Intent(mainActivity, SliderActivity.class);
        Messenger messenger = new Messenger(fragmentHandler);
        intent.putExtra(ARG_MESSENGER_HANDLER, messenger);
        intent.putExtra(ARG_USER_PREF, userPreferences);
        intent.putExtra(ARG_LINK, link);
        intent.putExtra(ARG_LOCKED, locked);
        intent.putExtra(ARG_DATA, "");
        return intent;
    }

    public static Intent newInstance(Activity mainActivity, Handler fragmentHandler, UserPreferences userPreferences, String link, boolean locked, String data){
        Intent intent = new Intent(mainActivity, SliderActivity.class);
        Messenger messenger = new Messenger(fragmentHandler);
        intent.putExtra(ARG_MESSENGER_HANDLER, messenger);
        intent.putExtra(ARG_USER_PREF, userPreferences);
        intent.putExtra(ARG_LINK, link);
        intent.putExtra(ARG_LOCKED, locked);
        intent.putExtra(ARG_DATA, data);
        return intent;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);


        if (getIntent() != null) {
            // messenger: communication between activity (not used for the moment)
            this.messenger = getIntent().getParcelableExtra(ARG_MESSENGER_HANDLER);
            this.userPreferences = getIntent().getParcelableExtra(ARG_USER_PREF);
            this.link = getIntent().getStringExtra(ARG_LINK);
            this.locked = getIntent().getBooleanExtra(ARG_LOCKED, false);
            this.data = getIntent().getStringExtra(ARG_DATA);
        }

        // Set web content
        WebView web = findViewById(R.id.web_view);
        web.loadUrl(link);

        // Enable javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Result state page
        //web.setWebViewClient(new FragmentApp.Callback());
        web.addJavascriptInterface(new WIPanels(web, userPreferences, handler, data), "Android");

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

        SlidrInterface slidrInterface = Slidr.attach(SliderActivity.this, config);
        // unlocked by default
        if (this.locked) slidrInterface.lock();

        setWindowVersion(SliderActivity.this, getWindow());
    }

    Handler handler = new Handler(message -> {
        if (message.what == CLOSE_SLIDER) {
            onBackPressed();
        } else if (message.what == QUERY){
            Log.d("GoGoCar_Slider", "handler: query");
            requestDatabase((int) message.obj);
        }
        return true;
    });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animate_slide_right_enter, R.anim.animate_slide_right_exit);
    }

    private boolean requestDatabase(int queryCode){
        Message message = Message.obtain();
        message.replyTo = messenger;
        message.what = queryCode;

        try {
            messenger.send(message);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
