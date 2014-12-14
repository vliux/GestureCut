package org.vliux.android.gesturecut.model;

import android.content.ComponentName;

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
        switch (type){
            case COMPONENT_NAME:
                return componentName.getPackageName();
            case PACKAGE_NAME:
                return packageName;
        }
        return packageName;
    }

}
