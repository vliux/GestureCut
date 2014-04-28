package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PreferenceHelper {

    public static boolean containsKey(Context appContext, int keyStringId) {
        return PreferenceManager.getDefaultSharedPreferences(appContext).contains(appContext.getString(keyStringId));
    }

    public static boolean getUserPref(Context appContext, int keyStringId, boolean defaultBoolean) {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(appContext.getString(keyStringId), defaultBoolean);
    }

    public static int getUserPref(Context appContext, int keyStringId, int defaultInt) {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(appContext.getString(keyStringId), defaultInt);
    }

    public static long getUserPref(Context appContext, int keyStringId, long defaultLong) {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getLong(appContext.getString(keyStringId), defaultLong);
    }

    public static String getUserPref(Context appContext, int keyStringId, String defaultString) {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getString(appContext.getString(keyStringId), defaultString);
    }

    public static void setUserPref(Context appContext, int keyStringId, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putBoolean(appContext.getString(keyStringId), value).commit();
    }

    public static void setUserPref(Context appContext, int keyStringId, int value) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putInt(appContext.getString(keyStringId), value).commit();
    }

    public static void setUserPref(Context appContext, int keyStringId, long value) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putLong(appContext.getString(keyStringId), value).commit();
    }

    public static void setUserPref(Context appContext, int keyStringId, String value) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putString(appContext.getString(keyStringId), value).commit();
    }

    public static void setUserPrefCurrentDate(Context appContext, int keyStringId, String format) {
        if (null == format) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String date = simpleDateFormat.format(new Date());
        setUserPref(appContext, keyStringId, date);
    }
}
