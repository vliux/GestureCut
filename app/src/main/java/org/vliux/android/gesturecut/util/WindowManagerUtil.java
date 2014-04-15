package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by vliux on 4/8/14.
 */
public class WindowManagerUtil {

    public enum WindowScope {
        GLOBAL, // always shown on screen
        APP // show in app lifecycle
    }

    public static void showWindow(Context context, View view, WindowScope windowScope){
        WindowManager.LayoutParams lp = null;
        switch (windowScope){
            case GLOBAL:
                lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT);
                lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
                break;
            case APP:
                lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        PixelFormat.OPAQUE);
                lp.gravity = Gravity.CENTER;
                break;
        }
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        view.setVisibility(View.VISIBLE);
        windowManager.addView(view, lp);
    }

    public static void closeWindow(Context context, View dialogView){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        dialogView.setVisibility(View.GONE);
        windowManager.removeView(dialogView);
    }
}
