package com.aristy.gogocar.WebInterfaces;

import static com.aristy.gogocar.HandlerCodes.REMOVE_MODAL;
import static com.aristy.gogocar.HandlerCodes.SET_PAGE;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.JS.CHANGE_PAGE;
import static com.aristy.gogocar.WebInterfaces.WICommon.Pages.JS.SET_MODAL;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.aristy.gogocar.ThreadManager;

/**
 * Web Interface for Navigation screen
 */
public class WINavigation extends WICommon {

    Handler handlerNavigation;
    WebView webViewContent;
    private String newWebPage;
    private final String initLink;

    public WINavigation(WebView webViewNav, WebView webViewContent, Handler handlerNavigation, String initLink) {
        super(webViewNav);

        this.handlerNavigation = handlerNavigation;
        this.webViewContent = webViewContent;
        this.initLink = initLink;
    }

    @JavascriptInterface
    public void initNavigation(){
        androidToWeb(CHANGE_PAGE, getPageName(initLink));
    }

    private String getPageName(String path){
        String[] parts = path.split("/");
        String filename = parts[parts.length - 1];
        String[] nameParts = filename.split("\\.");
        return nameParts[0];
    }

    /**
     * The user want to change the page
     * @param page new page
     */
    @JavascriptInterface
    public void requestChangePage(String page){
        this.newWebPage = page;
        ThreadManager.getInstance().verifyThread();
        handlerNavigation.obtainMessage(SET_PAGE, page).sendToTarget();
    }

    /**
     * Handler response from SET_PAGE code
     * -> response from the navigation bar
     */
    public void setPage(){
        loadNewPage(webViewContent, newWebPage);
        // inform success
        androidToWeb(CHANGE_PAGE);
    }

    /**
     * Handler response from SET_PAGE_FROM_HOME code
     * -> response from the home fragment
     * @param page new page for button identification
     */
    public void setPage(String page){
        loadNewPage(webViewContent, page);
        // inform success
        androidToWeb(CHANGE_PAGE, page);
    }

    /**
     * Active modal in the view or not
     * @param isActive true or false
     */
    public void setModal(boolean isActive){
        androidToWeb(SET_MODAL, String.valueOf(isActive));
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
