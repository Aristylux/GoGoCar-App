package com.aristy.gogocar;

import static com.aristy.gogocar.WINavigation.SET_PAGE_FROM_HOME;

import android.os.Handler;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;

/**
 * Web Interface for Screen (home, vehicles, drive, settings)
 */
public class WIMainScreen extends WICommon {

    private static final String path = "file:///android_asset/pages/";
    public static final String HOME = path + "home.html";

    UserPreferences userPreferences;
    Handler handlerNavigation;

    public WIMainScreen(WebView webView, UserPreferences userPreferences, Handler handlerNavigation) {
        super(webView);

        this.userPreferences = userPreferences;
        this.handlerNavigation = handlerNavigation;
    }


    /*  ---------------------------------- *
     *  --           home.html          -- *
     *  ---------------------------------- */

    /**
     * [LOADER METHOD]<br>
     * Request data:<br>
     * - Name of the actual user <br>
     * - Get vehicles booked by the user
     */
    @JavascriptInterface
    public void requestData(){
        androidToWeb("setUserName", userPreferences.getUserName());

        // TODO 'Get vehicles booked by the user'
    }

    /**
     * Request to change the page from home.html
     * @param page new page to load ('drive' or 'vehicle')
     */
    @JavascriptInterface
    public void requestChangePage(String page){
        handlerNavigation.obtainMessage(SET_PAGE_FROM_HOME, page).sendToTarget();
    }

}
