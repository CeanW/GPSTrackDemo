package com.ceanwu.gpstrackdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    static final String TABLE_TRACK = "track";
    static final String TABLE_TRACK_DETAIL = "track_detail";
    static final String ID = "_id";

    //table TRACK
    static final String TRACK_NAME = "track_name";
    static final String CREATE_DATE = "create_date";
    static final String START_LOC = "start_loc";
    static final String END_LOC = "end_loc";

    //table TRACK_DETAIL
    static final String TID = "tid";
    static final String LAT = "lat"; //latitude
    static final String LNG = "lng"; //longitude

    //SQL syntax
    static final String CREATE_TABLE_TRACK =
            "CREATE TABLE track(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "track_name TEXT, " +
                    "create_date TEXT, " +
                    "start_loc TEXT, " +
                    "end_loc TEXT)";
    static final String CREATE_TABLE_TRACK_DETAIL =
            "CREATE TABLE track_detail(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tid INTEGER not null, " +
                    "lat REAL, " + //REAL means double
                    "lng REAL)";

    static final int VERSION = 1;
    private static final String DB_NAME = "track.db";

    /**
     * Create SQLite Database with specified Database name and version
     * @param context
     */
    public MyDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRACK);
        db.execSQL(CREATE_TABLE_TRACK_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE track");
            db.execSQL("");
        }
    }
}
