package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

import java.util.List;

/**
 * Created by vliux on 4/3/14.
 */
public class FloatWindow extends LinearLayout implements View.OnClickListener {

    private WindowManager.LayoutParams mLayoutParams;

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

    /**
     * When added to WindowManager, should set the layoutParams here.
     * @param layoutParams
     */
    public void setWindowLayoutParams(WindowManager.LayoutParams layoutParams){
        mLayoutParams = layoutParams;
    }

    @Override
    public void onClick(View view){
        SecondaryFloatWindow expandedFloatWindow = new SecondaryFloatWindow(getContext().getApplicationContext());
        WindowManagerUtil.showWindow(getContext(), expandedFloatWindow, WindowManagerUtil.WindowScope.APP);
    }

    private float mDownX;
    private float mDownY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoving(event)){
                    mLayoutParams.x = (int)event.getRawX();
                    mLayoutParams.y = (int)event.getRawY();
                    WindowManagerUtil.updateWindow(getContext(), this, mLayoutParams);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isMoving(MotionEvent event){
        float offsetX = Math.abs(event.getRawX() - mDownX);
        float offsetY = Math.abs(event.getRawY() - mDownY);
        if (offsetX > 10 || offsetY > 10) {
            return true;
        }
        return false;
    }

}
