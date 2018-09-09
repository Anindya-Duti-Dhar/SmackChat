package base.droidtool.dtlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import anindya.sample.smackchat.utils.DatabaseHandler;

import static anindya.sample.smackchat.utils.Const.DATABASE_VERSION;
import static anindya.sample.smackchat.utils.Const.DB_NAME;

/**
 * Created by imrose on 6/16/2018.
 */

public class Db extends SQLiteOpenHelper {

    Context context;

    public Db(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void executeQuery(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new DatabaseHandler(context).alterTable(db, oldVersion, newVersion);
    }
}
