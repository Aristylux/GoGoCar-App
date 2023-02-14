package com.aristy.gogocar;

import android.webkit.WebView;

/**
 * Web Interface Common regroup all common methods for Web Interfaces
 */
public class WICommon {
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
}
