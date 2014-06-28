package org.vliux.android.gesturecut.ui.ctl;

import android.view.View;
import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 6/28/14.
 */
public class GestureOverlayTouchedEventBus {
    private static EventBus sEventBus;

    private static EventBus getEventBus(){
        if(null == sEventBus){
            synchronized (GestureOverlayTouchedEventBus.class){
                if(null == sEventBus){
                    sEventBus = new EventBus();
                }
            }
        }
        return sEventBus;
    }

    public static void register(TouchedEventHandler eventHandler){
        getEventBus().register(eventHandler);
    }

    public static void unregister(TouchedEventHandler eventHandler){
        getEventBus().unregister(eventHandler);
    }

    public static void post(TouchedEvent touchedEvent){
        getEventBus().post(touchedEvent);
    }

    public interface TouchedEventHandler{
        public void onEventMainThread(TouchedEvent touchedEvent);
    }

    public static class TouchedEvent{
        private final View view;
        private final EventType eventType;

        public enum EventType{
            ACTION_UP,
            ACTION_DOWN
        }

        public static TouchedEvent newTouchDownEvent(View targetView){
            return new TouchedEvent(targetView, EventType.ACTION_DOWN);
        }

        public static TouchedEvent newTouchUpEvent(View targetView){
            return new TouchedEvent(targetView, EventType.ACTION_UP);
        }

        private TouchedEvent(View targetView, EventType type){
            view = targetView;
            eventType = type;
        }

        public View getView() {
            return view;
        }

        public EventType getEventType() {
            return eventType;
        }

    }
}
