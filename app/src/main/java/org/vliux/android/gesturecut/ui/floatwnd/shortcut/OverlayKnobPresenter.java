package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

/**
 * Created by vliux on 2/12/15.
 * Manager overlay movement by knob.
 */
class OverlayKnobPresenter {
    static final int EVENT_TYPE_KNOB_PRESSED = 1;
    static final int EVENT_TYPE_KNOB_UNPRESSED = 2;
    static final int EVENT_TYPE_KNOB_MOVE = 3;
    static class Event{
        public int eventType;
        public int xDelta;

        /*Event(){}
        Event(int eventType, int xDelta) {
            this.eventType = eventType;
            this.xDelta = xDelta;
        }*/
    }

    private IShortcutWindow mShortcutWindow;

    public OverlayKnobPresenter(IShortcutWindow scw){
        mShortcutWindow = scw;
    }

    public void onEventMainThread(Event event){
        switch (event.eventType){
            case EVENT_TYPE_KNOB_PRESSED:
                //Log.d(TAG, "KNOB_PRESSED received");
                mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.BY_KNOB);
                break;
            case EVENT_TYPE_KNOB_UNPRESSED:
                //Log.d(TAG, "KNOB_UNPRESSED received");
                mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.UNKNOWN);
                break;
            case EVENT_TYPE_KNOB_MOVE:
                //Log.d(TAG, "KNOB_MOVE received, xDelta = " + event.xDelta);
                mShortcutWindow.moveOverlay(event.xDelta);
                break;
        }
    }
}
