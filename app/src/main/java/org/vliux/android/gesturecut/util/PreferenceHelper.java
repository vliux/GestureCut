package org.vliux.android.gesturecut.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PreferenceHelper {
    private static final HashMap<String, String> keyToName;

    static {
        keyToName = new HashMap<String, String>();
    }

    /**
     * main preference
     */
    public static final String KEY_WELCOME_PAGE_ALREADY_SHOWN = "welcome_already_shown";
    static {
        keyToName.put(KEY_WELCOME_PAGE_ALREADY_SHOWN, "main");
    }

    private static String getPerferenceGroup(String key) {
        if (null == key || key.length() == 0) {
            throw new IllegalArgumentException("key == null or key is empty");
        }
        String name = keyToName.get(key);
        if (null == name) {
            throw new IllegalArgumentException("no name mapped to to key " + key);
        }
        return name;
    }

    public static boolean containsKey(Context appContext, String key) {
        return appContext.getSharedPreferences(
                getPerferenceGroup(key), 0).contains(key);
    }

    public static boolean getUserPref(Context appContext, String key, boolean defaultBoolean) {
        return appContext.getSharedPreferences(
                getPerferenceGroup(key), 0).getBoolean(key, defaultBoolean);
    }

    public static int getUserPref(Context appContext, String key, int defaultInt) {
        return appContext.getSharedPreferences(
                getPerferenceGroup(key), 0).getInt(key, defaultInt);
    }

    public static long getUserPref(Context appContext, String key, long defaultLong) {
        return appContext.getSharedPreferences(
                getPerferenceGroup(key), 0).getLong(key, defaultLong);
    }

    public static String getUserPref(Context appContext, String key, String defaultString) {
        return appContext.getSharedPreferences(
                getPerferenceGroup(key), 0).getString(key, defaultString);
    }

    //确定最小支持的sdk，如果为9则改为apply FIXME
    public static void setUserPref(Context appContext, String key, boolean value) {
        appContext.getSharedPreferences(getPerferenceGroup(key), 0).edit().putBoolean(key, value).commit();
    }

    public static void setUserPref(Context appContext, String key, int value) {
        appContext.getSharedPreferences(getPerferenceGroup(key), 0).edit().putInt(key, value).commit();
    }

    public static void setUserPref(Context appContext, String key, long value) {
        appContext.getSharedPreferences(getPerferenceGroup(key), 0).edit().putLong(key, value).commit();
    }

    public static void setUserPref(Context appContext, String key, String value) {
        appContext.getSharedPreferences(getPerferenceGroup(key), 0).edit().putString(key, value).commit();
    }

    public static void setUserPrefCurrentDate(Context appContext, String key, String format) {
        if (null == key || null == format) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String date = simpleDateFormat.format(new Date());
        setUserPref(appContext, key, date);
    }
}
