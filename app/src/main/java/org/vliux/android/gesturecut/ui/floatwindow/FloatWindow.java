package org.vliux.android.gesturecut.ui.floatwindow;

import android.app.ActivityManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.vliux.android.gesturecut.GestureCutApplication;
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
        SecondaryFloatWindow expandedFloatWindow = new SecondaryFloatWindow(getContext().getApplicationContext());
        WindowManagerUtil.showWindow(getContext(), expandedFloatWindow, WindowManagerUtil.WindowScope.APP);
    }
}
