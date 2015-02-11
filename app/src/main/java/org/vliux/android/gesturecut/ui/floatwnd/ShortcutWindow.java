package org.vliux.android.gesturecut.ui.floatwnd;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.ScreenUtil;

/**
 * Created by vliux on 1/23/15.
 */
public class ShortcutWindow extends FrameLayout {
    private static final String TAG = ShortcutWindow.class.getSimpleName();
    private GestureListView mGestureListView;
    private ViewGroup mOverlay;
    private GestureOverlayView mGestureOverLay;

    private int mTouchSlop;
    private int mInitialOverlayTranslationX;
    private int mGestureIconWidth;

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
        int screenWidth = ScreenUtil.getScreenSize(context)[0];
        mTouchSlop = -ViewConfiguration.get(context).getScaledTouchSlop(); // negative value, so detect for swipe left
        Resources res = context.getResources();
        mGestureIconWidth = // reserve space at left, showing gesture icons in listview
            (int)(res.getDimension(R.dimen.gesture_thumbnail_width) +
            res.getDimension(R.dimen.gesture_list_outter_margin) + // marginLeft of ImageView in item_gesture
            res.getDimension(R.dimen.gesture_list_item_vertical_divider_margin_horiz)); // marginRight of ImageView in item_gesture
        int overlayWidth = screenWidth - mGestureIconWidth;
        mInitialOverlayTranslationX = overlayWidth;

        LayoutInflater.from(context).inflate(R.layout.view_shortcut, this, true);
        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
        mOverlay = (ViewGroup)findViewById(R.id.sc_overlay);
        mGestureOverLay = (GestureOverlayView)findViewById(R.id.sc_ges_overlay);

        mGestureListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mGestureListView.getViewTreeObserver().removeOnPreDrawListener(this);
                startShowAnim();
                return true;
            }
        });

        mGestureListView.refresh();
        mOverlay.setTranslationX(mInitialOverlayTranslationX);
        mOverlay.getLayoutParams().width = overlayWidth;
        mGestureOverLay.addOnGesturePerformedListener(mOnGesturePerformedListener);
    }

    private void startShowAnim(){
        int[] wndLoc = FloatWindowManager.parseLocationFromPrefs(getContext());
        mGestureListView.setScaleY(0.3f);
        mGestureListView.setScaleX(0.3f);

        mGestureListView.setPivotX(wndLoc[0]);
        mGestureListView.setPivotY(wndLoc[1]);
        mGestureListView.animate().scaleY(1).scaleX(1)
                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void startCloseAnim(){
        int[] wndLoc = FloatWindowManager.parseLocationFromPrefs(getContext());
        mGestureListView.setPivotX(wndLoc[0]);
        mGestureListView.setPivotY(wndLoc[1]);
        mGestureListView.animate().scaleY(0.1f).scaleX(0.1f)
                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        FloatWindowManager.closeWindow(getContext(), ShortcutWindow.this);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            startCloseAnim();
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
                boolean isOverlayVisible = isOverlayVisible();
                if(!isOverlayVisible && xDiff < mTouchSlop){
                    Log.d(TAG, "showOverlay()");
                    mIsScrollingOverlay = true;
                    showOverlay();
                    return true;
                }else if(isOverlayVisible // Overlay is shown, swipe from left to right, and first touch left enough, will we hide the overlay
                        && ev.getX() < mGestureIconWidth
                        && xDiff > -mTouchSlop){
                    Log.d(TAG, "hideOverlay()");
                    mIsScrollingOverlay = true;
                    hideOverlay(false);
                    return true;
                }else{
                    Log.d(TAG, "less than slop, return false");
                    return false;
                }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "UP---");
                mIsScrollingOverlay = false;
                mDownX = -1;
        }
        return true;
    }

    private int calculateDistanceX(MotionEvent ev){
        if(mDownX >= 0){
            return (int)(ev.getX() - mDownX);
        }else{
            return 0;
        }
    }

    private boolean isOverlayVisible(){
        return mOverlay.getTranslationX() < mInitialOverlayTranslationX;
    }

    private void showOverlay(){
        mOverlay.setVisibility(VISIBLE);
        mOverlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                .translationX(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void hideOverlay(boolean closeShortcutWindow){
        ViewPropertyAnimator animator =
                mOverlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                .translationX(mInitialOverlayTranslationX).setInterpolator(new AccelerateInterpolator());
        if(closeShortcutWindow){
            animator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startCloseAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        animator.start();
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            ResolvedComponent rc = GesturePersistence.loadGesture(getContext(), gesture);
            if(null != rc){
                TaskManager.startActivity(getContext(), rc);
                hideOverlay(true);
            }
        }
    };
}
