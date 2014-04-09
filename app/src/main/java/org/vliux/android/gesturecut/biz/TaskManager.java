package org.vliux.android.gesturecut.biz;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import org.vliux.android.gesturecut.GestureCutApplication;

import java.util.List;

/**
 * Created by vliux on 4/9/14.
 */
public class TaskManager {

    public static ComponentName getTopComponentName(Context context){
        ComponentName componentName = null;
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        if(null != runningTaskInfoList && runningTaskInfoList.size() > 0){
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
            if(null != runningTaskInfo) {
                componentName = runningTaskInfo.baseActivity;
                Toast.makeText(context,
                        String.format("Base:%s, Top:%s", runningTaskInfo.baseActivity.flattenToShortString(),
                                runningTaskInfo.topActivity.flattenToShortString()),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return componentName;
    }
}
