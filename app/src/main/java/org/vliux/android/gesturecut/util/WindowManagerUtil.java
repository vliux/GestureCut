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

    public static WindowManager.LayoutParams dialogLayoutParams(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.gravity = Gravity.CENTER;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.5f;
        return lp;
    }

    public static void closeWindow(Context context, View view){
        if(null == view.getParent()){
            AppLog.loge(TAG, "view doesn't have any parent yet, unable to removeWindow()");
            return;
        }
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(view);
    }

}
