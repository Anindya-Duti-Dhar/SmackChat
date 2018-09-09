package base.droidtool.config;

public class Constants {

    public static final String mContentType = "Content-Type";
    public static final String mApplicationType = "application/json";
    public static final int mSyncedValue = 1;

    public static final String mStatus = "Status";
    public static final String mMessage = "Message";
    public static final String mIsSuccess = "IsSuccess";
    public static final String mSyncQueryWhereClause = mStatus + " != " + mSyncedValue;

    public static final String mRecordId = "RecordId";
    public static final String mServerRecordId = "ServerRecordId";
    public static final String mRowId = "RowId";

    public static final int mLocalPageSize = 10;
    public static final int mServerPageSize = 10;
    public static final int mQueryLimit = 50;

    public static final String mSyncQueryOrderBy = "order by " + mRecordId + " Desc Limit " + mQueryLimit;
    public static final String mSyncQueryNoLimit = "order by " + mRecordId + " Desc";

    public static final String mFirstTimeInstallation = "FirstTimeInstallation";
    public static final String mTableName = "TableName";
    public static final String mFetchData = "FetchData";
    public static final String mSyncData = "SyncData";
    public static final String mItemName = "ItemName";
    public static final String mPageNo = "PageNo";
    public static final String mUserGeo = "UserGeo";
    public static final String mLogin = "Login";
    public static final String mScreenHeight = "screenHeight";
    public static final String mOldOnSync = "oldOnSync";
    public static final String mOldOnHome = "oldOnHome";
    public static final String mUserFullName = "userFullName";
    public static final String mUserId = "UserId";
    public static final String mLanguage = "Language";
    public static final String mAppVersion = "appVersion";
    public static final String mUserDesignation = "userDesignation";
    public static final String mUserGeoType = "userGeoType";
    public static final String mShouldStore = "ShouldStore";
    public static final String mHasPaging = "HasPaging";
    public static final String mObject = "Object";

}