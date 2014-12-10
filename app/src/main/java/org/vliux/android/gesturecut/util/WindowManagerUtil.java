package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by vliux on 4/8/14.
 */
public class WindowManagerUtil {
    private static final String TAG = WindowManagerUtil.class.getSimpleName();

    public static WindowManager.LayoutParams showWindow(Context context, View view, WindowManager.LayoutParams lp){
        if(null != view.getParent()){
            AppLog.loge(TAG, "view already has a parent, unable to showWindow()");
            return null;
        }

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        view.setVisibility(View.VISIBLE);
        windowManager.addView(view, lp);
        return lp;
    }

    public static void closeWindow(Context context, View dialogView){
        if(null == dialogView.getParent()){
            AppLog.loge(TAG, "view doesn't have any parent yet, unable to removeWindow()");
            return;
        }

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(dialogView);
    }

    public static void updateWindow(Context context, View view, WindowManager.LayoutParams layoutParams){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.updateViewLayout(view, layoutParams);
    }

}
