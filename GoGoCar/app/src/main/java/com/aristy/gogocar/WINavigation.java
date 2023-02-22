package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG;
import static com.aristy.gogocar.HandlerCodes.REMOVE_MODAL;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Web Interface for Navigation screen
 */
public class WINavigation extends WICommon {

    Handler handlerNavigation;
    WebView webViewContent;
    private String newWebPage;

    public WINavigation(WebView webViewNav, WebView webViewContent, Handler handlerNavigation) {
        super(webViewNav);

        this.handlerNavigation = handlerNavigation;
        this.webViewContent = webViewContent;
    }

    /**
     * The user want to change the page
     * @param page new page
     */
    @JavascriptInterface
    public void requestChangePage(String page){
        this.newWebPage = page;
        handlerNavigation.obtainMessage(SET_PAGE).sendToTarget();
    }

    /**
     * Handler response from SET_PAGE code
     * -> response from the navigation bar
     */
    public void setPage(){
        loadNewPage(webViewContent, newWebPage);
        // inform success
        androidToWeb("pageChanged");
    }

    /**
     * Handler response from SET_PAGE_FROM_HOME code
     * -> response from the home fragment
     * @param page new page for button identification
     */
    public void setPage(String page){
        loadNewPage(webViewContent, page);
        // inform success
        androidToWeb("pageChanged", page);
    }

    /**
     * Active modal in the view or not
     * @param isActive true or false
     */
    public void setModal(boolean isActive){
        androidToWeb("setModal", String.valueOf(isActive));
    }

    /**
     * The user has clicked on the nav during the Modal was open<br>
     * Call remove popup on the MainScreen<br>
     * <i>Call in</i>: <code>nav.html</code><br>
     */
    @JavascriptInterface
    public void removeModal(){
        handlerNavigation.obtainMessage(REMOVE_MODAL).sendToTarget();
    }

}
