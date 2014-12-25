package org.vliux.android.gesturecut.activity.add;

/**
 * Created by vliux on 12/25/14.
 */
class AddGestureEvent {
    public static enum EventType{
        GESTURE_ADDED
    }

    private EventType mType;

    public AddGestureEvent(EventType type){
        mType = type;
    }

    public EventType getType() {
        return mType;
    }
}
