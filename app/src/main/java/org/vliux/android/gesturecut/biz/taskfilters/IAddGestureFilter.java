package org.vliux.android.gesturecut.biz.taskfilters;

import android.content.Context;

import org.vliux.android.gesturecut.model.ResolvedComponent;

/**
 * Created by vliux on 4/26/14.
 */
public interface IAddGestureFilter {
    public String getReadableName();
    public void filter(Context context, ResolvedComponent resolvedComponent) throws TaskFilterException;
}
