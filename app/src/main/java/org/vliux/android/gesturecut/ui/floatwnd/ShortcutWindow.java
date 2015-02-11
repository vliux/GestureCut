package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.ScreenUtil;

/**
 * Created by vliux on 1/23/15.
 */
public class ShortcutWindow extends FrameLayout {
    private static final String TAG = ShortcutWindow.class.getSimpleName();
    private GestureListView mGestureListView;
    private ViewGroup mOverlay;
    private int mTouchSlop;
    private int mScreenWidth;

    public ShortcutWindow(Context context) {
        super(context);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScreenWidth = ScreenUtil.getScreenSize(context)[0];

        LayoutInflater.from(context).inflate(R.layout.view_shortcut, this, true);
        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
        mOverlay = (ViewGroup)findViewById(R.id.sc_overlay);

        mGestureListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mGestureListView.getViewTreeObserver().removeOnPreDrawListener(this);
                startShowAnim();
                return true;
            }
        });
        mGestureListView.refresh();
        mOverlay.setTranslationX(-mScreenWidth);
    }

    private void startShowAnim(){
        int[] wndLoc = FloatWindowManager.parseLocationFromPrefs(getContext());
        mGestureListView.setScaleY(0.3f);
        mGestureListView.setScaleX(0.3f);

        mGestureListView.setPivotX(wndLoc[0]);
        mGestureListView.setPivotY(wndLoc[1]);
        mGestureListView.animate().scaleY(1).scaleX(1).setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            FloatWindowManager.closeWindow(getContext(), this);
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

    private boolean mIsScrollingOverlay = false;
    private int mDownX = -1;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "DOWN: " + ev.getX());
                mDownX = (int)ev.getX();
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "MOVE: " + ev.getX());
                if(mIsScrollingOverlay){
                    Log.d(TAG, "isScrollingOverlay = true, return true");
                    return true;
                }
                int xDiff = calculateDistanceX(ev);
                Log.d(TAG, "xDiff = " + xDiff);
                Log.d(TAG, "touchSlop = " + mTouchSlop);
                if(xDiff > mTouchSlop){
                    Log.d(TAG, "showOverlay()");
                    mIsScrollingOverlay = true;
                    showOverlay();
                    return true;
                }else{
                    Log.d(TAG, "less than slop, return false");
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "UP");
                mIsScrollingOverlay = false;
                mDownX = -1;
                return false;
        }
        return false;
    }

    private int calculateDistanceX(MotionEvent ev){
        if(mDownX >= 0){
            Log.d(TAG, "mMotionEventDown.X = " + mDownX + ", ev.X = " + ev.getX());
            return (int)(ev.getX() - mDownX);
        }else{
            Log.d(TAG, "mMotionEventDown = null");
            return 0;
        }
    }

    private void showOverlay(){
        mOverlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).translationX(0).setInterpolator(new DecelerateInterpolator()).start();
    }
}
