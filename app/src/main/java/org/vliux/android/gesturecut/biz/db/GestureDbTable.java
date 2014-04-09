package org.vliux.android.gesturecut.biz.db;

import android.content.ContentValues;
import android.database.Cursor;
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

    public ContentValues getGesture(String gestureName){
        String[] columns = new String[]{
            DB_COL_GESTURE_ICON_PATH_TEXT_1,
            DB_COL_COMPONENT_NAME_TEXT_1
        };

        ContentValues conditions = new ContentValues();
        conditions.put(DB_COL_GESTURE_NAME_TEXT_1, gestureName);
        Cursor cursor = __select(columns, conditions, null, true);
        if(null == cursor || cursor.getCount() <= 0){
            return null;
        }else{
            ContentValues result = new ContentValues();
            cursor.moveToFirst();
            for(; !cursor.isAfterLast(); cursor.moveToNext()){
                result.put(DB_COL_GESTURE_ICON_PATH_TEXT_1, cursor.getString(0));
                result.put(DB_COL_COMPONENT_NAME_TEXT_1, cursor.getString(1));
                result.put(DB_COL_GESTURE_NAME_TEXT_1, gestureName);
                break;
            }
            return result;
        }
    }
}
