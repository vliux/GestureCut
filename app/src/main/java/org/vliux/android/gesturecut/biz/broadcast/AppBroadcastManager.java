package org.vliux.android.gesturecut.biz.broadcast;

import android.content.Context;
import android.content.Intent;
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
}
