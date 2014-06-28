package org.vliux.android.gesturecut.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.ctl.GestureItemTouchedEventBus;
import org.vliux.android.gesturecut.ui.view.UnlockBar;
import org.vliux.android.gesturecut.ui.view.gesturelistview.simplified.SimplifiedGestureListView;
import org.vliux.android.gesturecut.util.AnimUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends Activity {
    private ViewGroup mOutmostLayout;
    private GestureOverlayView mGesutreOverLayView;
    private SimplifiedGestureListView mSimplifiedGestureListView;
    private UnlockBar mUnlockBar;
    private TextView mTvTime;
    private TextView mTvDate;
    private TimeChangeReceiver mTimeChangeReceiver;
    private ImageView mIvAppIconAnim; // ImageView of Animator for starting activity for the given gesture

    private View mMaskLayer;
    private TextView mMaskLayerTextView;
    private MaskLayerAnimation mMaskLayerAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyGuardFlags();
        setContentView(R.layout.activity_main);
        mOutmostLayout = (ViewGroup)findViewById(R.id.main_outmost_layout);
        mGesutreOverLayView = (GestureOverlayView)findViewById(R.id.main_gesture_overlay);
        mSimplifiedGestureListView = (SimplifiedGestureListView)findViewById(R.id.main_gesture_simp_listview);
        mUnlockBar = (UnlockBar)findViewById(R.id.main_unlock_bar);
        mTvTime = (TextView)findViewById(R.id.main_tv_time);
        mTvDate = (TextView)findViewById(R.id.main_tv_date);
        mIvAppIconAnim = (ImageView)findViewById(R.id.main_appicon_startactiv);
        mMaskLayer = findViewById(R.id.main_mask_layer);
        mMaskLayerTextView = (TextView)findViewById(R.id.main_mask_layer_text);

        loadCustomFont();
        mSimplifiedGestureListView.getLayoutParams().height = decideSimplifiedGestureListViewHeight();
        mGesutreOverLayView.addOnGesturePerformedListener(mOnGesutrePerformedListener);
        mUnlockBar.setTargetViewGroup(mOutmostLayout);
        mUnlockBar.setOnUnlockListener(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlockConditionFulfilled() {
                finish();
            }
        });

        mTimeChangeReceiver = new TimeChangeReceiver();

        // show welcome page after 1000ms
        mOutmostLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                WelcomeActivity.startWelcomeIfNeeded(MainActivity.this);
            }
        }, 1000L);
    }

    private void setLayoutHeightExceedScreen(){
        Rect decroWndRect = new Rect();
        mOutmostLayout.getWindowVisibleDisplayFrame(decroWndRect);
        mOutmostLayout.getLayoutParams().height = decroWndRect.height() +
            mUnlockBar.getLayoutParams().height/2;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register eventbus for show/hide mask layer
        GestureItemTouchedEventBus.register(mSimplGestureListViewItemTouchedEventHandler);
    }

    @Override
    protected void onStop() {
        super.onDestroy();
        // unregister eventbus
        GestureItemTouchedEventBus.unregister(mSimplGestureListViewItemTouchedEventHandler);
    }

    private int decideSimplifiedGestureListViewHeight(){
        float sizeA = getResources().getDimension(R.dimen.gesture_list_item_height) * 4;
        float sizeB = ScreenUtil.getScreenSize(this)[1]/2;
        return (int)(sizeA > sizeB ? sizeA : sizeB);
    }

    private void loadCustomFont(){
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Fontfabric.otf");
        mTvTime.setTypeface(typeFace);
        mTvDate.setTypeface(typeFace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimplifiedGestureListView.setAutoRefresh(true);
        mTimeChangeReceiver.register();
        AppBroadcastManager.sendLockerStartedBroadcast(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSimplifiedGestureListView.setAutoRefresh(false);
        mTimeChangeReceiver.unregister();
        AppBroadcastManager.sendLockerStoppedBroadcast(this);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    private void setKeyGuardFlags(){
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        /**
         * @author haihong.xiahh add flags to show activity before key guard
         *         FLAG_SHOW_WHEN_LOCKED : special flag to let windows be shown
         *         when the screen is locked. FLAG_DISMISS_KEYGUARD : when set
         *         the window will cause the keyguard to be dismissed, only if
         *         it is not a secure lock keyguard.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesutrePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            ResolvedComponent resolvedComponent = GesturePersistence.loadGesture(getApplicationContext(), gesture);
            if(null != resolvedComponent){
                /*Toast.makeText(getApplicationContext(),
                        getString(R.string.start_activity_from_gesture),
                        Toast.LENGTH_SHORT).show();*/
                AnimUtil.getStartActivityAnimatorSet(MainActivity.this, mIvAppIconAnim, resolvedComponent, null).start();
            }else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_gesture_match), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context paramContext, Intent paramIntent) {
            refreshTimeTextView();
        }

        public void register() {
            refreshTimeTextView();
            IntentFilter localIntentFilter = new IntentFilter();
            localIntentFilter.addAction("android.intent.action.TIME_SET");
            localIntentFilter.addAction("android.intent.action.TIME_TICK");

            MainActivity.this.registerReceiver(this, localIntentFilter);

        }

        public void unregister() {
            MainActivity.this.unregisterReceiver(this);
        }

        private void refreshTimeTextView(){
            Date date = new Date();
            mTvTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(date));
            mTvDate.setText(new SimpleDateFormat("yyyy-MM-dd | EEEE", Locale.CHINA).format(date));
        }

    }

    private final GestureItemTouchedEventBus.TouchedEventHandler mSimplGestureListViewItemTouchedEventHandler = new GestureItemTouchedEventBus.TouchedEventHandler() {
        @Override
        public void onEventMainThread(GestureItemTouchedEventBus.TouchedEvent touchedEvent) {
            if(null == mMaskLayerAnim){
                mMaskLayerAnim = new MaskLayerAnimation();
            }

            switch (touchedEvent.getEventType()){
                case ACTION_DOWN:
                    if(mMaskLayer.getVisibility() != View.VISIBLE) {
                        mMaskLayerAnim.showMaskLayer();
                    }
                    break;
                case START_TASK:
                    AnimUtil.getStartActivityAnimatorSet(MainActivity.this, mIvAppIconAnim, touchedEvent.getResolvedComponent(), null).start();
                case ACTION_UP:
                    if(mMaskLayer.getVisibility() != View.GONE){
                        mMaskLayerAnim.hideMaskLayer();
                    }
                    break;
            }
        }
    };

    private class MaskLayerAnimation{
        private Animator mPrevShowAnimator;
        private Animator mPrevHideAnimator;

        public void showMaskLayer(){
            if(null != mPrevShowAnimator && mPrevShowAnimator.isStarted()){
                return;
            }else if(null != mPrevHideAnimator && mPrevHideAnimator.isStarted()){
                mPrevHideAnimator.cancel();
            }

            final Animator animator = ObjectAnimator.ofFloat(mMaskLayer, "alpha", 0.0f, 1.0f);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mPrevShowAnimator = animator;
                    mMaskLayer.setVisibility(View.VISIBLE);
                    mMaskLayerTextView.setText(getString(R.string.lock_screen_mask_layer_msg));
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPrevShowAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mPrevShowAnimator = null;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }

        private void hideMaskLayer(){
            if(null != mPrevHideAnimator && mPrevHideAnimator.isStarted()){
                return;
            }else if(null != mPrevShowAnimator && mPrevShowAnimator.isStarted()){
                mPrevShowAnimator.cancel();
            }

            final Animator animator = ObjectAnimator.ofFloat(mMaskLayer, "alpha", 1.0f, 0.0f);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mPrevHideAnimator = animator;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPrevHideAnimator = null;
                    mMaskLayer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mPrevHideAnimator = null;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }
}
