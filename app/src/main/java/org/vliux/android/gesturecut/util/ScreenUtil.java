package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by vliux on 4/11/14.
 */
public class ScreenUtil {

    public static int[] getScreenSize(Context context) {
        int[] screenSize = new int[2];
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            /*
             * respect the change of orientation
             */
            screenSize[0] = dm.widthPixels;
            screenSize[1] = dm.heightPixels;

        }
        return screenSize;
    }

    public static int getStatusBarHeight(Context context) {
        int result = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
