package org.vliux.android.gesturecut.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.GestureListActivity;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;
import org.vliux.android.gesturecut.util.PreferenceHelper;

import java.util.Set;

/**
 * Created by vliux on 6/24/14.
 */
public class LockScreenWidget extends AppWidgetProvider {
    private static final String TAG = LockScreenWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ACTIONS.FORWARD);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        ACTIONS act = null;
        try {
            act = ACTIONS.valueOf(action);
        } catch (IllegalArgumentException e){
            return;
        }

        if(null != act){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, LockScreenWidget.class));
            for(int appWidgetId : widgetIds){
                updateAppWidget(context, appWidgetManager, appWidgetId, act);
            }
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, ACTIONS actions){
        Log.d(TAG, String.format("updateAppWidget(%d, %s) ...", appWidgetId, actions.name()));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small);
        setStartSettingsPendingIntent(context, views);

        String gestureName = getGestureNameForWidget(context, actions);
        Log.d(TAG, "    gestureName = " + gestureName);
        GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(context, gestureName);
        if(null != dbData && null != dbData.resolvedComponent) {
            String iconPath = dbData.iconPath;
            Log.d(TAG, "    gestureIcon = " + iconPath);
            Bitmap bmp = null;
            if (null != iconPath && iconPath.length() > 0) {
                int iconDimen = (int)context.getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
                bmp = ImageUtil.decodeSampledBitmap(iconPath, iconDimen, iconDimen, ImageUtil.optionSave());
            }

            if (null != bmp) {
                Log.d(TAG, "null != bmp");
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
            setStartTaskPendingIntent(context, views, dbData.resolvedComponent);
        }
        setForwardPendingIntent(context, views);
        setBackwardPendingIntent(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String getGestureNameForWidget(Context context, ACTIONS action){
        Set<String> gestureNames = GestureUtil.getInstance().getGestureNames();
        int setSize = gestureNames.size();
        if(setSize <= 0){
            Log.d(TAG, "    GestureUtil.getGestureNames() returns null");
            return null;
        }else{
            int index = PreferenceHelper.getUserPref(context, R.string.pref_key_widget_index, 0);
            Log.d(TAG, "    last widget_index = " + index);
            switch (action){
                case BACKWARD:
                    index -= 1;
                    break;
                case FORWARD:
                default:
                    index += 1;
                    break;
            }

            if(index < 0){
                index = setSize - 1;
            }else if(index >= setSize){
                index = 0;
            }

            Log.d(TAG, "    current widget_index = " + index);
            PreferenceHelper.setUserPref(context, R.string.pref_key_widget_index, index);
            return gestureNames.toArray(new String[setSize])[index];
        }
    }

    private void setStartSettingsPendingIntent(Context context, RemoteViews remoteViews){
        Intent intent = new Intent(context, GestureListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_gesture_icon, pendingIntent);
    }

    private void setStartTaskPendingIntent(Context context, RemoteViews remoteViews, ResolvedComponent resolvedComponent){
        Intent intent = TaskManager.getStartActivityIntent(context, resolvedComponent);
        if(null != intent){
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_app_info_layout, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.widget_app_icon, pendingIntent);
        }
    }

    private void setForwardPendingIntent(Context context, RemoteViews remoteViews){
        Intent intent = new Intent(context, LockScreenWidget.class);
        intent.setAction(ACTIONS.FORWARD.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next, pendingIntent);
    }

    private void setBackwardPendingIntent(Context context, RemoteViews remoteViews){
        Intent intent = new Intent(context, LockScreenWidget.class);
        intent.setAction(ACTIONS.BACKWARD.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev, pendingIntent);
    }

    public enum ACTIONS {
        FORWARD,
        BACKWARD
    }
}
