package org.vliux.android.gesturecut.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;

/**
 * Created by vliux on 6/30/14.
 */
public class PackageRemovedReceiver extends BroadcastReceiver {
    private static final String TAG = PackageRemovedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(true != intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)){
            Uri uri = intent.getData();
            String pkg = (uri != null)? uri.getSchemeSpecificPart() : null;
            Log.d(TAG, String.format("package %s removed", pkg));
            GesturePersistence.onPackageRemovedOnDevice(context, pkg);
        }
    }
}
