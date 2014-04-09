package org.vliux.android.gesturecut.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends BaseActivity {
    private GestureOverlayView mGesutreOverLayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyGuardFlags();
        setContentView(R.layout.activity_main);

        mGesutreOverLayView = (GestureOverlayView)findViewById(R.id.main_gesture_overlay);
        mGesutreOverLayView.setGestureColor(Color.BLUE);
        mGesutreOverLayView.addOnGesturePerformedListener(mOnGesutrePerformedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view){
        switch (view.getId()){

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
            ComponentName componentName = GesturePersistence.loadGesture(getApplicationContext(), gesture);
            if(null != componentName){
                Toast.makeText(getApplicationContext(),
                        getString(R.string.start_activity_from_gesture),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setComponent(componentName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    };
}
