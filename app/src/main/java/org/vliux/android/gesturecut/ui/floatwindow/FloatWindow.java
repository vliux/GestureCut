package org.vliux.android.gesturecut.ui.floatwindow;

import android.app.ActivityManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.vliux.android.gesturecut.GuestCutApplication;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

import java.util.List;

/**
 * Created by vliux on 4/3/14.
 */
public class FloatWindow extends LinearLayout implements View.OnClickListener {

    public FloatWindow(Context context) {
        super(context);
        init();
    }

    public FloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_floatwindow, this, true);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        ActivityManager activityManager = (ActivityManager)getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        if(null != runningTaskInfoList && runningTaskInfoList.size() > 0){
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
            if(null != runningTaskInfo) {
                GuestCutApplication.sTargetComponentName = (null != runningTaskInfo.topActivity?
                    runningTaskInfo.topActivity : runningTaskInfo.baseActivity);
                Toast.makeText(getContext(),
                        String.format("Base:%s, Top:%s", runningTaskInfo.baseActivity.flattenToShortString(),
                                runningTaskInfo.topActivity.flattenToShortString()),
                        Toast.LENGTH_SHORT).show();
            }
        }

        SecondaryFloatWindow secondaryFloatWindow = new SecondaryFloatWindow(getContext());
        WindowManagerUtil.showWindow(getContext(), secondaryFloatWindow, WindowManagerUtil.WindowScope.APP);
    }
}
