package org.vliux.android.gesturecut.ui.floatwindow;

/**
 * Created by vliux on 4/3/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
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

    public static void registerLockerStatusReceiver(Context context){
        AppBroadcastManager.registerLockerStateChangesReceiver(context.getApplicationContext(), sLockerStatusReceiver);
    }

    public static void unregisterLockerStatusReceiver(Context context){
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(sLockerStatusReceiver);
    }

    public static void showWindow(Context context){
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

    public static void closeWindow(Context context){
        if(null != sFloatWindow){
            WindowManagerUtil.closeWindow(context.getApplicationContext(), sFloatWindow);
        }else{
            AppLog.logw(TAG, "FloatWindow instance NULL, nothing to be closed");
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
                closeWindow(context.getApplicationContext());
            }else if(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STOPPED.equals(action)){
                showWindow(context.getApplicationContext());
            }
        }
    };

}

