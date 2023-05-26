package com.aristy.gogocar;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * ---- WINDOW settings ----
 */
public class WindowHelper {

    /**
     * Set Window option for status bar
     * @param activity  current activity
     * @param window    current window
     */
    public static void setWindowVersion(Activity activity, Window window){
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * Set parameter
     * @param activity  current activity
     * @param bits      parameter
     */
    public static void setWindowFlag(Activity activity, final int bits) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~bits;
        win.setAttributes(winParams);
    }

}
