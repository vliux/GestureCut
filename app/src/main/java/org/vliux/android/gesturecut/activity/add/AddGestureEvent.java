package org.vliux.android.gesturecut.activity.add;

/**
 * Created by vliux on 12/25/14.
 */
class AddGestureEvent {
    public static enum EventType{
        GESTURE_ADDED,
        TAB_CHANGED // system app, user app
    }

    private EventType mType;

    public AddGestureEvent(EventType type){
        mType = type;
    }

    public EventType getType() {
        return mType;
    }
}
