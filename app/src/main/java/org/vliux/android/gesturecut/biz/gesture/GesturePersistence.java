package org.vliux.android.gesturecut.biz.gesture;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.io.IOException;

/**
 * Created by vliux on 4/9/14.
 */
public class GesturePersistence {

    public static void saveGesture(Context context, Gesture gesture){
        int thumbWidth = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_width);
        int thumbHeight = (int)context.getResources().getDimension(R.dimen.gesture_thumbnail_height);
        Bitmap gestureBitmap = gesture.toBitmap(thumbWidth, thumbHeight, 10, 0xFFFF0000);

        String gestureName = GestureUtil.getInstance().addGesture(gesture);
        Toast.makeText(context, String.valueOf(gestureName), Toast.LENGTH_SHORT).show();


    }

    private static class SaveGestureRunnable implements Runnable{
        private Context context;
        private String mGestureName;
        private Bitmap mGestureIcon;
        private ComponentName mComponentName;

        public SaveGestureRunnable(Context context, String gestureName, Bitmap gestureIcon, ComponentName componentName){
            mGestureName = gestureName;
            mGestureIcon = gestureIcon;
            mComponentName = componentName;
        }

        @Override
        public void run() {
            try {
                ImageUtil.saveBmp(mGestureIcon,
                        context.getDir(AppConstant.GestureStorage.GESTURE_ICON_DIR_NAME, Context.MODE_PRIVATE),
                        mGestureName, ImageUtil.QUALITY_OK);
            } catch (IOException e) {
                e.printStackTrace();
            }

            GestureDbTable gestureDbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);

        }
    }
}
