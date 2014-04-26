package org.vliux.android.gesturecut.biz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 4/14/14.
 * Represent an entity of the top task.
 */
public class ResolvedComponent {
    private static final String TAG = ResolvedComponent.class.getSimpleName();

    public enum ResolvedType{
        COMPONENT_NAME,
        PACKAGE_NAME,
    }

    private ResolvedType type;
    private ComponentName componentName;
    private String packageName;

    public ResolvedComponent(ComponentName cn){
        componentName = cn;
        type = ResolvedType.COMPONENT_NAME;
    }

    public ResolvedComponent(String pkgName){
        packageName = pkgName;
        type = ResolvedType.PACKAGE_NAME;
    }

    public ResolvedType getType() {
        return type;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void startActivity(Context context){
        Intent intent = null;
        switch (type){
            case COMPONENT_NAME:
                intent = new Intent();
                intent.setComponent(componentName);
                break;
            case PACKAGE_NAME:
                PackageManager packageManager = context.getPackageManager();
                intent = packageManager.getLaunchIntentForPackage(packageName);
                break;
        }
        if(null != intent){
            try{
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                return;
            }catch (Exception e){
                AppLog.loge(TAG, "startActivity() of ResolvedComponent throws Exception");
                e.printStackTrace();
            }
        }
        Toast.makeText(context.getApplicationContext(),
                context.getApplicationContext().getString(R.string.start_activity_failed),
                Toast.LENGTH_SHORT).show();
    }
}
