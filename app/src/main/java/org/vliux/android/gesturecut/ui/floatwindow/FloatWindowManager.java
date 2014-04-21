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
import org.vliux.android.gesturecut.util.WindowManagerUtil;

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
            WindowManager.LayoutParams lp =
                    WindowManagerUtil.showWindow(context.getApplicationContext(), sFloatWindow, WindowManagerUtil.WindowScope.GLOBAL);
            sFloatWindow.setWindowLayoutParams(lp);
        }else{
            AppLog.logw(TAG, "FloatWindow instance exist, it should already been shown");
        }
    }

    public static void closeWindow(Context context){
        if(null != sFloatWindow){
            WindowManagerUtil.closeWindow(context.getApplicationContext(), sFloatWindow);
            sFloatWindow = null;
        }else{
            AppLog.logw(TAG, "FloatWindow instance NULL, nothing to be closed");
        }
    }

}

