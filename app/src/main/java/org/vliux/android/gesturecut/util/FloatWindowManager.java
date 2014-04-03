package org.vliux.android.gesturecut.util;

/**
 * Created by vliux on 4/3/14.
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.AppConstant;

/**
 * Created by vliux on 10/15/13.
 */
public class FloatWindowManager {

    public static void showWindow(Context activityContext, View view){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0, PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER;
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

