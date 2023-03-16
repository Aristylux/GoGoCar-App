package com.aristy.gogocar;

import static com.aristy.gogocar.HandlerCodes.CLOSE_SLIDER;
import static com.aristy.gogocar.HandlerCodes.DATA_SET_VEHICLE;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WIPanels extends WICommon {

    UserPreferences userPreferences;
    Handler handler;
    String data;

    public WIPanels(WebView webView, UserPreferences userPreferences, Handler handler, String data) {
        super(webView);

        this.userPreferences = userPreferences;
        this.handler = handler;
        this.data = data;
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

    // ---- Add ----

    /**
     * Send vehicle (in main) to the new fragment
     */
    @JavascriptInterface
    public void requestGetVehicle(){
        Log.d("GoGoCar_T", "requestGetVehicle: get vehicle:" + data);
        androidToWeb("setVehicle", data);
        //fragmentHandler.obtainMessage(DATA_SET_VEHICLE).sendToTarget();
    }

}
