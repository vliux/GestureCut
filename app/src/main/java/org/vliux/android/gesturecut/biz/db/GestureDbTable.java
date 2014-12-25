package org.vliux.android.gesturecut.biz.db;

import android.content.ComponentName;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.util.AppLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vliux on 4/9/14.
 */
public class GestureDbTable extends DbTable {
    private static final String TAG = GestureDbTable.class.getSimpleName();
    public static final String DB_COL_GESTURE_NAME_TEXT_1 = "gestureName";
    public static final String DB_COL_COMPONENT_NAME_TEXT_1 = "componentName";
    public static final String DB_COL_GESTURE_ICON_PATH_TEXT_1 = "iconPath";
    /*
     * determine the type of string stored in componentName,
     * valid values as defined in ResolvedType.
     */
    public static final String DB_COL_COMPONENT_TYPE_TEXT_1 = "componentType";
    //public static final String DB_COL_ADDED_TIME_TEXT_1 = "addedTime";
    //public static final String DB_COL_MODIFIED_TIME_TEXT_1 = "modifiedTime";

    protected GestureDbTable(DbManager.DbHelper helper) {
        super(helper);
    }

    @Override
    public void onTableCreated(SQLiteDatabase db) {
    }

    public boolean addGesture(String gestureName, ResolvedComponent resolvedComponent, String iconPath){
        ContentValues cv = new ContentValues();
        cv.put(DB_COL_GESTURE_NAME_TEXT_1, gestureName);
        switch (resolvedComponent.getType()){
            case COMPONENT_NAME:
                cv.put(DB_COL_COMPONENT_TYPE_TEXT_1, ResolvedComponent.ResolvedType.COMPONENT_NAME.name());
                cv.put(DB_COL_COMPONENT_NAME_TEXT_1, resolvedComponent.getComponentName().flattenToString());
                break;
            case PACKAGE_NAME:
                cv.put(DB_COL_COMPONENT_TYPE_TEXT_1, ResolvedComponent.ResolvedType.PACKAGE_NAME.name());
                cv.put(DB_COL_COMPONENT_NAME_TEXT_1, resolvedComponent.getPackageName());
                break;
        }
        cv.put(DB_COL_GESTURE_ICON_PATH_TEXT_1, iconPath);
        return insert(cv);
    }

    public boolean removeGesture(String gestureName){
        return super.delete(DB_COL_COMPONENT_NAME_TEXT_1 + "=?",
                new String[]{gestureName});
    }

    public Set<String> getGesturedPackageNames(){
        String[] columns = new String[]{
            DB_COL_COMPONENT_NAME_TEXT_1,
            DB_COL_COMPONENT_TYPE_TEXT_1
        };

        Set<String> packageNames = new HashSet<String>();
        ContentValues conditions = new ContentValues();
        Cursor cursor = __select(columns, conditions, null);
        if(null == cursor || cursor.getCount() <= 0){
            return packageNames;
        }

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            String componentName = cursor.getString(0);
            String componentType = cursor.getString(1);
            ResolvedComponent rc = ResolvedComponent.restoreResolvedComponent(componentName, componentType);
            if(null != rc){
                packageNames.add(rc.getPackageName());
            }
        }

        return packageNames;
    }

    public DbData getGesture(String gestureName){
        String[] columns = new String[]{
            DB_COL_GESTURE_ICON_PATH_TEXT_1,
            DB_COL_COMPONENT_NAME_TEXT_1,
            DB_COL_COMPONENT_TYPE_TEXT_1
        };

        ContentValues conditions = new ContentValues();
        conditions.put(DB_COL_GESTURE_NAME_TEXT_1, gestureName);
        Cursor cursor = __select(columns, conditions, null, true);
        if(null == cursor || cursor.getCount() <= 0){
            return null;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String iconPath = cursor.getString(0);
            String componentName = cursor.getString(1);
            if (TextUtils.isEmpty(componentName)) {
                continue;
            }

            String typeTxt = cursor.getString(2);
            ResolvedComponent resolvedComponent = ResolvedComponent.restoreResolvedComponent(componentName, typeTxt);
            if (null != resolvedComponent) {
                DbData dbData = new DbData();
                dbData.gestureName = gestureName;
                dbData.iconPath = iconPath;
                dbData.resolvedComponent = resolvedComponent;
                return dbData;
            }
        }
        return null;
    }

    /**
     *
     * @param packageName
     * @return names of the gestures which are removed.
     */
    public List<String> removeGesturesByPackage(String packageName){
        List<String> removedNames = new ArrayList<String>();
        List<Integer> removeIds = new ArrayList<Integer>();
        String[] columns = new String[]{
            PRIMARY_COLUMN_NAME,
            DB_COL_COMPONENT_NAME_TEXT_1,
            DB_COL_COMPONENT_TYPE_TEXT_1,
            DB_COL_GESTURE_NAME_TEXT_1,
        };

        Cursor cursor = __select(columns, null, null);
        if(null == cursor || cursor.getCount() <= 0){
            return removedNames;
        }else {
            cursor.moveToFirst();
            for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String componentName = cursor.getString(1);
                if (TextUtils.isEmpty(componentName)) {
                    continue;
                }

                ResolvedComponent.ResolvedType resolvedType =
                        ResolvedComponent.ResolvedType.valueOf(cursor.getString(2));
                String gestureName = cursor.getString(3);
                switch (resolvedType) {
                    case COMPONENT_NAME:
                        ComponentName cn = ComponentName.unflattenFromString(componentName);
                        if (packageName.equals(cn.getPackageName())) {
                            removeIds.add(id);
                            removedNames.add(gestureName);
                        }
                        break;
                    case PACKAGE_NAME:
                        if(packageName.equals(componentName)){
                            removeIds.add(id);
                            removedNames.add(gestureName);
                        }
                        break;
                }

            }
        }

        for(int id : removeIds){
            Log.d("vliux", "remove row primary id = " + id);
            delete(String.format("%s=?", PRIMARY_COLUMN_NAME),
                    new String[]{String.valueOf(id)});
        }

        return removedNames;
    }

    public static class DbData{
        public ResolvedComponent resolvedComponent;
        public String iconPath;
        public String gestureName;
    }
}
