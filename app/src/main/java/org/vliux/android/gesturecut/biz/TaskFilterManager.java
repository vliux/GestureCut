package org.vliux.android.gesturecut.biz;

import android.content.Context;

import org.vliux.android.gesturecut.biz.taskfilters.PkgRcFilter;
import org.vliux.android.gesturecut.biz.taskfilters.TaskFilterException;
import org.vliux.android.gesturecut.biz.taskfilters.IAddGestureFilter;
import org.vliux.android.gesturecut.util.AppLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vliux on 4/26/14.
 */
public class TaskFilterManager {
    private static final String TAG = TaskFilterManager.class.getSimpleName();

    private static TaskFilterManager sInstance;
    private List<IAddGestureFilter> mAddFilter;

    public static TaskFilterManager getInstance(){
        if(null == sInstance){
            synchronized (TaskFilterManager.class){
                if(null == sInstance){
                    sInstance = new TaskFilterManager();
                }
            }
        }
        return sInstance;
    }

    private TaskFilterManager(){
        mAddFilter = new ArrayList<IAddGestureFilter>();
        mAddFilter.add(new PkgRcFilter());
    }

    public boolean processAddFilters(Context context, ResolvedComponent rc)
            throws TaskFilterException {
        for(IAddGestureFilter filter : mAddFilter){
            try{
                AppLog.logd(TAG, String.format("processing add fileter %s ...", filter.getReadableName()));
                filter.filter(context, rc);
            }catch (TaskFilterException e){
                AppLog.loge(TAG, "FALSE");
                throw e;
            }
        }
        return true;
    }

}
