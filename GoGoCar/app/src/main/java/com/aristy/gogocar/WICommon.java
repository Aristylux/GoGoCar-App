package com.aristy.gogocar;

import android.webkit.WebView;

/**
 * Web Interface Common regroup all common methods for Web Interfaces
 */
public class WICommon {

    private static final String path = "file:///android_asset/";
    public static final String LOGIN = path + "login.html";

    private final WebView webView;

    public WICommon (WebView webView){
        this.webView = webView;
    }

    /**
     * Send data to web
     * @param functionName javascript function name
     * @param data data in string
     */
    public void androidToWeb(String functionName, String... data){
        StringBuilder builder = new StringBuilder();
        if(data.length != 0) {
            builder.append(data[0]);
            for (int i = 1; i < data.length ; i++){
                builder.append("','");
                builder.append(data[i]);
            }
        }
        webView.post(() -> webView.loadUrl("javascript:" + functionName + "('" + builder + "')"));
    }

    /**
     * Load a new page
     * @param webView custom webView
     * @param page page name to load
     */
    public void loadNewPage(WebView webView, String page){
        webView.post(() -> webView.loadUrl("file:///android_asset/pages/" + page + ".html"));
    }

}
