package org.vliux.android.gesturecut.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.view.GestureList;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    private GestureOverlayView mGesutreOverLayView;
    private GestureList mGesutreListLayout;
    private ImageView mIvSettings; //outmost settings btn
    private ImageView mIvUnlock; //unlock imageview at bottom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyGuardFlags();
        setContentView(R.layout.activity_main);
        mGesutreOverLayView = (GestureOverlayView)findViewById(R.id.main_gesture_overlay);
        mGesutreListLayout = (GestureList)findViewById(R.id.main_gesture_list_layout);
        mIvSettings = (ImageView)findViewById(R.id.main_settings_outmost);
        mIvUnlock = (ImageView)findViewById(R.id.main_unlock_iv);

        mGesutreOverLayView.addOnGesturePerformedListener(mOnGesutrePerformedListener);
        mIvSettings.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGesutreListLayout.setAutoRefresh(true);
        playUnlockAnim(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGesutreListLayout.setAutoRefresh(false);
        playUnlockAnim(false);
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
        }else {
            super.onBackPressed();
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
                Toast.makeText(getApplicationContext(),
                        getString(R.string.start_activity_from_gesture),
                        Toast.LENGTH_SHORT).show();
                resolvedComponent.startActivity(getApplicationContext());
            }
        }
    };

    private void playUnlockAnim(boolean play){
        AnimationDrawable animationDrawable = (AnimationDrawable)mIvUnlock.getDrawable();
        if(play){
            animationDrawable.start();
        }else{
            animationDrawable.stop();
        }
    }
}
