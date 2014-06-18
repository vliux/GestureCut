package org.vliux.android.gesturecut.ui.view.gesturelistview.simplified;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.ui.ctl.GestureItemTouchedEventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vliux on 6/18/14.
 */
public class SimpListViewItem extends FrameLayout {
    private ImageView mAppIcon;
    private ImageView mGestureIcon;
    private ResolvedComponent mRelatedResolvedComponent;
    private GestureDetectorCompat mGestureDetector;

    private static final int SECONDS_LEFT_TO_START_TASK = 2;
    private Timer mCountDownTimer;

    public SimpListViewItem(Context context) {
        super(context);
        init();
    }

    public SimpListViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpListViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.item_gesture_simplified, this, true);
        mAppIcon = (ImageView)findViewById(R.id.item_simple_app_icon);
        mGestureIcon = (ImageView)findViewById(R.id.item_simple_ges_icon);
        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
    }

    public void setAppIcon(Drawable appDrawable){
        mAppIcon.setImageDrawable(appDrawable);
    }

    public void setGestureIcon(Bitmap gestureBmp){
        mGestureIcon.setImageBitmap(gestureBmp);
    }

    public void setResolvedComponent(ResolvedComponent rc){
        mRelatedResolvedComponent = rc;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(getResources().getColor(R.color.gesture_cur_blue_light_semi_transparent));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setBackgroundColor(getResources().getColor(R.color.gesture_cur_blue_transparent));
                if(null != mCountDownTimer){
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
                GestureItemTouchedEventBus.post(GestureItemTouchedEventBus.TouchedEvent.newTouchUpEvent(this));
                break;
        }
        return true;
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
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
            Log.d("vliux",
                    String.format("SimpListViewItem.onScroll(%s, %s, %f, %f)", e1.toString(), e2.toString(), distanceX, distanceY));
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (null == mCountDownTimer) {
                mCountDownTimer = new Timer(true);
            }
            mCountDownTimer.schedule(new LongPressTimerTask(SECONDS_LEFT_TO_START_TASK), 0L, 1000L);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    class LongPressTimerTask extends TimerTask{
        private int secondsLeft;

        public LongPressTimerTask(int secLeft){
            secondsLeft = secLeft;
        }

        @Override
        public void run() {
            if(secondsLeft > 0){
                GestureItemTouchedEventBus.post(GestureItemTouchedEventBus.TouchedEvent.newTouchDownEvent(SimpListViewItem.this, secondsLeft));
                secondsLeft--;
            }else{
                GestureItemTouchedEventBus.post(GestureItemTouchedEventBus.TouchedEvent.newTouchUpEvent(SimpListViewItem.this));
                mHandler.sendMessage(mHandler.obtainMessage(WHAT_CANCEL_TIMER));
                if(null != mRelatedResolvedComponent){
                    mRelatedResolvedComponent.startActivity(getContext());
                }
            }
        }
    }

    private static final int WHAT_CANCEL_TIMER = 100;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_CANCEL_TIMER:
                    if(null != mCountDownTimer){
                        mCountDownTimer.cancel();
                        mCountDownTimer = null;
                    }
                    break;
            }
        }
    };
}
