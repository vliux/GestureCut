package org.vliux.android.gesturecut.util;

import android.util.Log;

public class AppLog {

    private static boolean isPrintLog = true;

    public static boolean getLoggingEnabled() {
        return isPrintLog;
    }

    public static void setLoggingEnabled(boolean open) {
        isPrintLog = open;
    }

    public static void printStackTrace(Throwable e) {
        if (e != null && isPrintLog) {
            e.printStackTrace();
        }
    }

    public static void logd(String tag, String msg) {
        if (tag != null && msg != null) {
            if (isPrintLog) {
                Log.d(tag, msg);
            }
        }

        return;
    }

    public static void loge(String tag, String msg) {
        if (tag != null && msg != null) {
            if (isPrintLog) {
                Log.e(tag, msg);
            }
        }

        return;
    }

    public static void logi(String tag, String msg) {
        if (tag != null && msg != null) {
            if (isPrintLog) {
                Log.i(tag, msg);
            }
        }

        return;
    }

    public static void logv(String tag, String msg) {
        if (tag != null && msg != null) {
            if (isPrintLog) {
                Log.v(tag, msg);
            }
        }

        return;
    }

    public static void logw(String tag, String msg) {
        if (tag != null && msg != null) {
            if (isPrintLog) {
                Log.w(tag, msg);
            }
        }
        return;
    }


}
