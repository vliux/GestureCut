package org.vliux.android.gesturecut.biz.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by vliux on 4/9/14.
 */
public class GestureDbTable extends DbTable {
    public static final String DB_COL_GESTURE_NAME_TEXT_1 = "gestureName";
    public static final String DB_COL_COMPONENT_NAME_TEXT_1 = "componentName";
    public static final String DB_COL_GESTURE_ICON_PATH_TEXT_1 = "iconPath";
    //public static final String DB_COL_ADDED_TIME_TEXT_1 = "addedTime";
    //public static final String DB_COL_MODIFIED_TIME_TEXT_1 = "modifiedTime";

    protected GestureDbTable(DbManager.DbHelper helper) {
        super(helper);
    }

    @Override
    public void onTableCreated(SQLiteDatabase db) {
    }

    public boolean addGesture(String gestureName, String componentString, String iconPath){
        ContentValues cv = new ContentValues();
        cv.put(DB_COL_GESTURE_NAME_TEXT_1, gestureName);
        cv.put(DB_COL_COMPONENT_NAME_TEXT_1, componentString);
        cv.put(DB_COL_GESTURE_ICON_PATH_TEXT_1, iconPath);
        return insert(cv);
    }
}
