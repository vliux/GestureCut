package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/3/14.
 */
public class FloatWindow extends LinearLayout implements View.OnClickListener {
    private static final String TAG = FloatWindow.class.getSimpleName();

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
        WindowManagerUtil.showWindow(getContext(), expandedFloatWindow, WindowManagerUtil.WindowScope.SECOND_FLOAT_WND);
    }

    // raw location of ACTION_DOWN, which is screen-coordinator-based.
    private float mDownX;
    private float mDownY;
    // location of ACTION_DOWN inside this view
    private float mDownInnerX;
    private float mDownInnerY;
    // checked by ACTION_UP, if there was not any movement, won't update window.
    private boolean mIsPrevMoved = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY() - 25;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mIsPrevMoved = false;
                mDownX = rawX;
                mDownY = rawY;
                mDownInnerX = event.getX();
                mDownInnerY = event.getY();
                Log.d(TAG, String.format("DOWN: %f, %f; %f, %f", mDownX, mDownY, event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoving(event)){
                    mIsPrevMoved = true;
                    mLayoutParams.x = (int)(rawX - mDownInnerX);
                    mLayoutParams.y = (int)(rawY - mDownInnerY);
                    Log.d(TAG, String.format("MOVE: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                    WindowManagerUtil.updateWindow(getContext(), this, mLayoutParams, false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDownInnerX = 0;
                mDownInnerY = 0;
                mDownX = 0;
                mDownY = 0;
                Log.d(TAG, String.format("CANCEL: %f, %f; %f, %f", rawX, rawY, event.getX(), event.getY()));
                mIsPrevMoved = false;
                break;
            case MotionEvent.ACTION_UP:
                mLayoutParams.x = (int)(rawX - mDownInnerX);
                mLayoutParams.y = (int)(rawY - mDownInnerY);
                Log.d(TAG, String.format("UP: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                if(mIsPrevMoved) {
                    WindowManagerUtil.updateWindow(getContext(), this, mLayoutParams, true);
                }

                mIsPrevMoved = false;
                mDownInnerX = 0;
                mDownInnerY = 0;
                mDownX = 0;
                mDownY = 0;
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
