package com.aristy.gogocar;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WINavigation extends WICommon {

    public static final int SET_PAGE = 1;
    public static final int SET_PAGE_FROM_HOME = 2;

    Handler handlerNavigation;
    WebView webViewContent;
    private String newWebPage;

    public WINavigation(WebView webViewNav, WebView webViewContent, Handler handlerNavigation) {
        super(webViewNav);

        this.handlerNavigation = handlerNavigation;
        this.webViewContent = webViewContent;
    }

    // SET_PAGE
    public void setPage(){
        loadNewPage(webViewContent, newWebPage);
        // inform success
        androidToWeb("pageChanged");
    }

    // SET_PAGE_FROM_HOME
    public void setPage(String page){
        loadNewPage(webViewContent, page);
        // inform success
        androidToWeb("pageChanged", page);
    }


    @JavascriptInterface
    public void requestChangePage(String page){
        this.newWebPage = page;
        handlerNavigation.obtainMessage(SET_PAGE).sendToTarget();
    }

}
