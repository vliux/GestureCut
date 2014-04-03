package org.vliux.android.gesturecut.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import org.vliux.android.gesturecut.ui.MainActivity;

/**
 * Created by vliux on 4/3/14.
 */
public class GuestKeyGuardService extends Service {
    private static final String TAG = GuestKeyGuardService.class.getSimpleName();

    /* SCREEN_ON and SCREEN_OFF have to be registered by code only */
    public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";

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
            if(SCREEN_OFF.equals(action)){
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            }
        }
    };


}
