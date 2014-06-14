package org.vliux.android.gesturecut;

/**
 * Created by vliux on 4/3/14.
 */
public class AppConstant {

    public static class LockScreen{
        public static final float MIN_UNLOCK_FLOING_VELOCITY = -100.0f;
    }

    public static class Gestures{
        public static final double MIN_GESTURE_MATCH_SCORE = 2.0;
    }

    public static class DbTables{
        public static final String DB_NAME = "gesturecut.db";
        public static final int DB_VER = 1;
    }

    public static class GestureStorage{
        public static final String GESTURE_ICON_DIR_NAME = "gesture_icon";
    }

    public static class LocalBroadcasts{
        public static final String BROADCAST_GESTURE_ADDED = "org.vliux.android.gesturecut.GESTURE_ADDED";
        public static final String BROADCAST_GESTURE_DELELTED = "org.vliux.android.gesturecut.GESTURE_DELETED";

        public static final String BROADCAST_LOCKER_STARTED = "org.vliux.android.gesturecut.LOCKER_STARTED";
        public static final String BROADCAST_LOCKER_STOPPED = "org.vliux.android.gesturecut.LOCKER_STOPED";
    }

    public static class Anim{
        public static final long ANIM_DURATION_NORMAL = 300L;
        public static final long ANIM_DURATION_LONGER = 500L;
    }
}
