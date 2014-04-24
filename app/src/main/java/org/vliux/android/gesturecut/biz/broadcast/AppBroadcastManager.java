package org.vliux.android.gesturecut.biz.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.vliux.android.gesturecut.AppConstant;

/**
 * Created by vliux on 4/12/2014.
 */
public class AppBroadcastManager {

    public static void sendGestureAddedBroadcast(Context context){
        Intent intent = new Intent(AppConstant.LocalBroadcasts.BROADCAST_GESTURE_ADDED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void registerLockerStateChangesReceiver(Context context, BroadcastReceiver receiver){
        if(null != receiver){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STARTED);
            intentFilter.addAction(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STOPPED);
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
        }
    }

    public static void sendLockerStartedBroadcast(Context context){
        Intent intent = new Intent(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STARTED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendLockerStoppedBroadcast(Context context){
        Intent intent = new Intent(AppConstant.LocalBroadcasts.BROADCAST_LOCKER_STOPPED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
