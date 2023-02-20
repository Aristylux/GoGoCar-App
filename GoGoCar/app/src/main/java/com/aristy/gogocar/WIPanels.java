package com.aristy.gogocar;

import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WIPanels extends WICommon {

    UserPreferences userPreferences;
    Handler handler;

    public WIPanels(WebView webView, UserPreferences userPreferences, Handler handler) {
        super(webView);

        this.userPreferences = userPreferences;
        this.handler = handler;
    }

    @JavascriptInterface
    public void requestClosePanel(){
        /*
        Message message = Message.obtain();
        message.replyTo = messenger;
        message.what = CLOSE_SLIDER;

        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        handler.obtainMessage(CLOSE_SLIDER).sendToTarget();
    }

    // ---- Personal information container ----

    @JavascriptInterface
    public void requestPersonalInformation() {
        androidToWeb("setUserInformation", userPreferences.toString());
    }

}
