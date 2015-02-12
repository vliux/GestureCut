package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by vliux on 2/12/15.
 */
class OverlayGesturePresenter extends GestureDetector.SimpleOnGestureListener{
    private IShortcutWindow mShortcutWindow;
    private int mVelocityXThreshold; // for showing, so it's negative
    private GestureDetectorCompat mGestureDetector;

    public OverlayGesturePresenter(Context context, IShortcutWindow scw, int velocityXThreshold){
        this.mShortcutWindow = scw;
        this.mVelocityXThreshold = velocityXThreshold;
        mGestureDetector = new GestureDetectorCompat(context, this);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                //Log.d(TAG, "onTouchEvent(), DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                /*Log.d(TAG, "onTouchEvent(), MOVE");
                Log.d(TAG, "  event.x = " + event.getX());
                Log.d(TAG, String.format("  knob.x=%.2f, y=%d, width=%d, height=%d", mKnob.getTranslationX(), mKnob.getTop(), mKnob.getWidth(), mKnob.getHeight()));*/
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //Log.d(TAG, "onTouchEvent(), CANCEL/UP");
                mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.UNKNOWN);
        }
        return true;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Log.d(TAG, "detector: velocityX = " + velocityX);
        if (velocityX < mVelocityXThreshold) { // mTouchSlop is negative
            mShortcutWindow.showOverlay();
        } else if (velocityX > -mVelocityXThreshold) {
            mShortcutWindow.hideOverlay(false);
        }
        return true;
    }

}
