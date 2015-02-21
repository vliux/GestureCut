package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.add.AddGestureActivity;
import org.vliux.android.gesturecut.ui.SizeCalculator;
import org.vliux.android.gesturecut.ui.floatwnd.FloatWindowManager;
import org.vliux.android.gesturecut.ui.view.glv.GestureListView;
import org.vliux.android.gesturecut.util.ScreenUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 1/23/15.
 */
public class ShortcutWindow extends FrameLayout implements IShortcutWindow {
    static final String TAG = ShortcutWindow.class.getSimpleName();
    private GestureListView mGestureListView;
    private ViewGroup mOverlay;
    private GestureOverlayView mGestureOverLay;
    private OverlayKnob mKnob;
    private ImageView mIvTargetApp;

    private int mTouchSlop;
    private int mInitialOverlayTranslationX;
    private int mTargetOverlayTranslationX;
    private int mGestureIconWidth;

    private OverlayMoveMode mCurrentOverlayMode = OverlayMoveMode.UNKNOWN;
    private OverlayKnobPresenter mOverlayKnobPresenter;
    private OverlayGesturePresenter mOverlayGesturePresenter;
    private StartTaskPresenter mStartTaskPresenter;

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
        int[] screenDimen = ScreenUtil.getScreenSize(context);
        int screenWidth = screenDimen[0];
        int screenHeight = screenDimen[1];
        mTouchSlop = -ViewConfiguration.get(context).getScaledTouchSlop(); // negative value, so detect for swipe left
        Resources res = context.getResources();
        mGestureIconWidth = SizeCalculator.gestureIconWidth(res); // reserve space at left, showing gesture icons in listview

        LayoutInflater.from(context).inflate(R.layout.view_shortcut, this, true);
        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
        mOverlay = (ViewGroup)findViewById(R.id.sc_overlay);
        mGestureOverLay = (GestureOverlayView)findViewById(R.id.sc_ges_overlay);
        mKnob = (OverlayKnob)findViewById(R.id.sc_knob);
        mIvTargetApp = (ImageView)findViewById(R.id.sc_target_icon);

        mOverlayKnobPresenter = new OverlayKnobPresenter(this);
        mOverlayGesturePresenter = new OverlayGesturePresenter(context, this, mTouchSlop);
        mStartTaskPresenter = new StartTaskPresenter(this, mIvTargetApp, -screenHeight/2);

        mGestureListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mGestureListView.getViewTreeObserver().removeOnPreDrawListener(this);
                startShowAnim();
                return true;
            }
        });
        //mGestureListView.setOnGestureIconClickedListener(mStartTaskPresenter);
        mGestureListView.setOnItemClickListener(mStartTaskPresenter);
        mGestureListView.setOnEmptyViewClickedListener(mGestureListEmptyViewClicked);
        mGestureListView.refresh();

        // as the width of the overlay will be changed below, while its layout_gravity is right,
        // so calculation of translationX is then affected.
        mTargetOverlayTranslationX = 0;
        mInitialOverlayTranslationX = screenWidth - mGestureIconWidth;

        mOverlay.setTranslationX(mInitialOverlayTranslationX);

        mGestureOverLay.addOnGesturePerformedListener(mStartTaskPresenter);
        mGestureOverLay.getLayoutParams().width = screenWidth - mGestureIconWidth;
        mOverlay.getLayoutParams().width = screenWidth - (mGestureIconWidth - mKnob.getRadius());
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
                        mOverlayKnobPresenter.onShortcutWindowClosed();
                        EventBus.getDefault().post(new IShortcutWindow.EventToKnob(IShortcutWindow.EventToKnob.WND_CLOSING));
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
            if(isOverlayVisible()){
                hideOverlay(true);
            }else {
                startCloseAnim();
            }
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

    private int mDownX = -1;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mCurrentOverlayMode == OverlayMoveMode.BY_KNOB
                || mCurrentOverlayMode == OverlayMoveMode.BY_GESTURE){
            //Log.d(TAG, "knob is pressed, stop intercepting events");
            return false;
        }

        int action = MotionEventCompat.getActionMasked(ev);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "DOWN: " + ev.getX());
                mDownX = (int)ev.getX();
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "MOVE: " + ev.getX());
                int xDiff = calculateDistanceX(ev);
                Log.d(TAG, "xDiff = " + xDiff);
                Log.d(TAG, "touchSlop = " + mTouchSlop);
                boolean isOverlayVisible = isOverlayVisible();
                if(!isOverlayVisible && xDiff < mTouchSlop){
                    setExclusiveMoveMode(OverlayMoveMode.BY_GESTURE);
                    return true;
                }else if(isOverlayVisible // Overlay is shown, swipe from left to right, and first touch left enough, will we hide the overlay
                        && ev.getX() < mGestureIconWidth
                        && xDiff > -mTouchSlop){
                    setExclusiveMoveMode(OverlayMoveMode.BY_GESTURE);
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
        mOverlayGesturePresenter.onTouchEvent(event);
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
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

    @Override
    public int getInitialTranslationX() {
        return mInitialOverlayTranslationX;
    }

    @Override
    public int getTargetTranslationX() {
        return mTargetOverlayTranslationX;
    }

    @Override
    public void setExclusiveMoveMode(OverlayMoveMode mode) {
        mCurrentOverlayMode = mode;
    }

    @Override
    public OverlayMoveMode getExclusiveMoveMode() {
        return mCurrentOverlayMode;
    }

    @Override
    public void showOverlay(){
        if(mCurrentOverlayMode == OverlayMoveMode.BY_GESTURE) {
            mGestureOverLay.setVisibility(VISIBLE);
            ViewPropertyAnimator animator = mOverlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                    .translationX(mTargetOverlayTranslationX).setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mGestureOverLay.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            EventBus.getDefault().post(new EventToKnob(EventToKnob.END_STATE_LEFT));
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
            animator.start();
        }
    }

    @Override
    public void hideOverlay(final boolean closeShortcutWindow){
        ViewPropertyAnimator animator =
                mOverlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                .translationX(mInitialOverlayTranslationX).setInterpolator(new AccelerateInterpolator());
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mGestureOverLay.setVisibility(GONE);
                EventBus.getDefault().post(new EventToKnob(EventToKnob.END_STATE_RIGHT));
                if(closeShortcutWindow) {
                    startCloseAnim();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    @Override
    public View getOverlayView() {
        return mOverlay;
    }

    @Override
    public void setGestureOverlayViewVisible(int visibility) {
        mGestureOverLay.setVisibility(visibility);
    }

    private final OnClickListener mGestureListEmptyViewClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = ShortcutWindow.this.getContext();
            if(null != context) {
                Intent intent = new Intent(context, AddGestureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                startCloseAnim();
            }
        }
    };
}
