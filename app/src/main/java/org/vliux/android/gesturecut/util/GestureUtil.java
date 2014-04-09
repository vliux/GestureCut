package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.Prediction;

import java.util.List;

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

    /**
     *
     * @param gesture
     * @return The name of the saved gesture. If saving failed return NULL.
     */
    public String addGesture(Gesture gesture){
        if(null != gesture){
            String name = String.valueOf(System.currentTimeMillis());
            mGestureLibrary.addGesture(name, gesture);
            if(mGestureLibrary.save()){
                return name;
            }
        }
        return null;
    }

    /**
     *
     * @param gesture
     * @return The gesture name previously saved in GestureLibrary; NULL if not found.
     */
    public String matchGesture(Gesture gesture){
        if(null != gesture){
            List<Prediction> predictionList = mGestureLibrary.recognize(gesture);
            if(null != predictionList && predictionList.size() > 0){
                return predictionList.get(0).name;
            }
        }
        return null;
    }
}
