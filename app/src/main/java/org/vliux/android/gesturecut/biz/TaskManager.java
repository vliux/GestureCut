package org.vliux.android.gesturecut.biz;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import org.vliux.android.gesturecut.GestureCutApplication;

import java.util.List;

/**
 * Created by vliux on 4/9/14.
 */
public class TaskManager {

    public static ResolvedComponent getTopComponent(Context context){
        ComponentName componentName = null;
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        if(null != runningTaskInfoList && runningTaskInfoList.size() > 0){
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
            if(null != runningTaskInfo) {
                componentName = chooseComponent(context.getPackageManager(),
                        runningTaskInfo);
                if(null != componentName){
                    Toast.makeText(context,
                            String.format("ComponentName: %s", componentName.getClassName()),
                            Toast.LENGTH_SHORT).show();
                    return new ResolvedComponent(componentName);
                }else if(null != runningTaskInfo.baseActivity){
                    Toast.makeText(context,
                            String.format("Package: %s", runningTaskInfo.baseActivity.getPackageName()),
                            Toast.LENGTH_SHORT).show();
                    return new ResolvedComponent(runningTaskInfo.baseActivity.getPackageName());
                }
            }
        }
        return null;
    }

    private static ComponentName chooseComponent(PackageManager packageManager,
                                                 ActivityManager.RunningTaskInfo runningTaskInfo){
        if(null != runningTaskInfo.topActivity
                && componentNameUsable(packageManager, runningTaskInfo.topActivity)){
            return runningTaskInfo.topActivity;
        }

        if(null != runningTaskInfo.baseActivity
                && componentNameUsable(packageManager, runningTaskInfo.baseActivity)){
            return runningTaskInfo.baseActivity;
        }

        return null;
    }

    private static boolean componentNameUsable(PackageManager packageManager, ComponentName componentName){
        if(null != componentName){
            try {
                ActivityInfo activityInfo = packageManager.getActivityInfo(componentName,
                        PackageManager.GET_META_DATA);
                if(null != activityInfo &&
                        activityInfo.exported &&
                        activityInfo.enabled){
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getDescription(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        if(null != packageManager){
            try {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                if(null != applicationInfo){
                    CharSequence charSequence = packageManager.getApplicationLabel(applicationInfo);
                    if(null != charSequence){
                        return charSequence.toString();
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getDescription(Context context, ComponentName componentName){
        return getDescription(context, componentName.getPackageName());
    }

    public static Drawable getIcon(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        if(null != packageManager){
            try {
                return packageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Drawable getIcon(Context context, ComponentName componentName){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            try {
                return packageManager.getActivityIcon(componentName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
