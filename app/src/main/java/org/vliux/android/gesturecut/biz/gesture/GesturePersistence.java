package org.vliux.android.gesturecut.biz.gesture;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
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
    private static final String TAG = GesturePersistence.class.getSimpleName();

    public static Bitmap toBitmap(Context context, Gesture gesture){
        int thumbWidth = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_width);
        int thumbHeight = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_height);
        Bitmap gestureBitmap = gesture.toBitmap(thumbWidth, thumbHeight, 10, 0xFFFF0000);
        return gestureBitmap;
    }

    public static void saveGesture(Context context, Gesture gesture, ResolvedComponent resolvedComponent)
            throws GestureLibraryException,
            GestureSaveIconException,
            GestureDbException{
        Bitmap gestureBitmap = toBitmap(context, gesture);
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
        if (!gestureDbTable.addGesture(gestureName, resolvedComponent, iconPath)) {
            throw new GestureDbException();
        }

        // send local broadcast
        AppBroadcastManager.sendGestureAddedBroadcast(context);
        Toast.makeText(context, String.format(context.getString(R.string.saving_gesture_completed), gestureName),
                Toast.LENGTH_SHORT).show();
    }

    public static void removeGesture(Context context, String gestureName){
        if(null != gestureName && gestureName.length() > 0){
            // remove from GestureLibrary
            GestureUtil.getInstance().deleteGesture(gestureName);

            // remove gesture bitmap
            GestureDbTable gestureDbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
            GestureDbTable.DbData dbData = gestureDbTable.getGesture(gestureName);
            if(null != dbData && null != dbData.iconPath){
                File iconFile = new File(dbData.iconPath);
                if(iconFile.exists()){
                    iconFile.delete();
                }
            }

            // delete from db
            gestureDbTable.removeGesture(gestureName);
        }
    }

    public static ResolvedComponent loadGesture(Context context, Gesture gesture){
        GestureDbTable.DbData dbData = loadGestureEx(context, gesture);
        if(null != dbData){
            return dbData.resolvedComponent;
        }

        return null;
    }

    public static GestureDbTable.DbData loadGestureEx(Context context, Gesture gesture){
        Prediction prediction = GestureUtil.getInstance().matchGesture(gesture);
        if(null == prediction){
            return null;
        }

        String gestureName = prediction.name;
        if(null == gestureName || gestureName.length() <= 0){
            return null;
        }
        //Toast.makeText(context, "prediction.score=" + prediction.score, Toast.LENGTH_SHORT).show();
        GestureDbTable gestureDbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
        return gestureDbTable.getGesture(gestureName);
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
