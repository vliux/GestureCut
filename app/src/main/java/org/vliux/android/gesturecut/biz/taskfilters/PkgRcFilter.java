package org.vliux.android.gesturecut.biz.taskfilters;

import android.content.Context;
import android.content.pm.PackageManager;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;

/**
 * Created by vliux on 4/26/14.
 */
public class PkgRcFilter implements IAddGestureFilter {
    private static final String TAG = PkgRcFilter.class.getSimpleName();

    @Override
    public String getReadableName() {
        return TAG;
    }

    @Override
    public void filter(Context context, ResolvedComponent resolvedComponent)
            throws TaskFilterException {
        if(ResolvedComponent.ResolvedType.PACKAGE_NAME == resolvedComponent.getType()){
            PackageManager packageManager = context.getPackageManager();
            if(null == packageManager.getLaunchIntentForPackage(resolvedComponent.getPackageName())){
                throw new TaskFilterException(context.getString(R.string.target_component_invalid));
            }
        }
    }
}
