package org.vliux.android.gesturecut.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.vliux.android.gesturecut.ui.ctl.FloatWindowManager;

/**
 * Created by vliux on 4/3/14.
 */
public class GestureService extends Service {
    private static final String TAG = GestureService.class.getSimpleName();
    private static final String INTENT_SHOW_FLOATING = "org.vliux.android.gesturecut.SHOW_FLOAT";

    public static void showFloating(Context context){
        Intent intent = new Intent(context, GestureService.class);
        intent.setAction(INTENT_SHOW_FLOATING);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(null != intent){
            String action = intent.getAction();
            if(INTENT_SHOW_FLOATING.equals(action)){
                FloatWindowManager.toggleWindow(this, true);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
