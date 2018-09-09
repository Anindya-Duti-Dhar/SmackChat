package base.droidtool.database;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;



public interface IRepository<T> {

    String getCreateSQL(String primaryKey, boolean isAutoIncremental);

    String add(T item);

    void add(ArrayList<T> items);

    void update(T item, String where, String[] whereArgs);

    void remove(String where, String[] whereArgs);

    void removeAll();

    void removeAll(String sWhere);

    JSONArray getAllJsonArray();

    String getAllJsonString(String rootKey, String orderBy);

    Object getAll(String orderBy, Class<?> cls);

    Object getAll(String where, String orderBy, Class<?> cls);

    Object get(String where, String orderBy, Class<?> cls);

    String getFiledValue(String fieldName, String sql);

    public int getCountAgainstField(String fieldName, String fieldValue);

    public List<String> getListOfNotSyncedUid(String fieldName, long recordId);

    void updateMasterSyncStatus(long recordId, long serverRecordId);

    public void updateDetailsSyncStatus(long recordId);

    boolean isDataSynced(String fieldName, String fieldValue);

    int getNotSyncDataCount();

    int getAllDataCount();

    int getMaxRecordIdValue(String fieldName);

    String getLastNotUsedId(String fieldName);

    void updateIdStatus(String targetField, String targetStringValue, int integerValue);

}