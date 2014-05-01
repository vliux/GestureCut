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
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 10/15/13.
 * All methods should only be called in main thread,
 * as they maintain an instance of FloatWindow.
 */
public class FloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getSimpleName();
    private static FloatWindow sFloatWindow;

    public static void registerLockerStatusReceiver(Context context){
        AppBroadcastManager.registerLockerStateChangesReceiver(context.getApplicationContext(), sLockerStatusReceiver);
    }

    public static void unregisterLockerStatusReceiver(Context context){
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(sLockerStatusReceiver);
    }

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
                    sFloatWindow = new FloatWindow(context.getApplicationContext());
                }
            }
        }

        WindowManager.LayoutParams lp =
                    WindowManagerUtil.showWindow(context.getApplicationContext(), sFloatWindow, WindowManagerUtil.WindowScope.GLOBAL);
        if(null != lp){
            sFloatWindow.setWindowLayoutParams(lp);
        }
    }

    public static void removeWindow(Context context){
        if(null != sFloatWindow){
            WindowManagerUtil.closeWindow(context.getApplicationContext(), sFloatWindow);
        }else{
            AppLog.logw(TAG, "FloatWindow instance is NULL, nothing to be closed");
        }
    }

    private static BroadcastReceiver sLockerStatusReceiver = new BroadcastReceiver() {
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
    };

}

