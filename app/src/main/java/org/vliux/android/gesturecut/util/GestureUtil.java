package org.vliux.android.gesturecut.util;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureStore;
import android.gesture.Prediction;

import org.vliux.android.gesturecut.AppConstant;

import java.util.List;
import java.util.Set;

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
        mGestureLibrary.setOrientationStyle(GestureStore.ORIENTATION_SENSITIVE);
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

    public void deleteGesture(String name){
        if(null != name && name.length() > 0){
            mGestureLibrary.removeEntry(name);
            mGestureLibrary.save();
        }
    }

    /**
     *
     * @param gesture
     * @return The gesture name previously saved in GestureLibrary; NULL if not found.
     */
    public Prediction matchGesture(Gesture gesture){
        if(null != gesture){
            List<Prediction> predictionList = mGestureLibrary.recognize(gesture);
            if(null != predictionList && predictionList.size() > 0){
                for(Prediction prediction : predictionList){
                    if(prediction.score > AppConstant.Gestures.MIN_GESTURE_MATCH_SCORE){
                        return prediction;
                    }
                }
            }
        }
        return null;
    }

    public Set<String> getGestureNames(){
        return mGestureLibrary.getGestureEntries();
    }

}
