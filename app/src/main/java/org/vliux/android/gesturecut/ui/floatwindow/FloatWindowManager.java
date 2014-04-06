package org.vliux.android.gesturecut.ui.floatwindow;

/**
 * Created by vliux on 4/3/14.
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 10/15/13.
 * showWindow() and closeWindow() should only be called in main thread,
 * as they maintain an instance of FloatWindow.
 */
public class FloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getSimpleName();
    private static FloatWindow sFloatWindow;

    public static void showWindow(Context context){
        if(null == sFloatWindow){
            if (null == sFloatWindow) {
                sFloatWindow = new FloatWindow(context.getApplicationContext());
            }
            showWindow(context.getApplicationContext(), sFloatWindow);
        }else{
            AppLog.logw(TAG, "FloatWindow instance exist, it should already been shown");
        }
    }

    public static void closeWindow(Context context){
        if(null != sFloatWindow){
            closeWindow(context.getApplicationContext(), sFloatWindow);
            sFloatWindow = null;
        }else{
            AppLog.logw(TAG, "FloatWindow instance NULL, nothing to be closed");
        }
    }

    private static void showWindow(Context activityContext, View view){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.RIGHT|Gravity.TOP;
        //lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        WindowManager windowManager = (WindowManager)activityContext.getSystemService(Context.WINDOW_SERVICE);
        view.setVisibility(View.VISIBLE);
        windowManager.addView(view, lp);
    }

    private static void closeWindow(Context activityContext, View dialogView){
        WindowManager windowManager = (WindowManager)activityContext.getSystemService(Context.WINDOW_SERVICE);
        dialogView.setVisibility(View.GONE);
        windowManager.removeView(dialogView);
    }

}

