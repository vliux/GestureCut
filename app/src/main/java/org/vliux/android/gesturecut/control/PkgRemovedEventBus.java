package org.vliux.android.gesturecut.control;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 6/30/14.
 */
public class PkgRemovedEventBus {
    private static PkgRemovedEventBus sInstance;
    private EventBus mEventBus;

    public static PkgRemovedEventBus getInstance(){
        if(null == sInstance){
            synchronized (PkgRemovedEventBus.class){
                if(null == sInstance){
                    sInstance = new PkgRemovedEventBus();
                }
            }
        }
        return sInstance;
    }

    private PkgRemovedEventBus(){
        mEventBus = new EventBus();
    }

    public void register(PkgRemovedHandler handler){
        mEventBus.register(handler);
    }

    public void unregister(PkgRemovedHandler handler){
        mEventBus.unregister(handler);
    }

    public void post(PkgRemovedEvent event){
        mEventBus.post(event);
    }

    public static class PkgRemovedEvent{
    }

    public static interface PkgRemovedHandler{
        public void onEventMainThread(PkgRemovedEvent event);
    }
}
