package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.view.View;

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
}
