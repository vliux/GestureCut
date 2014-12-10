package org.vliux.android.gesturecut;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.service.GestureKeyGuardService;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.GestureUtil;

/**
 * Created by vliux on 4/3/14.
 */
public class GestureCutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        checkDebuggable();
        GestureUtil.init(getApplicationContext());
        initDb();
        GestureKeyGuardService.showFloating(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initDb(){
        DbManager.init(getApplicationContext());
    }


    private void checkDebuggable() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        if (null != applicationInfo) {
            boolean debug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            AppLog.setLoggingEnabled(debug);
        }
    }

}
