package com.aristy.gogocar;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WINavigation extends WICommon {

    public static final int SET_PAGE = 1;

    Handler handlerNavigation;
    WebView webViewContent;
    private String newWebPage;

    public WINavigation(WebView webViewNav, WebView webViewContent, Handler handlerNavigation) {
        super(webViewNav);

        this.handlerNavigation = handlerNavigation;
        this.webViewContent = webViewContent;
    }

    public void setPage(){
        loadNewPage(webViewContent, newWebPage);
        // inform success
    }


    @JavascriptInterface
    public void requestChangePage(String page){
        this.newWebPage = page;
        handlerNavigation.obtainMessage(SET_PAGE).sendToTarget();
    }

}
