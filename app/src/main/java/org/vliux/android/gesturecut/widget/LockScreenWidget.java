package org.vliux.android.gesturecut.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.GestureListActivity;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.util.Set;

/**
 * Created by vliux on 6/24/14.
 */
public class LockScreenWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, GestureListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small);
            views.setOnClickPendingIntent(R.id.widget_gesture_icon, pendingIntent);

            String gestureName = randomSelectGestureName();
            GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(context, gestureName);
            if(null != dbData && null != dbData.resolvedComponent) {
                String iconPath = dbData.iconPath;
                Bitmap bmp = null;
                if (null != iconPath && iconPath.length() > 0) {
                    int iconDimen = (int)context.getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
                    bmp = ImageUtil.decodeSampledBitmap(iconPath, iconDimen, iconDimen, ImageUtil.optionSave());
                }

                if (null != bmp) {
                    views.setImageViewBitmap(R.id.widget_gesture_icon, bmp);
                }

                Drawable appIconDrawable = TaskManager.getIcon(context, dbData.resolvedComponent);
                if(appIconDrawable instanceof BitmapDrawable){
                    Bitmap appIcon = ((BitmapDrawable)appIconDrawable).getBitmap();
                    views.setImageViewBitmap(R.id.widget_app_icon, appIcon);
                }

                String[] descs = TaskManager.getDescription(context, dbData.resolvedComponent, false);
                views.setTextViewText(R.id.widget_appname, descs[0]);
                views.setTextViewText(R.id.widget_appdetail, descs[1]);
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private String randomSelectGestureName(){
        Set<String> gestureNames = GestureUtil.getInstance().getGestureNames();
        int setSize = gestureNames.size();
        if(null == gestureNames || setSize <= 0){
            return null;
        }else{
            int randomIndex = (int)(Math.random() * (setSize - 1));
            return gestureNames.toArray(new String[setSize])[randomIndex];
        }
    }

}
