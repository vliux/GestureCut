package org.vliux.android.gesturecut.biz.db;

/**
 * Created by vliux on 9/21/13.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.vliux.android.gesturecut.util.AppLog;

public abstract class DbTable{
    protected final String PRIMARY_COLUMN_NAME = "_id";
    protected final String DB_COLUMN_PREFIX = "DB_COL_";
    protected final int DB_COLUMN_SECTIONS = 4;
    protected HashMap<String, DbColumnDef> mColumnDefinitions = new HashMap<String, DbColumnDef>();
    protected DbManager.DbHelper mDbHelper;

    protected DbTable(DbManager.DbHelper helper) {
        mDbHelper = helper;
        initColumns();
    }

    protected void initColumns() {
        Field[] fields = this.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            if (fieldName.startsWith(DB_COLUMN_PREFIX)) {
                String[] sects = fieldName.split("_");
                if (null == sects || sects.length < DB_COLUMN_SECTIONS) {
                    String msgError = String.format(Locale.US, "DB column definition %s is not in valid format in %s", fieldName, getTableName());
                    AppLog.loge(getTableName(), msgError);
                    throw new SQLiteException(msgError);
                }
                Integer dbVer = Integer.valueOf(sects[sects.length - 1]);
                String dbType = sects[sects.length - 2];
                String dbColumnName = null;
                try {
                    dbColumnName = (String) fields[i].get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new SQLiteException(String.format(Locale.US, "Unable to get column name for %s in %s", fieldName, getTableName()));
                }
                if (null != dbColumnName && null != dbType) {
                    DbColumnDef columnDef = new DbColumnDef();
                    columnDef.setColumn(dbColumnName);
                    columnDef.setIntroducedVersion(dbVer.intValue());
                    columnDef.setType(dbType);
                    mColumnDefinitions.put(dbColumnName, columnDef);
                }
            }
        }
    }

    public String getTableName() {
        return getClass().getSimpleName();
    }

    public String getCreateSql() {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + getTableName() +
                String.format(" (%s INTEGER DEFAULT '0' NOT NULL PRIMARY KEY AUTOINCREMENT", PRIMARY_COLUMN_NAME));
        for (String colName : mColumnDefinitions.keySet()) {
            DbColumnDef colDef = mColumnDefinitions.get(colName);
            sb.append(String.format(Locale.US,
                    ",%s %s DEFAULT '%s'", colDef.getColumn(), colDef.getType(), colDef.getDefaultValue()));
        }
        sb.append(")");
        AppLog.logi(getTableName(), sb.toString());
        return sb.toString();
    }

    protected String getUpdateSql(int oldVer, int newVer) {
        return null;
    }

    public  abstract void onTableCreated(SQLiteDatabase db);

    protected boolean insert(SQLiteDatabase sqliteDb, ContentValues cv){
        try{
            sqliteDb.beginTransaction();
            long ret = sqliteDb.insert(getTableName(), null, cv);
            if (ret != -1L) {
                sqliteDb.setTransactionSuccessful();
            }
            return (-1L != ret ? true : false);
        }catch(SQLiteException e){
            e.printStackTrace();
            throw e;
        }finally {
            if(null != sqliteDb){
                sqliteDb.endTransaction();
            }
        }
    }

    protected boolean insert(ContentValues cv) {
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            long ret = sqliteDb.insert(getTableName(), null, cv);
            if (ret != -1L) {
                sqliteDb.setTransactionSuccessful();
            }
            return (-1L != ret ? true : false);
        } catch (SQLiteException ex) {
            AppLog.loge(getTableName(), ex.toString());
            throw ex;
        } finally {
            if (null != sqliteDb) {
                sqliteDb.endTransaction();
            }
        }
    }

    protected boolean update(ContentValues cv, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            int ret = sqliteDb.update(getTableName(), cv, whereClause, whereArgs);
            if (ret > 0) {
                sqliteDb.setTransactionSuccessful();
            }
            return (0 < ret ? true : false);
        } catch (SQLiteException ex) {
            AppLog.loge(getTableName(), ex.toString());
            throw ex;
        } finally {
            if (null != sqliteDb) {
                sqliteDb.endTransaction();
            }
        }
    }

    protected boolean delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase sqliteDb = null;
        try {
            sqliteDb = mDbHelper.getWritableDatabase();
            sqliteDb.beginTransaction();
            int ret = sqliteDb.delete(getTableName(), whereClause, whereArgs);
            if (ret > 0) {
                sqliteDb.setTransactionSuccessful();
            }
            return (0 < ret ? true : false);
        } catch (SQLiteException ex) {
            AppLog.loge(getTableName(), ex.toString());
            throw ex;
        } finally {
            if (null != sqliteDb) {
                sqliteDb.endTransaction();
            }
        }
    }

    protected Cursor __select(String[] columns, ContentValues cv, int[] limits) throws IllegalArgumentException{
        return __select(columns, cv, limits, false);
    }

    protected Cursor __select(String[] columns, ContentValues cv, int[] limits, boolean isDesc) throws IllegalArgumentException {
        // generate SQL WHERE clause and selectArgs
        String whereClause = "";
        ArrayList<String> selectArgs = new ArrayList<String>();
        if (null == cv || cv.size() <= 0) {
            whereClause = null;
        } else {
            for (Map.Entry<String, Object> entry : cv.valueSet()) {
                String key = entry.getKey();
                if (mColumnDefinitions.keySet().contains(key)) {
                    String clause = String.format("%s=?", key);
                    if (whereClause.length() > 0) {
                        whereClause += String.format(" AND %s", clause);
                    } else {
                        whereClause += clause;
                    }
                    Object object = entry.getValue();
                    String value = String.valueOf(object);
                    if(null == value){
                        value = "";
                    }
                    selectArgs.add(value);
                } else {
                    throw new IllegalArgumentException(String.format("'%s' is not a db column", key));
                }
            }
        }
        // generate LIMIT clause
        String limitClause;
        if (null == limits || limits.length <= 0) {
            limitClause = null;
        } else if (limits.length == 1) {
            limitClause = String.format("%d", limits[0]);
        } else {
            limitClause = String.format("%d, %d", limits[0], limits[1]);
        }

        SQLiteDatabase db = null;
        try {
            db = mDbHelper.getReadableDatabase();
            return db.query(getTableName(),
                    columns,
                    whereClause,
                    (null != whereClause ? selectArgs.toArray(new String[selectArgs.size()]) : null),
                    null,
                    null,
                    (isDesc? PRIMARY_COLUMN_NAME + " DESC" : PRIMARY_COLUMN_NAME + " ASC"),
                    limitClause);
        } catch (SQLiteException exp) {
            AppLog.loge(getTableName(), exp.toString());
            throw exp;
        } finally {
            // no need to close SQLiteDatabase as it is cached by Helper
            // if close it here, later use of the SQLiteDatabase obj will throw NullPointerException
            //if(null != db){
            //    db.close();
            //}
        }
    }

    protected class DbColumnDef {
        private String column;
        private String type;
        private String defaultValue = "";
        private int introducedVersion = -1;

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public int getIntroducedVersion() {
            return introducedVersion;
        }

        public void setIntroducedVersion(int introducedVersion) {
            this.introducedVersion = introducedVersion;
        }
    }
}

