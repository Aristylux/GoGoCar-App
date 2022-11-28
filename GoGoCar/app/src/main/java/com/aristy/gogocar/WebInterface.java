package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class WebInterface {

    Activity activity;
    Context context;
    WebView webView;

    // Constructor
    WebInterface(Activity activity, Context context, WebView webView){
        this.activity = activity;
        this.context = context;
        this.webView = webView;
    }

    /** ----------------------------- *
     *  -- Methods call by webPage -- *
     *  ----------------------------- */

    /* Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void sendData(String action){
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();

        dataReceived();
    }

    @JavascriptInterface
    public void changeBackground(String webColor){
        Log.d(TAG_Web, "changeBackground : " + webColor);

        HexColor hexColor = new HexColor(webColor);
        hexColor.convertToAndroidColor();
        long colorSigned = hexColor.getDecSigned();
        //String colorAndroid = convertWebColor(webColor);²
        //long colorSigned = hexToSignedDec(colorAndroid);

        Window window = activity.getWindow();
        Log.d(TAG_Web, "changeBackground : window" + window);

        // Finally change the color
        //window.setStatusBarColor(ContextCompat.getColor(context, R.color.my_statusbar_color));
        window.setStatusBarColor((int) colorSigned);

        Log.d(TAG_Web, "changeBackground : color: " + ContextCompat.getColor(context, R.color.my_statusbar_color));
        Log.d(TAG_Web, "changeBackground : color: " + Integer.toHexString(ContextCompat.getColor(context, R.color.my_statusbar_color)));
        Log.d(TAG_Web, "changeBackground : color: " + ContextCompat.getColor(context, R.color.white));
        Log.d(TAG_Web, "changeBackground : color: " + Integer.toHexString(ContextCompat.getColor(context, R.color.white)));
        Log.d(TAG_Web, "changeBackground : color: " + "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.my_statusbar_color)));
    }

    @JavascriptInterface
    public void openPopupBook(int _id_vehicle){
        Log.d(TAG_Web, "id vehicle: " + _id_vehicle);
        if(_id_vehicle == 1)
            androidToWeb("openPopupBook", "Nissan GT", "23 rue Charles Leclec");
        else
            androidToWeb("openPopupBook", "Peugeot 206", "Boulevard Michou");
    }


    /** ---------------------------------- *
     *  -- Methods send data to webPage -- *
     *  ---------------------------------- */

    public void dataReceived(){
        androidToWeb("dataReceived", "red");
    }

    /** ---------------- *
     *  -- Interfaces -- *
     *  ---------------- */
    private void androidToWeb(String function){
        androidToWeb(function, "");
    }

    private void androidToWeb(String function, String data){
        webView.post(() -> webView.loadUrl("javascript:" + function + "('" + data + "')"));     //webView.loadUrl("javascript:dataReceived('red')");
    }

    private void androidToWeb(String function, String data1, String data2){
        webView.post(() -> webView.loadUrl("javascript:" + function + "('" + data1 + "','" + data2 + "')"));     //webView.loadUrl("javascript:dataReceived('red')");
    }

}
