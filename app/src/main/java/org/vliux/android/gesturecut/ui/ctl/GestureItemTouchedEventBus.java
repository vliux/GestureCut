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
        public void onEventMainThread(TouchedEvent touchedEvent);
    }

    public static class TouchedEvent{
        private View view;
        private boolean isTouchDown;
        private int secondsLeft;

        public static TouchedEvent newTouchDownEvent(View targetView, int secLeft){
            return new TouchedEvent(targetView, true, secLeft);
        }

        public static TouchedEvent newTouchUpEvent(View targetView){
            return new TouchedEvent(targetView, false, 0);
        }

        private TouchedEvent(View targetView, boolean isDown, int secLeft){
            view = targetView;
            isTouchDown = isDown;
            secondsLeft = secLeft;
        }


        public View getView() {
            return view;
        }

        public boolean isTouchDown() {
            return isTouchDown;
        }

        public int getSecondsLeft(){
            return secondsLeft;
        }
    }
}
