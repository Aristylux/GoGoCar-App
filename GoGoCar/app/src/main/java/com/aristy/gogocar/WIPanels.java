package com.aristy.gogocar;

import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WIPanels extends WICommon {

    UserPreferences userPreferences;
    Handler fragmentHandler;

    public WIPanels(WebView webView, UserPreferences userPreferences, Handler fragmentHandler) {
        super(webView);

        this.userPreferences = userPreferences;
        this.fragmentHandler = fragmentHandler;
    }

    @JavascriptInterface
    public void requestClosePanel(){
        fragmentHandler.obtainMessage(CLOSE_SLIDER).sendToTarget();
    }

    // ---- Personal information container ----

    @JavascriptInterface
    public void requestPersonalInformation() {
        androidToWeb("setUserInformation", userPreferences.toString());
    }

}
