package anindya.sample.smackchat.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import anindya.sample.smackchat.model.Users;
import base.droidtool.database.Repository;


import static base.droidtool.config.Constants.mRecordId;


public class DatabaseHandler {

    Context mContext;

    public DatabaseHandler(Context context) {
        mContext = context;
        createTable();
    }

    public void createTable() {
       // new Repository(mContext, new Users()).create(mRecordId, true);
    }

    public void alterTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        new Repository(mContext, new Users()).
                alterFieldRename("8,12", String.valueOf(oldVersion), "Name,Fname","FullName");
    }
}
