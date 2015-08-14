package com.keysight.guozhitao.iisuite.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class DBService extends SQLiteOpenHelper implements Serializable {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "iisuite.db";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE iis_instr ( " +
                "connection VARCHAR(100) PRIMARY KEY, " +
                "timeout INTEGER NOT NULL DEFAULT 5, " +
                "idn BOOLEAN NOT NULL DEFAULT FALSE, " +
                "scpitree BOOLEAN NOT NULL DEFAULT FALSE," +
                "connected BOOLEAN NOT NULL DEFAULT TRUE," +
                "locked BOOLEAN NOT NULL DEFAULT FALSE )";
        db.execSQL(sql);

        sql = "CREATE TABLE iis_server ( " +
                "server VARCHAR(100) PRIMARY KEY, " +
                "timeout INTEGER NOT NULL DEFAULT 5, " +
                "autoconn BOOLEAN NOT NULL DEFAULT TRUE )";
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
    public Cursor rawQuery(String sql, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        return c;
    }

    /**
     * Only for change, if change is NOT necessary, call query
     * @param sql
     * @return
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
}
