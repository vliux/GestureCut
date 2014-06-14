package org.vliux.android.gesturecut.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.ScreenUtil;

/**
 * Created by vliux on 4/15/14.
 */
public class UnlockBar extends LinearLayout {
    private static final String TAG = UnlockBar.class.getSimpleName();

    private GestureDetectorCompat mGestureDetector;
    private ViewGroup mTargetViewGroup;
    private ImageView mIvUnlock;
    private OnUnlockListener mUnlockListener;
    private Drawable mOrgBkDrawable;
    private int mThresholdY;

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
        mThresholdY = ScreenUtil.getScreenSize(getContext())[1]/3;
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

    private void changeBkColor(boolean onPress){
        if(onPress){
            if(null == mOrgBkDrawable){
                mOrgBkDrawable = getBackground();
            }
            setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{getContext().getResources().getColor(R.color.gesture_cur_blue),
                            getContext().getResources().getColor(R.color.gesture_cur_blue_transparent)}));

        }else {
            setBackgroundDrawable(mOrgBkDrawable);
        }
    }

    private boolean mUnlockAfterActionUp = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                changeBkColor(true);
                break;
            case MotionEvent.ACTION_UP:
                changeBkColor(false);
                if(mUnlockAfterActionUp){
                    AppLog.logd(TAG, "flyAway() as unlockAfterActionUp=TRUE");
                    flyAway();
                }else if(!mIsInAnim){
                    AppLog.logd(TAG, "reset() from onTouchEvent().ACTION_UP");
                    reset();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                changeBkColor(false);
                break;
        }
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
            if(null != mTargetViewGroup){
                float deltaY = e2.getRawY() - e1.getRawY();
                AppLog.logd(TAG, String.format("deltaY = %s, distanceY = %s",
                        deltaY, distanceY));
                if((deltaY <= 0)
                        || (deltaY >=0 && mTargetViewGroup.getTranslationY() <= 0)){
                    mTargetViewGroup.setTranslationY(deltaY);
                }

                if(!mUnlockAfterActionUp && deltaY <= -mThresholdY){
                    AppLog.logd(TAG, "unlockAfterActionUp=TRUE");
                    mUnlockAfterActionUp = true;
                }else if(mUnlockAfterActionUp && deltaY > -mThresholdY){
                    AppLog.logd(TAG, "unlockAfterActionUp=FALSE");
                    mUnlockAfterActionUp = false;
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
            if(velocityY < AppConstant.LockScreen.MIN_UNLOCK_FLOING_VELOCITY){
                AppLog.logd(TAG, "flyAway() from onFling()");
                flyAway();
            }else if(!mUnlockAfterActionUp){
                AppLog.logd(TAG, "reset() from onFling()");
                reset();
            }
            return true;
        }
    };

    public interface OnUnlockListener{
        public void onUnlockConditionFulfilled();
    }

    private boolean mIsInAnim = false;
    private void reset(){
        if(!mIsInAnim){
            mIsInAnim = true;
            ObjectAnimator returnAnim = ObjectAnimator.ofFloat(mTargetViewGroup, "translationY",
                    mTargetViewGroup.getTranslationY(), 0.0f);
            returnAnim.setDuration(AppConstant.Anim.ANIM_DURATION_LONGER);
            returnAnim.setInterpolator(new OvershootInterpolator());
            returnAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsInAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsInAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsInAnim = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mIsInAnim = true;
                }
            });
            returnAnim.start();
        }
    }

    private void flyAway(){
        if(!mIsInAnim){
            mIsInAnim = true;
            ObjectAnimator flyAnim = ObjectAnimator.ofFloat(mTargetViewGroup, "translationY",
                    mTargetViewGroup.getTranslationY(), -mTargetViewGroup.getHeight());
            flyAnim.setDuration(800L);
            flyAnim.setInterpolator(new OvershootInterpolator());
            flyAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsInAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsInAnim = false;
                    //Toast.makeText(getContext(), "onUnlockConditionFulfilled", Toast.LENGTH_SHORT).show();
                    if(null != mUnlockListener){
                        mUnlockListener.onUnlockConditionFulfilled();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsInAnim = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mIsInAnim = true;
                }
            });
            flyAnim.start();
        }
    }
}
