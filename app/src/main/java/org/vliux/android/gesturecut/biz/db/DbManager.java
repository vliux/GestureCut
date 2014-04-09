package org.vliux.android.gesturecut.biz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.util.AppLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vliux on 4/9/14.
 */
public class DbManager {
    private static final String TAG = DbManager.class.getSimpleName();

    private static DbManager sInstance;
    private HashMap<Class, DbTable> mDbTables;
    private DbHelper mDbHelper;

    /**
     * Must call init() first.
     * @param appContext
     */
    public static void init(Context appContext){
        sInstance = new DbManager(appContext);
    }

    /**
     * Must call init() before getInstance().
     * @return
     */
    public static DbManager getInstance(){
        return sInstance;
    }

    private DbManager(Context context){
        mDbHelper = new DbHelper(context);
        mDbTables = new HashMap<Class, DbTable>();
        initDbTables();
    }

    private void initDbTables(){
        mDbTables.put(GestureDbTable.class, new GestureDbTable(mDbHelper));
    }

    public Collection<DbTable> getDbTables(){
        return mDbTables.values();
    }

    /**
     * Return instance of DbTable according to the class.
     * @param tableClass
     * @return
     */
    public DbTable getDbTable(Class tableClass){
        return mDbTables.get(tableClass);
    }

    /**
     *  DBHelper class
     */
    public static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, AppConstant.DbTables.DB_NAME, null, AppConstant.DbTables.DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            AppLog.logd(TAG, "table create");
            for (DbTable table : DbManager.getInstance().getDbTables()) {
                String sql = table.getCreateSql();
                if (null != sql && sql.length() > 0) {
                    sqLiteDatabase.execSQL(sql);
                    table.onTableCreated(sqLiteDatabase);
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
            AppLog.logd(TAG, "table upgrade");
            for (DbTable table : DbManager.getInstance().getDbTables()) {
                String sql = table.getUpdateSql(oldVer, newVer);
                AppLog.logd(TAG, "table " + sql);
                if (null != sql && sql.length() > 0) {
                    sqLiteDatabase.execSQL(sql);
                }
            }
        }

    }

}
