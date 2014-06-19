package org.vliux.android.gesturecut.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.ScreenUtil;

/**
 * Created by vliux on 4/15/14.
 */
public class UnlockBar extends View {
    private static final String TAG = UnlockBar.class.getSimpleName();
    private static final float sStrokeWidthCircleWrap = 4.0f;
    private static final float sStrokeWidthArrow = 2.0f;

    private GestureDetectorCompat mGestureDetector;
    private ViewGroup mTargetViewGroup;
    private OnUnlockListener mUnlockListener;
    private int mThresholdY;
    private Paint mPaint;
    private boolean mShowReverse = false;
    private Bitmap mArrowBitmap;

    private RectF mCachedCircleRectF;
    private RectF mCachedCircleWrapRectF;
    private Rect mCachedArrowRect;

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
        mGestureDetector = new GestureDetectorCompat(getContext(), mOnGestureListener);
        mThresholdY = ScreenUtil.getScreenSize(getContext())[1]/3;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.lock_screen_up_arrow_bg));
        mArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_unlock09);
    }

    public void setTargetViewGroup(ViewGroup viewGroup){
        mTargetViewGroup = viewGroup;
    }

    public void setOnUnlockListener(OnUnlockListener listener){
        mUnlockListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = 0;
        int height = 0;
        if(null == mCachedCircleRectF) {
            width = getWidth();
            height = getHeight();

            mCachedCircleRectF = new RectF();
            float gap = sStrokeWidthArrow * 3;
            mCachedCircleRectF.top = gap;
            mCachedCircleRectF.left = gap;
            mCachedCircleRectF.bottom = height * 2 - gap;
            mCachedCircleRectF.right = width - gap;

            mCachedCircleWrapRectF = new RectF();
            gap = sStrokeWidthArrow;
            mCachedCircleWrapRectF.top = gap;
            mCachedCircleWrapRectF.left = gap;
            mCachedCircleWrapRectF.bottom = height * 2 - gap;
            mCachedCircleWrapRectF.right = width - gap;
        }

        if(mShowReverse){
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawOval(mCachedCircleRectF, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(sStrokeWidthCircleWrap);
            canvas.drawArc(mCachedCircleWrapRectF, 0.0f, 360.0f, false, mPaint);
        }

        if(null == mCachedArrowRect) {
            mCachedArrowRect = new Rect();
            int bmpWidth = mArrowBitmap.getWidth();
            int bmpHeight = mArrowBitmap.getHeight();
            mCachedArrowRect.top = height / 2 - bmpHeight / 2;
            mCachedArrowRect.left = width / 2 - bmpWidth / 2;
            mCachedArrowRect.bottom = height / 2 + bmpHeight / 2;
            mCachedArrowRect.right = width / 2 + bmpWidth / 2;
        }
        mPaint.setStrokeWidth(sStrokeWidthArrow);
        canvas.drawBitmap(mArrowBitmap, null, mCachedArrowRect, mPaint);
    }

    private void changeBkColor(boolean onPress){
        if(onPress){
            mShowReverse = true;
        }else {
            mShowReverse = false;
        }

        invalidate();
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
