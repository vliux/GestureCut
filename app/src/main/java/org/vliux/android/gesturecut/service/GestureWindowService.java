package org.vliux.android.gesturecut.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.main.GestureListActivity;
import org.vliux.android.gesturecut.biz.PhoneStateMonitor;
import org.vliux.android.gesturecut.ui.floatwnd.FloatWindow;
import org.vliux.android.gesturecut.ui.floatwnd.FloatWindowManager;

/**
 * Created by vliux on 4/3/14.
 */
public class GestureWindowService extends Service {
    private static final String TAG = GestureWindowService.class.getSimpleName();

    private static final int START_FOREGROUND_NOTIFY_ID = 999;

    /* SCREEN_ON and SCREEN_OFF have to be registered by code only */
    private static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private static final String SCREEN_ON = "android.intent.action.SCREEN_ON";

    private FloatWindow mFloatWindow;

    public static void showWindow(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(context, GestureWindowService.class);
        appContext.startService(intent);
        //PhoneStateMonitor.getInstance().register();
    }

    public static void hideWindow(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(context, GestureWindowService.class);
        appContext.stopService(intent);
        //PhoneStateMonitor.getInstance().register();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //registerBizReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        showFloatWindow();
        startForeground(START_FOREGROUND_NOTIFY_ID, getStartForegroundNotification());
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterBizReceivers();
        closeFloatWindow();
        stopForeground(true);
    }

    private void showFloatWindow(){
        if(null == mFloatWindow){
            mFloatWindow = new FloatWindow(this);
        }
        FloatWindowManager.showFloatWindow(this, mFloatWindow);
    }

    private void closeFloatWindow(){
        if(null != mFloatWindow){
            FloatWindowManager.closeWindow(this, mFloatWindow);
        }

        mFloatWindow = null;
    }

    private void registerBizReceivers(){
        IntentFilter screenOnOffIntentFilter = new IntentFilter();
        screenOnOffIntentFilter.addAction(SCREEN_ON);
        screenOnOffIntentFilter.addAction(SCREEN_OFF);
        registerReceiver(mScreenOnOffReceiver, screenOnOffIntentFilter);
    }

    private void unregisterBizReceivers(){
        unregisterReceiver(mScreenOnOffReceiver);
    }

    private final BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null == intent){
                return;
            }
            String action = intent.getAction();
            if(SCREEN_OFF.equals(action) && !PhoneStateMonitor.getInstance().isOnCall()){
            }
        }
    };

    private Notification getStartForegroundNotification(){
        Intent intent = new Intent(this, GestureListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(getText(R.string.notification_content))
                .setContentIntent(pendingIntent)
                .setContentTitle(getText(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_launcher);

        return builder.build();
    }

}
