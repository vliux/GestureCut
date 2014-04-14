package org.vliux.android.gesturecut.biz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/14/14.
 * Represent an entity of the top task.
 */
public class ResolvedComponent {
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }
}
