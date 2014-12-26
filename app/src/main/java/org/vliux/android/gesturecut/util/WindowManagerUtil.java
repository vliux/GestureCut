package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/8/14.
 */
public class WindowManagerUtil {
    private static final String TAG = WindowManagerUtil.class.getSimpleName();

    public enum WindowScope {
        FIRST_FLOAT_WND, // always shown on screen
        SECOND_FLOAT_WND // show in app lifecycle
    }

    public static WindowManager.LayoutParams dialogLayoutParams(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    public static WindowManager.LayoutParams showWindow(Context context, View view, WindowScope windowScope){
        if(null != view.getParent()){
            AppLog.loge(TAG, "view already has a parent, unable to showWindow()");
            return null;
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        lp.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        lp.format = PixelFormat.TRANSLUCENT;
        int[] screenSize = ScreenUtil.getScreenSize(context);

        switch (windowScope){
            case FIRST_FLOAT_WND:
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                lp.gravity = Gravity.LEFT | Gravity.TOP;
                setLayoutParamsLocation(context, lp, screenSize);
                break;
            case SECOND_FLOAT_WND:
                lp.width = (int)(screenSize[0] * 0.93);
                lp.height = (int)(screenSize[1] * 0.93);
                lp.gravity = Gravity.CENTER;
                lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                lp.dimAmount = 0.5f;
                break;
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
        dialogView.setVisibility(View.GONE);
        windowManager.removeView(dialogView);
    }

    public static void updateWindow(Context context, View view, WindowManager.LayoutParams layoutParams,
                                    boolean isStoreXY){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.updateViewLayout(view, layoutParams);
        if(isStoreXY){
            PreferenceHelper.setUserPref(context.getApplicationContext(),
                    R.string.pref_key_float_wnd_xy,
                    layoutParams.x + ","+layoutParams.y);
        }
    }

    private static void setLayoutParamsLocation(Context context, WindowManager.LayoutParams lp,
                                                int[] screenSize){
        int[] xyValues = parseLocationFromPrefs(
                PreferenceHelper.getUserPref(context.getApplicationContext(),
                        R.string.pref_key_float_wnd_xy,
                        null));
        if(null != xyValues){
            lp.x = xyValues[0];
            lp.y = xyValues[1];
        }else {
            lp.x = screenSize[0];
            lp.y = screenSize[1] / 2;
        }
    }

    private static int[] parseLocationFromPrefs(String value){
        if(TextUtils.isEmpty(value)){
            return null;
        }

        String[] values = value.split(",");
        if(null == values || values.length < 2){
            return null;
        }

        int[] xyValues = new int[2];
        try {
            xyValues[0] = Integer.parseInt(values[0]);
            xyValues[1] = Integer.parseInt(values[1]);
        }catch(NumberFormatException e){
            return null;
        }

        return xyValues;
    }
}
