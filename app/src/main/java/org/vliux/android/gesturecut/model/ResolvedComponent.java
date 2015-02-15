package org.vliux.android.gesturecut.model;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 4/14/14.
 * Represent an entity of the top task.
 */
public class ResolvedComponent implements Parcelable{
    private static final String TAG = ResolvedComponent.class.getSimpleName();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeParcelable(componentName, 0);
        dest.writeString(packageName);
    }

    public enum ResolvedType{
        COMPONENT_NAME,
        PACKAGE_NAME,
    }

    public static final Creator<ResolvedComponent> CREATOR = new Creator<ResolvedComponent>() {
        @Override
        public ResolvedComponent createFromParcel(Parcel source) {
            ResolvedComponent rc = null;
            ResolvedType rType = ResolvedType.valueOf(source.readString());
            ComponentName cn = ComponentName.readFromParcel(source);
            String pkgName = source.readString();

            switch (rType){
                case COMPONENT_NAME:
                    rc = new ResolvedComponent(cn);
                    break;
                case PACKAGE_NAME:
                    rc = new ResolvedComponent(pkgName);
                    break;
            }
            return rc;
        }

        @Override
        public ResolvedComponent[] newArray(int size) {
            return new ResolvedComponent[0];
        }
    };

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

    public static ResolvedComponent restoreResolvedComponent(String componentName, String componentType){
        ResolvedComponent resolvedComponent = null;
        try {
            ResolvedComponent.ResolvedType resolvedType =
                    ResolvedComponent.ResolvedType.valueOf(componentType);
            switch (resolvedType) {
                case COMPONENT_NAME:
                    resolvedComponent = new ResolvedComponent(ComponentName.unflattenFromString(componentName));
                    break;
                case PACKAGE_NAME:
                    resolvedComponent = new ResolvedComponent(componentName);
                    break;
            }
        }catch(IllegalArgumentException e){
            AppLog.loge(TAG, "invalid resolvedType from DB: " + componentName);
            return null;
        }
        return resolvedComponent;
    }

}
