package org.vliux.android.gesturecut;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

import org.vliux.android.gesturecut.biz.PhoneStateMonitor;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.service.GestureWindowService;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.ui.floatwnd.FloatWindowManager;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.PreferenceHelper;

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

        if(PreferenceHelper.getUserPref(this, R.string.pref_key_float_wnd, true)){
            GestureWindowService.showWindow(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //controlFloatWindow(false);
    }

    private void initDb(){
        DbManager.init(getApplicationContext());
    }

    private void initKeyguardRelated(){
        //PhoneStateMonitor.init(this);
    }

    private void checkDebuggable() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        if (null != applicationInfo) {
            boolean debug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            AppLog.setLoggingEnabled(debug);
        }
    }

    private final Handler mHandler = new Handler();
}
