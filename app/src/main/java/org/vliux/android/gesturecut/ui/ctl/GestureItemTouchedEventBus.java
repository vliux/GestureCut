package org.vliux.android.gesturecut.ui.ctl;

import android.view.View;

import org.vliux.android.gesturecut.biz.ResolvedComponent;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 6/18/14.
 * EventBus to notify that an item in the SimplifiedGestureListView is touched down or up.
 */
public class GestureItemTouchedEventBus {
    private static EventBus sEventBus;

    private static EventBus getEventBus(){
        if(null == sEventBus){
            synchronized (GestureItemTouchedEventBus.class){
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
        private View view;
        private EventType eventType;
        private ResolvedComponent resolvedComponent;

        public enum EventType{
            ACTION_UP,
            ACTION_DOWN,
            START_TASK
        }

        public static TouchedEvent newTouchDownEvent(View targetView){
            return new TouchedEvent(targetView, EventType.ACTION_DOWN, null);
        }

        public static TouchedEvent newTouchUpEvent(View targetView){
            return new TouchedEvent(targetView, EventType.ACTION_UP, null);
        }

        public static TouchedEvent newStartTaskEvent(ResolvedComponent rc){
            return new TouchedEvent(null, EventType.START_TASK, rc);
        }

        private TouchedEvent(View targetView, EventType type, ResolvedComponent rc){
            view = targetView;
            eventType = type;
            resolvedComponent = rc;
        }

        public View getView() {
            return view;
        }

        public EventType getEventType() {
            return eventType;
        }

        public ResolvedComponent getResolvedComponent() {
            return resolvedComponent;
        }
    }
}
