package org.vliux.android.gesturecut;

/**
 * Created by vliux on 4/3/14.
 */
public class AppConstant {

    public static class Gestures{
        public static final double MIN_GESTURE_MATCH_SCORE = 1.0;
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
    }
}
