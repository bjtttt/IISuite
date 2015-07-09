package com.keysight.guozhitao.iisuite.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class DBService extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "iisuite.db";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE [iis_instr] ( " +
                "[id] AUTOINC, " +
                "[connection] VARCHAR(100) PRIMARY KEY, " +
                "[idn] BOOLEAN NOT NULL DEFAULT FALSE, " +
                "[scpitree] BOOLEAN NOT NULL DEFAULT FALSE," +
                "[connected] BOOLEAN NOT NULL DEFAULT FALSE," +
                "[locked] BOOLEAN NOT NULL DEFAULT FALSE";
        db.execSQL(sql);
    }

    public DBService(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Only for query, if change is necessary, call querySet
     * @param sql
     * @param args
     * @return
     */
    public Cursor query(String sql, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        return c;
    }

    /**
     * Only for change, if change is NOT necessary, call query
     * @param sql
     * @param args
     * @return
     */
    public Cursor querySet(String sql, String[] args) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(sql, args);
        return c;
    }
}
