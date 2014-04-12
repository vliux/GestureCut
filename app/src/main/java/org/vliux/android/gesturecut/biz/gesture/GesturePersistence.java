package org.vliux.android.gesturecut.biz.gesture;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by vliux on 4/9/14.
 */
public class GesturePersistence {

    public static void saveGesture(Context context, Gesture gesture, ComponentName componentName)
            throws GestureLibraryException, GestureSaveIconException, GestureDbException {
        int thumbWidth = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_width);
        int thumbHeight = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_height);
        Bitmap gestureBitmap = gesture.toBitmap(thumbWidth, thumbHeight, 10, 0xFFFF0000);

        // save to GestureLibrary
        String gestureName = GestureUtil.getInstance().addGesture(gesture);
        if(null == gestureName || gestureName.length() <= 0){
            throw new GestureLibraryException();
        }
        // save icon to file
        File iconDir = context.getDir(AppConstant.GestureStorage.GESTURE_ICON_DIR_NAME, Context.MODE_PRIVATE);
        String iconPath = null;
        try {
            iconPath = ImageUtil.saveBmp(gestureBitmap, iconDir, gestureName, ImageUtil.QUALITY_OK);
            if(null == iconPath || iconPath.length() <= 0){
                throw new GestureSaveIconException();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GestureSaveIconException();
        }
        // save to DB
        GestureDbTable gestureDbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
        if(!gestureDbTable.addGesture(gestureName, componentName.flattenToString(), iconPath)){
            throw new GestureDbException();
        }
        // send local broadcast
        AppBroadcastManager.sendGestureAddedBroadcast(context);
        Toast.makeText(context, String.format(context.getString(R.string.saving_gesture_completed), gestureName),
                Toast.LENGTH_SHORT).show();
    }

    public static ComponentName loadGesture(Context context, Gesture gesture){
        String gestureName = GestureUtil.getInstance().matchGesture(gesture);
        if(null == gestureName || gestureName.length() <= 0){
            return null;
        }

        GestureDbTable gestureDbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
        ContentValues dbResult = gestureDbTable.getGesture(gestureName);
        if(null != dbResult && dbResult.containsKey(GestureDbTable.DB_COL_COMPONENT_NAME_TEXT_1)){
            return ComponentName.unflattenFromString(dbResult.getAsString(GestureDbTable.DB_COL_COMPONENT_NAME_TEXT_1));
        }else{
            return null;
        }
    }

    /**
     * Failed to save gesture to Android GestureLibrary.
     */
    public static class GestureLibraryException extends Exception{
    }

    /**
     * Failed to save gesture thumbnail icon.
     */
    public static class GestureSaveIconException extends Exception{

    }

    /**
     * Failed to save gesture to DB.
     */
    public static class GestureDbException extends Exception{
    }
}
