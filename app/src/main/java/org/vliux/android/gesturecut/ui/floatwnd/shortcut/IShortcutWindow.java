package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.content.Context;
import android.view.View;

import org.vliux.android.gesturecut.util.ConcurrentManager;

/**
 * Created by vliux on 2/12/15.
 */
interface IShortcutWindow {
    public enum OverlayMoveMode{
        UNKNOWN,
        BY_KNOB,
        BY_GESTURE
    }

    public void setExclusiveMoveMode(OverlayMoveMode mode);
    public OverlayMoveMode getExclusiveMoveMode();

    public void showOverlay();
    public void hideOverlay(boolean closeWindow);

    public View getOverlayView();
    public void setGestureOverlayViewVisible(int visibility);
    public int getInitialTranslationX();
    public int getTargetTranslationX();
    public Context getContext();

    /**
     * Event send to OverlayKnob, notify it that the moving is stopped,
     * and the whether the knob stops at left/right side of the screen.
     */
    static class EventToKnob{
        public static final int END_STATE_LEFT = 1;
        public static final int END_STATE_RIGHT = 2;
        public static final int WND_CLOSING = 3;

        public int eventType;

        public EventToKnob(int eventType){
            this.eventType = eventType;
        }

    }
}
