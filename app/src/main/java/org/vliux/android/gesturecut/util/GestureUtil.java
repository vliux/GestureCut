package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;

/**
 * Created by vliux on 4/9/14.
 */
public class GestureUtil {
    private static GestureUtil sInstance;
    private GestureLibrary mGestureLibrary;

    public static void init(Context appContext){
        sInstance = new GestureUtil(appContext);
    }

    public static GestureUtil getInstance(){
        return sInstance;
    }

    private GestureUtil(Context context){
        mGestureLibrary = GestureLibraries.fromPrivateFile(context, "gesture_store");
        mGestureLibrary.load();
    }

    public boolean addGesture(Gesture gesture){
        if(null != gesture){
            String name = String.valueOf(System.currentTimeMillis());
            mGestureLibrary.addGesture(name, gesture);
            return mGestureLibrary.save();
        }else{
            return false;
        }
    }
}
