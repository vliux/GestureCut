package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.vliux.android.gesturecut.ui.ctl.GestureOverlayTouchedEventBus;

/**
 * Created by vliux on 6/28/14.
 * When user is ACTION_DOWN on this view, prompt the activity to show the mask layer through EventBus.
 */
public class LockScreenGestureOverlayView extends GestureOverlayView {
    public LockScreenGestureOverlayView(Context context) {
        super(context);
    }

    public LockScreenGestureOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockScreenGestureOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retValue = super.onTouchEvent(event);
        //Log.d("vliux", "LockScreenGestureOverlay.onTouchEvent(): " + event.getAction());
        //Log.d("vliux", "LockScreenGestureOverlay.onTouchEvent(): RETURN " + retValue);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                GestureOverlayTouchedEventBus.post(GestureOverlayTouchedEventBus.TouchedEvent.newTouchDownEvent(this));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                GestureOverlayTouchedEventBus.post(GestureOverlayTouchedEventBus.TouchedEvent.newTouchUpEvent(this));
                break;
        }
        return retValue;
    }
}
