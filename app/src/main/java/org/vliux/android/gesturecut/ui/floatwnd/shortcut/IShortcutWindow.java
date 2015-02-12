package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

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
    public void showOverlay();
    public void hideOverlay(boolean closeWindow);
    public void moveOverlay(int xDelta);
}
