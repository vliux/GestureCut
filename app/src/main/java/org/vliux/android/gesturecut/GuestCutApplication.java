package org.vliux.android.gesturecut;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 4/3/14.
 */
public class GuestCutApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        checkDebuggable();
    }

    private void checkDebuggable(){
        ApplicationInfo applicationInfo = getApplicationInfo();
        if(null != applicationInfo){
            boolean debug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            AppLog.setLoggingEnabled(debug);
        }
    }
}
