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
            int w = dm.widthPixels;
            int h = dm.heightPixels;
            // in case screen is in landscape mode
            if (h > w) {
                screenSize[0] = w;
                screenSize[1] = h;
            } else {
                screenSize[0] = h;
                screenSize[1] = w;
            }
        }
        return screenSize;
    }


}
