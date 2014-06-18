package org.vliux.android.gesturecut.ui.ctl;

import android.view.View;

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
        public void onEvent(TouchedEvent touchedEvent);
    }

    public static class TouchedEvent{
        private View view;
        private boolean isTouchDown;

        public TouchedEvent(View targetView, boolean isDown){
            view = targetView;
            isTouchDown = isDown;
        }

        public View getView() {
            return view;
        }

        public boolean isTouchDown() {
            return isTouchDown;
        }
    }
}
