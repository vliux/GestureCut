package org.vliux.android.gesturecut.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.biz.PhoneStateMonitor;
import org.vliux.android.gesturecut.ui.floatwindow.FloatWindowManager;

/**
 * Created by vliux on 4/3/14.
 */
public class GestureKeyGuardService extends Service {
    private static final String TAG = GestureKeyGuardService.class.getSimpleName();

    /* SCREEN_ON and SCREEN_OFF have to be registered by code only */
    private static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private static final String SCREEN_ON = "android.intent.action.SCREEN_ON";

    private static final String INTENT_SHOW_WINDOW = "org.vliux.android.gesture.SHOW_WND";
    private static final String INTENT_HIDE_WINDOW = "org.vliux.android.gesture.HIDE_WND";

    public static void showWindow(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(INTENT_SHOW_WINDOW);
        appContext.startService(intent);
        PhoneStateMonitor.getInstance().register();
    }

    public static void hideWindow(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(INTENT_HIDE_WINDOW);
        appContext.startService(intent);
        PhoneStateMonitor.getInstance().register();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBizReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(null != intent){
            String action = intent.getAction();
            if(INTENT_SHOW_WINDOW.equals(action)){
                FloatWindowManager.toggleWindow(this, true);
            }else if(INTENT_HIDE_WINDOW.equals(action)){
                FloatWindowManager.toggleWindow(this, false);
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBizReceivers();
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


}
