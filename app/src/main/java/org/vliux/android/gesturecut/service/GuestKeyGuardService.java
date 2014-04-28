package org.vliux.android.gesturecut.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.PhoneStateMonitor;
import org.vliux.android.gesturecut.ui.MainActivity;
import org.vliux.android.gesturecut.util.PreferenceHelper;

/**
 * Created by vliux on 4/3/14.
 */
public class GuestKeyGuardService extends Service {
    private static final String TAG = GuestKeyGuardService.class.getSimpleName();

    /* SCREEN_ON and SCREEN_OFF have to be registered by code only */
    public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";

    public static void startKeyGuard(Context context) {
        Context appContext = context.getApplicationContext();
        if(PreferenceHelper.getUserPref(appContext, R.string.pref_key_lockscreen_status, true)){
            Intent intent = new Intent(appContext, GuestKeyGuardService.class);
            appContext.startService(intent);
            // register monitor for incomming calls
            PhoneStateMonitor.getInstance().register();
        }
    }

    public static void stopKeyGuard(Context context){
        Context appContext = context.getApplicationContext();
        if(!PreferenceHelper.getUserPref(appContext, R.string.pref_key_lockscreen_status, true)){
            Intent intent = new Intent(appContext, GuestKeyGuardService.class);
            appContext.stopService(intent);
            PhoneStateMonitor.getInstance().unregister();
        }
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

    private BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null == intent){
                return;
            }
            String action = intent.getAction();
            if(SCREEN_OFF.equals(action)
                    && !PhoneStateMonitor.getInstance().isOnCall()){
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            }
        }
    };


}
