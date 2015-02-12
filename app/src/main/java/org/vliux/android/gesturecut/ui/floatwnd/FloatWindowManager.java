package org.vliux.android.gesturecut.ui.floatwnd;

/**
 * Created by vliux on 4/3/14.
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.floatwnd.shortcut.ShortcutWindow;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.PreferenceHelper;
import org.vliux.android.gesturecut.util.ScreenUtil;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 10/15/13.
 * All methods should only be called in main thread,
 * as they maintain an instance of FloatWindow.
 */
public class FloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getSimpleName();

    /*public static void registerLockerStatusReceiver(Context context){
        AppBroadcastManager.registerLockerStateChangesReceiver(context.getApplicationContext(), sLockerStatusReceiver);
    }

    public static void unregisterLockerStatusReceiver(Context context){
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(sLockerStatusReceiver);
    }*/

    public static void showFloatWindow(Context context, FloatWindow floatWindow){
        if(null != floatWindow.getParent()){
            AppLog.loge(TAG, "FloatWindow instance has already owned a parent, skip showFloatWindow()");
            return;
        }

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = getFloatWindowLayoutParams(context);
        windowManager.addView(floatWindow, lp);
        floatWindow.setWindowLayoutParams(lp);

    }

    public static void showSecondaryFloatWindow(Context context, ShortcutWindow shortcutWindow){
        if(null != shortcutWindow.getParent()){
            AppLog.loge(TAG, "SecondaryFloatWindow instance has already owned a parent, skip showSecondaryFloatWindow()");
            return;
        }

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(shortcutWindow, getSecondFloatWindowLayoutParmas(context));
    }

    public static void closeWindow(Context context, View view){
        WindowManagerUtil.closeWindow(context, view);
    }

    public static void updateFloatWindow(Context context, FloatWindow floatWindow, WindowManager.LayoutParams layoutParams,
                                    boolean isStoreXY){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.updateViewLayout(floatWindow, layoutParams);
        if(isStoreXY){
            saveLocationToPrefs(context,
                    layoutParams.x,
                    layoutParams.y);
        }
    }

    private static WindowManager.LayoutParams getFloatWindowLayoutParams(Context context){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        lp.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        lp.format = PixelFormat.TRANSLUCENT;
        int[] screenSize = ScreenUtil.getScreenSize(context);

        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        setLayoutParamsLocation(context, lp, screenSize);
        return lp;
    }

    private static WindowManager.LayoutParams getSecondFloatWindowLayoutParmas(Context context){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        lp.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        lp.format = PixelFormat.TRANSLUCENT;
        int[] screenSize = ScreenUtil.getScreenSize(context);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.7f;

        return lp;
    }

    private static void setLayoutParamsLocation(Context context, WindowManager.LayoutParams lp,
                                                int[] screenSize){
        int[] xyValues = parseLocationFromPrefs(context);
        if(null != xyValues){
            lp.x = xyValues[0];
            lp.y = xyValues[1];
        }else {
            lp.x = screenSize[0];
            lp.y = screenSize[1] / 2;
            saveLocationToPrefs(context, lp.x, lp.y);
        }
    }

    private static void saveLocationToPrefs(Context context, int x, int y){
        PreferenceHelper.setUserPref(context.getApplicationContext(),
                R.string.pref_key_float_wnd_xy,
                x + "," + y);
    }

    public static int[] parseLocationFromPrefs(Context context){
        String value = PreferenceHelper.getUserPref(context.getApplicationContext(),
                R.string.pref_key_float_wnd_xy, null);

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

    /*private final static BroadcastReceiver sLockerStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null == intent){
                return;
            }

            String action = intent.getAction();
            if(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STARTED.equals(action)){
                toggleWindow(context.getApplicationContext(), false);
            }else if(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STOPPED.equals(action)){
                toggleWindow(context.getApplicationContext(), true);
            }
        }
    };*/

}

