package org.vliux.android.gesturecut.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.view.GestureList.GestureList;
import org.vliux.android.gesturecut.ui.view.UnlockBar;
import org.vliux.android.gesturecut.util.AnimUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ViewGroup mOutmostLayout;
    private GestureOverlayView mGesutreOverLayView;
    private GestureList mGesutreListLayout;
    private ImageView mIvSettings; //outmost settings btn
    private UnlockBar mUnlockBar;
    private TextView mTvTime;
    private TextView mTvDate;
    private TimeChangeReceiver mTimeChangeReceiver;
    private int mScreenHeight;
    private ImageView mIvAppIconAnim; // ImageView of Animator for starting activity for the given gesture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyGuardFlags();
        setContentView(R.layout.activity_main);
        mOutmostLayout = (ViewGroup)findViewById(R.id.main_outmost_layout);
        mGesutreOverLayView = (GestureOverlayView)findViewById(R.id.main_gesture_overlay);
        mGesutreListLayout = (GestureList)findViewById(R.id.main_gesture_list_layout);
        mIvSettings = (ImageView)findViewById(R.id.main_settings_outmost);
        mUnlockBar = (UnlockBar)findViewById(R.id.main_unlock_bar);
        mTvTime = (TextView)findViewById(R.id.main_tv_time);
        mTvDate = (TextView)findViewById(R.id.main_tv_date);
        mIvAppIconAnim = (ImageView)findViewById(R.id.main_appicon_startactiv);

        loadCustomFont();
        mGesutreOverLayView.addOnGesturePerformedListener(mOnGesutrePerformedListener);
        mIvSettings.setOnClickListener(this);
        mUnlockBar.setTargetViewGroup(mOutmostLayout);
        mUnlockBar.setOnUnlockListener(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlockConditionFulfilled() {
                finish();
            }
        });

        mTimeChangeReceiver = new TimeChangeReceiver();
        mScreenHeight = ScreenUtil.getScreenSize(this)[1];

        // show welcome page after 1000ms
        mOutmostLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                WelcomeActivity.startWelcomeIfNeeded(MainActivity.this);
            }
        }, 1000L);
    }

    private void loadCustomFont(){
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Fontfabric.otf");
        mTvTime.setTypeface(typeFace);
        mTvDate.setTypeface(typeFace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGesutreListLayout.setAutoRefresh(true);
        mUnlockBar.setAnimationEffects(true);
        mTimeChangeReceiver.register();
        AppBroadcastManager.sendLockerStartedBroadcast(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGesutreListLayout.setAutoRefresh(false);
        mUnlockBar.setAnimationEffects(false);
        mTimeChangeReceiver.unregister();
        AppBroadcastManager.sendLockerStoppedBroadcast(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.main_settings_outmost:
                mGesutreListLayout.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mGesutreListLayout.isShown()){
            mGesutreListLayout.hide();
        }
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

    private GestureOverlayView.OnGesturePerformedListener mOnGesutrePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
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
}
