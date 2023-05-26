package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_SLIDER;
import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;
import static com.aristy.gogocar.HandlerCodes.OPEN_QRCODE_ACTIVITY;
import static com.aristy.gogocar.HandlerCodes.OPEN_SLIDER;
import static com.aristy.gogocar.WindowHelper.setWindowVersion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

public class SliderActivity extends AppCompatActivity {

    // The activity initialization parameters
    private static final String ARG_USER_PREF = "userPref";
    private static final String ARG_LINK = "webLink";
    private static final String ARG_LOCKED = "locked";
    private static final String ARG_DATA = "data";

    private UserPreferences userPreferences;
    private String link;
    private boolean locked;
    private String data;

    private WIPanels webInterfacePanel;

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     * @param mainActivity      principal activity
     * @param userPreferences   user preferences
     * @param link              web link
     * @param locked            slider locked (default false)
     * @return  A new intent of this activity.
     */
    public static Intent newInstance(Activity mainActivity, UserPreferences userPreferences, String link, boolean locked){
        Intent intent = new Intent(mainActivity, SliderActivity.class);
        intent.putExtra(ARG_USER_PREF, userPreferences);
        intent.putExtra(ARG_LINK, link);
        intent.putExtra(ARG_LOCKED, locked);
        intent.putExtra(ARG_DATA, "");
        return intent;
    }

    /**
     * Use this factory method to create a new instance of
     * this activity using the provided parameters.
     * @param mainActivity      principal activity
     * @param userPreferences   user preferences
     * @param link              web link
     * @param locked            slider locked (default false)
     * @param data              JSON data
     * @return  A new intent of this activity.
     */
    public static Intent newInstance(Activity mainActivity, UserPreferences userPreferences, String link, boolean locked, String data){
        Intent intent = new Intent(mainActivity, SliderActivity.class);
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
        webInterfacePanel = new WIPanels(web, userPreferences, handler, data);
        web.addJavascriptInterface(webInterfacePanel, "Android");

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

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.d(TAG_SLIDER, "onActivityResult:");
        String resultValue = null;
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                resultValue = data.getStringExtra(ScanQRCodeActivity.QR_CODE_VALUE);
                Log.d(TAG_SLIDER, "onActivityResult: " + resultValue);
            } else Log.e(TAG_SLIDER, "onActivityResult: data null");

        } else Log.e(TAG_SLIDER, "onActivityResult: " + result.getResultCode());

        webInterfacePanel.setResultQRCode(resultValue);
    });

    Handler handler = new Handler(message -> {
        if (message.what == CLOSE_SLIDER) {
            onBackPressed();
        } else if (message.what == OPEN_QRCODE_ACTIVITY){
            Log.d(TAG_SLIDER, "handler: open QRCODE Activity");
            Intent intent = new Intent(SliderActivity.this, ScanQRCodeActivity.class);
            resultLauncher.launch(intent);
        } else if (message.what == OPEN_SLIDER){
            Log.d(TAG_SLIDER, "handler: open another slider");
            Object [] args = (Object[]) message.obj;
            Intent intent = newInstance(SliderActivity.this, userPreferences, String.valueOf(args[0]), (boolean) args[1]);
            SliderActivity.this.startActivity(intent);
            SliderActivity.this.overridePendingTransition(
                    R.anim.animate_slide_left_enter,
                    R.anim.animate_slide_left_exit
            );
        }
        return true;
    });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animate_slide_right_enter, R.anim.animate_slide_right_exit);
    }

}
