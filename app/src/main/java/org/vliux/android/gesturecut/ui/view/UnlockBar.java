package org.vliux.android.gesturecut.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 4/15/14.
 */
public class UnlockBar extends LinearLayout {
    private static final String TAG = UnlockBar.class.getSimpleName();

    private GestureDetectorCompat mGestureDetector;
    private ViewGroup mTargetViewGroup;
    private ImageView mIvUnlock;
    private OnUnlockListener mUnlockListener;
    private float mInitY = -1;

    public UnlockBar(Context context) {
        super(context);
        init();
    }

    public UnlockBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnlockBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_unlock_bar, this, true);
        mIvUnlock = (ImageView)findViewById(R.id.iv_unlock);
        mGestureDetector = new GestureDetectorCompat(getContext(), mOnGestureListener);
    }

    public void setTargetViewGroup(ViewGroup viewGroup){
        mTargetViewGroup = viewGroup;
    }

    public void setOnUnlockListener(OnUnlockListener listener){
        mUnlockListener = listener;
    }

    public void setAnimationEffects(boolean play){
        AnimationDrawable animationDrawable = (AnimationDrawable)mIvUnlock.getDrawable();
        if(play){
            animationDrawable.start();
        }else{
            animationDrawable.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(mInitY < 0){
                mInitY = UnlockBar.this.getY();
            }

            if(null != mTargetViewGroup){
                float deltaY = e2.getRawY() - e1.getRawY();
                AppLog.logd(TAG, String.format("deltaY = %s, translationY = %s",
                        String.valueOf(deltaY), String.valueOf(mTargetViewGroup.getTranslationY())));
                if((deltaY <= 0)
                        || (deltaY >=0 && mTargetViewGroup.getTranslationY() <= 0)){
                    mTargetViewGroup.setTranslationY(deltaY);
                }
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            AppLog.logd(TAG, "onFling(): velocityY = " + velocityY);
            if(velocityY < -5){
                Toast.makeText(getContext(), "onUnlockConditionFulfilled", Toast.LENGTH_SHORT).show();
                if(null != mUnlockListener){
                    mUnlockListener.onUnlockConditionFulfilled();
                }
            }else {
                reset();
            }
            return true;
        }
    };

    public interface OnUnlockListener{
        public void onUnlockConditionFulfilled();
    }

    private void reset(){
        ObjectAnimator returnAnim = ObjectAnimator.ofFloat(mTargetViewGroup, "translationY",
                mTargetViewGroup.getTranslationY(), 0.0f);
        returnAnim.setDuration(500L);
        returnAnim.setInterpolator(new OvershootInterpolator());
        returnAnim.start();
    }
}
