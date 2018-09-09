package base.droidtool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static anindya.sample.smackchat.utils.Const.DATABASE_VERSION;
import static anindya.sample.smackchat.utils.Const.DB_NAME;
import static base.droidtool.config.Constants.mRecordId;
import static base.droidtool.config.Constants.mServerRecordId;
import static base.droidtool.config.Constants.mStatus;
import static base.droidtool.config.Constants.mSyncedValue;



public class Repository<T> extends SQLiteOpenHelper implements IRepository<T> {

    T object;
    Context mContext;
    public String mTableName = "";
    private String primaryKeyField = "";
    String mPKField = "";
    List<String> excludeFields = new ArrayList<>();
    HashMap<String, String> tableMap = new HashMap<>();

    public Repository(Context context, T object) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        mContext = context;
        this.object = object;

        mTableName = object.getClass().getName().toString().replace(object.getClass()
                .getPackage().toString().replace("package ", ""), "").replace(".", "");
    }

    public String getTableName() {
        return mTableName;
    }

    //if class name is not same as table name.. so need to map..
    public void addInCustomTableMap(String className, String tableName) {
        tableMap.put(className, tableName);
    }

    public List<String> getExcludeFields() {
        return excludeFields;
    }

    public void addExcludeFields(String excludeField) {
        this.excludeFields.add(excludeField);
    }


    private String getPrimaryKey() {
        SQLiteDatabase db = this.getWritableDatabase();
        String fieldName = "";
        Cursor c = null;
        try {
            c = db.rawQuery("pragma table_info(" + mTableName + ");", null);
            while (c != null && c.moveToNext()) {
                int fieldValue = c.getInt(c.getColumnIndex("pk"));
                if (fieldValue == 1) fieldName = c.getString(c.getColumnIndex("name"));
                break;
            }
            c.close();
            primaryKeyField = fieldName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return primaryKeyField;
    }

    private boolean isAutoIncrement() {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isAuto = false;
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) yes FROM sqlite_master WHERE tbl_name='" + mTableName + "' AND sql LIKE '%AUTOINCREMENT%'", null);
            while (c != null && c.moveToNext()) {
                int fieldValue = c.getInt(c.getColumnIndex("yes"));
                if (fieldValue == 1) isAuto = true;
                break;
            }
            c.close();
        } catch (Exception e) {
            c.close();
            e.printStackTrace();
        }
        db.close();
        return isAuto;
    }


    //region Create Table SQL
    public String create(String primaryKey, boolean isAutoIncremental) {
        String sql = "", fieldName = "", fieldType = "";
        String s = object.toString();
        String s2 = s.substring(s.indexOf("{"));
        String[] objectToString = s2.replace("{", "").replace("}", "").split(",");
        for (int i = 0; i < objectToString.length; i++) {
            fieldName = objectToString[i].split("=")[0];
            fieldType = getFieldType(object, fieldName);
            if (!TextUtils.isEmpty(fieldType)) {
                if (fieldName.equals(primaryKey)) {
                    fieldName = getAutoIncrementalPrimaryKey(fieldName + " " + fieldType, isAutoIncremental);
                    sql = sql + fieldName + ", ";
                } else {
                    sql = sql + fieldName + " " + fieldType + ", ";
                }
            }
        }
        String finalStatement = "CREATE TABLE IF NOT EXISTS " + mTableName + "(" + sql.substring(0, sql.length() - 2) + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(finalStatement);
        db.close();
        return finalStatement;
    }

    private String getAutoIncrementalPrimaryKey(String primaryKeyFieldName, boolean isAutoIncremental) {
        if (isAutoIncremental) {
            primaryKeyField = primaryKeyFieldName;
            return primaryKeyFieldName + " PRIMARY KEY AUTOINCREMENT";
        } else return primaryKeyFieldName + " PRIMARY KEY";
    }

    private String getFieldType(Object obj, String fieldName) {
        String fieldTypeName = "";
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName.trim());
            fieldTypeName = getSqlFieldType(field.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return fieldTypeName;
    }

    private String getSqlFieldType(String s) {
        String type = "";
        if (s.contains("String")) type = " TEXT";
        else if (s.contains("long")) type = " INTEGER";
        else if (s.contains("int")) type = " INTEGER";
        else type = "";
        return type;
    }
    //endregion

    public String getCreateSQL(String primaryKey, boolean isAutoIncremental) {
        String sql = "", fieldName = "", fieldType = "";
        String s = object.toString();
        String s2 = s.substring(s.indexOf("{"));
        String[] objectToString = s2.replace("{", "").replace("}", "").split(",");
        for (int i = 0; i < objectToString.length; i++) {
            fieldName = objectToString[i].split("=")[0];
            fieldType = getFieldType(object, fieldName);
            if (!TextUtils.isEmpty(fieldType)) {
                if (fieldName.equals(primaryKey)) {
                    fieldName = getAutoIncrementalPrimaryKey(fieldName + " " + fieldType, isAutoIncremental);
                    sql = sql + fieldName + ", ";
                } else {
                    sql = sql + fieldName + " " + fieldType + ", ";
                }
            }
        }
        String finalStatement = "CREATE TABLE IF NOT EXISTS " + mTableName + "(" + sql.substring(0, sql.length() - 2) + ")";
        return finalStatement;
    }


    @Override
    public String add(T item) {

        getPrimaryKey();

        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        for (Field field : item.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields

            try {
                if (!field.getName().toString().equalsIgnoreCase(mPKField)) {
                    String fieldName = field.getName().toString();
                    if (fieldName.equalsIgnoreCase("$change")) {
                    } else if (fieldName.equalsIgnoreCase("serialVersionUID")) {
                    } else if (excludeFields.contains(fieldName)) {
                    } else {
                        if (!field.getName().toString().equals(primaryKeyField))
                            values.put(field.getName().toString(), field.get(item) != null ? field.get(item).toString() : "");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        long val = db.insert(mTableName, null, values);

        String sql = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) cursor.moveToFirst();
        String sId = cursor.getString(0);
        db.close();
        return sId;
    }

    @Override
    public void add(ArrayList<T> items) {
        for (T o : items) {
            add(o);
        }
    }

    @Override
    public void update(T item, String where, String[] whereArgs) {

        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        for (Field field : item.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields

            try {
                String fieldName = field.getName().toString();
                if (fieldName.equalsIgnoreCase("$change")) {
                } else if (fieldName.equalsIgnoreCase("serialVersionUID")) {
                } else if (excludeFields.contains(fieldName)) {
                } else
                    values.put(field.getName().toString(), field.get(item) != null ? field.get(item).toString() : "");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        db.update(mTableName, values, where, whereArgs);
        db.close();
    }

    @Override
    public void remove(String where, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(mTableName, where, whereArgs);
        db.close();
    }

    @Override
    public void removeAll(String sWhere) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (sWhere != "") {
            db.execSQL("DELETE FROM " + mTableName + " WHERE " + sWhere);
        } else {
            db.execSQL("DELETE FROM " + mTableName);
        }
        db.close();
    }

    private JSONArray getJSON(Cursor cursor) {

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {

                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("repo: ", resultSet.toString());
        return resultSet;
    }

    @Override
    public JSONArray getAllJsonArray() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + mTableName;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) cursor.moveToFirst();

        JSONArray jArr = getJSON(cursor);

        return jArr;
    }

    @Override
    public String getAllJsonString(String rootKey, String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + mTableName + " " + orderBy;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) cursor.moveToFirst();

        JSONArray jArr = getJSON(cursor);
        String jsonStr = jArr.toString();

        if (rootKey != "") jsonStr = rootKey + " : {" + jsonStr + "}";

        return jsonStr;
    }

    @Override
    public Object getAll(String orderBy, Class<?> cls) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + mTableName + " " + orderBy;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        JSONArray jArr = getJSON(cursor);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Object obj = gson.fromJson(jArr.toString(), cls);
        db.close();

        return obj;
    }

    @Override
    public Object getAll(String where, String orderBy, Class<?> cls) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + mTableName + " " + where + " " + orderBy;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        JSONArray jArr = getJSON(cursor);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Object obj = gson.fromJson(jArr.toString(), cls);
        db.close();

        return obj;
    }

    @Override
    public Object get(String where, String orderBy, Class<?> cls) {

/*        SQLiteDatabase db;

        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            db = dbHelper.getWritableDatabase();
        } else {
            db = this.getReadableDatabase();
        }*/

        SQLiteDatabase db = this.getReadableDatabase();

        if (!TextUtils.isEmpty(tableMap.get(mTableName))) {
            mTableName = tableMap.get(mTableName);
        }

        String selectQuery = "SELECT * FROM " + mTableName + " WHERE " + where + " " + orderBy;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        JSONArray jArr = getJSON(cursor);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Object obj = gson.fromJson(jArr.toString(), cls);

/*        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            dbHelper.close();
        } else {
            db.close();
        }*/

        db.close();

        return obj;
    }

    @Override
    public String getFiledValue(String fieldName, String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) cursor.moveToFirst();
        String _fieldName = cursor.getString(cursor.getColumnIndex(fieldName));
        db.close();
        return _fieldName;
    }

    @Override
    public int getCountAgainstField(String fieldName, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + mTableName + " where " + fieldName + " = '" + fieldValue + "'";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    @Override
    public boolean isDataSynced(String fieldName, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + mTableName + " where " + fieldName + " = '" + fieldValue + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) cursor.moveToFirst();
        int value = cursor.getInt(cursor.getColumnIndex(mStatus));
        db.close();
        if(value==1) return true;
        else return false;
    }

    public long getRecordCount(String where, int move) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) count from " + mTableName + " where " + where, null);
        if (cursor != null) cursor.moveToFirst();
        String rowCoount = cursor.getString(cursor.getColumnIndex("count"));
        db.close();
        return Long.parseLong(rowCoount);
    }

    public long getRecordCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) count from " + mTableName, null);
        if (cursor != null) cursor.moveToFirst();
        String _fieldName = cursor.getString(cursor.getColumnIndex("count"));
        db.close();
        return Long.parseLong(_fieldName);
    }

    public boolean isRecordExists(String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) count from " + mTableName + " where " + where, null);
        if (cursor != null) cursor.moveToFirst();
        long _fieldVal = cursor.getLong(cursor.getColumnIndex("count"));
        db.close();
        if (_fieldVal == 0) return false;
        else return true;
    }

    public int getRecordMaxValue(String fieldName, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select max(cast(" + fieldName + " as integer)) maxid from " + mTableName + " where " + where, null);
        if (cursor != null) cursor.moveToFirst();
        int _val = cursor.getInt(cursor.getColumnIndex("maxid"));
        db.close();
        return _val;
    }

    public long getRecordMaxValue(String fieldName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(" + fieldName + ") maxid from " + mTableName, null);
        if (cursor != null) cursor.moveToFirst();
        long _val = cursor.getLong(cursor.getColumnIndex("maxid"));
        db.close();
        return _val;
    }

    @Override
    public void updateMasterSyncStatus(long recordId, long serverRecordId) {

/*        SQLiteDatabase db;

        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            db = dbHelper.getWritableDatabase();
        } else {
            db = this.getReadableDatabase();
        }*/

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(mStatus, 1);
        if (serverRecordId != 0) cv.put(mServerRecordId, serverRecordId);
        db.update(mTableName, cv, mRecordId + "=" + recordId, null);

/*        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            dbHelper.close();
        } else {
            db.close();
        }*/

        db.close();
    }

    @Override
    public void updateDetailsSyncStatus(long recordId) {

/*        SQLiteDatabase db;

        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            db = dbHelper.getWritableDatabase();
        } else {
            db = this.getReadableDatabase();
        }*/

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(mStatus, 1);
        db.update(mTableName, cv, mRecordId + "=" + recordId, null);

/*        if (isExternalDatabase(mContext)) {
            DbHelperExternal dbHelper = new DbHelperExternal(mContext);
            dbHelper.close();
        } else {
            db.close();
        }*/

        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    // area duti start

    @Override
    public int getNotSyncDataCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + mTableName + " where " + mStatus + " != " + mSyncedValue;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    @Override
    public int getAllDataCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + mTableName;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(mTableName, null, null);
        db.close();
    }

    @Override
    public int getMaxRecordIdValue(String fieldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select max(" + fieldName + ") maxid from " + mTableName, null);
        if (cursor != null) cursor.moveToFirst();
        int value = cursor.getInt(cursor.getColumnIndex("maxid"));
        db.close();
        return value;
    }

    @Override
    public String getLastNotUsedId(String fieldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + mTableName + " where " + mStatus + " != '" + mSyncedValue + "'" + " order by " + mRecordId + " asc limit 1", null);
        if (cursor != null) cursor.moveToFirst();
        String value = cursor.getString(cursor.getColumnIndex(fieldName));
        db.close();
        return value;
    }

    @Override
    public void updateIdStatus(String targetField, String targetStringValue, int integerValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(mStatus, integerValue);
        db.update(mTableName, contentValues, targetField + " = ?", new String[]{targetStringValue});
        db.close();
    }

    public List<String> getListOfNotSyncedUid(String fieldName, long recordId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> modelList = new ArrayList<String>();
        String query = "select * from " + mTableName + " where " + mRecordId + " = '" + recordId + "'" + " and " + mStatus + "=" + mSyncedValue;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    modelList.add(cursor.getString(cursor.getColumnIndex(fieldName)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return modelList;
    }



    //region Alter Table
    public void alterFieldAdd(String afterFieldName, String newFieldName, String defaultValue) {
        String oldFiledStruct = "";
        List<String> list = Arrays.asList(getFieldNames().split(","));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(afterFieldName)) {  //loop afterFieldName for old versions
                oldFiledStruct = oldFiledStruct + list.get(i);
                oldFiledStruct = oldFiledStruct + defaultValue;
            } else oldFiledStruct = oldFiledStruct + list.get(i);
        }
        alterTable(oldFiledStruct);
    }

    public void alterFieldRename(String oldVersions, String currentVersion, String oldfieldNames, String newFieldName) {

        String[] oldVar = oldVersions.split(",");
        String[] oldFields = oldfieldNames.split(",");
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < oldVar.length ; i++) {
            map.put(Integer.parseInt(oldVar[i].trim()), oldFields[i].trim());
        }
        String oldfieldName = map.get(Integer.parseInt(currentVersion));

        String oldFiledStruct = "";
        List<String> list = Arrays.asList(getFieldNames().split(","));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(newFieldName)) {   //loop newFieldName for old versions
                oldFiledStruct = oldFiledStruct + oldfieldName;
            } else oldFiledStruct = oldFiledStruct + list.get(i);
        }
        alterTable(oldFiledStruct);
    }

    public void alterFieldRemoveUpdate() {
        alterTable(getFieldNames());
    }

    private void alterTable(String oldSelectFieldsString) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlBeginTrans = "BEGIN TRANSACTION;";
        String sqlRenameTable = "ALTER TABLE " + mTableName + " RENAME TO temp_" + mTableName + ";";

        String sqlCreateTable = getCreateSQL(getPrimaryKey(), isAutoIncrement());

        String oldTableFields = oldSelectFieldsString;

        String sqlInsertInToTable = "INSERT INTO " + mTableName + " SELECT " + oldTableFields + " FROM temp_" + mTableName + ";";

        String sqlDropTable = "DROP TABLE temp_" + mTableName + ";";
        String sqlCommit = "COMMIT;";

        db.execSQL(sqlBeginTrans);

        db.execSQL(sqlRenameTable);
        db.execSQL(sqlCreateTable);
        db.execSQL(sqlInsertInToTable);
        db.execSQL(sqlDropTable);

        db.execSQL(sqlCommit);

        db.close();
    }

    private String getFieldNames() {
        String sql = "", fieldName = "", fieldType = "";
        String s = object.toString();
        String s2 = s.substring(s.indexOf("{"));
        String[] objectToString = s2.replace("{", "").replace("}", "").split(",");
        for (int i = 0; i < objectToString.length; i++) {
            fieldName = objectToString[i].split("=")[0];
            fieldType = getFieldType(object, fieldName);
            if (!TextUtils.isEmpty(fieldType)) {
                sql = sql + fieldName + ", ";
            }
        }
        return sql.substring(0, sql.length() - 1);
    }
    //endregion

}