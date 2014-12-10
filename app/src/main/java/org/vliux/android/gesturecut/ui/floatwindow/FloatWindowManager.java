package org.vliux.android.gesturecut.ui.floatwindow;

/**
 * Created by vliux on 4/3/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.ui.view.satellite.SatelliteMenu;
import org.vliux.android.gesturecut.ui.view.satellite.SatelliteMenuItem;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vliux on 10/15/13.
 * All methods should only be called in main thread,
 * as they maintain an instance of FloatWindow.
 */
public class FloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getSimpleName();
    private static SatelliteMenu sFloatWindow;

    /**
     * Show or hide the float window.
     * If there is no instance yet, will call addWindow() then.
     * @param context
     * @param isShow
     */
    public static void toggleWindow(Context context, boolean isShow){
        if(null != sFloatWindow){
            sFloatWindow.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }else{
            if(isShow){
                AppLog.logw(TAG, "FloatWindow instance is NULL, will call addWindow()");
                addWindow(context);
            }else{
                AppLog.logw(TAG, "FloatWindow instance is NULL, ignore toggleWindow(false)");
            }
        }
    }

    private static void addWindow(Context context){
        if(null == sFloatWindow){
            synchronized (FloatWindowManager.class){
                if (null == sFloatWindow) {
                    sFloatWindow = new SatelliteMenu(context.getApplicationContext());
                    List<SatelliteMenuItem> menuItemList = new ArrayList<SatelliteMenuItem>();
                    menuItemList.add(new SatelliteMenuItem(1, R.drawable.ic_add));
                    menuItemList.add(new SatelliteMenuItem(2, R.drawable.ic_settings));
                    sFloatWindow.addItems(menuItemList);
                }
            }
        }

        WindowManager.LayoutParams lp =
                    WindowManagerUtil.showWindow(context.getApplicationContext(), sFloatWindow, WindowManagerUtil.WindowScope.FIRST_FLOAT_WND);
        if(null != lp){
            //sFloatWindow.setWindowLayoutParams(lp);
        }
    }

    public static void removeWindow(Context context){
        if(null != sFloatWindow){
            WindowManagerUtil.closeWindow(context.getApplicationContext(), sFloatWindow);
        }else{
            AppLog.logw(TAG, "FloatWindow instance is NULL, nothing to be closed");
        }
    }

}

