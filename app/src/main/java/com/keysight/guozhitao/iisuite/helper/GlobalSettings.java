package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cn569363 on 7/16/2015.
 */
public class GlobalSettings implements Serializable {

    private ArrayList<InstrumentInfo> mInstrInfoList = new ArrayList<InstrumentInfo>();

    public ArrayList<InstrumentInfo> getInstrumentInfoList() {
        return mInstrInfoList;
    }

    private ArrayList<ServerInfo> mServerInfoList = new ArrayList<ServerInfo>();

    public ArrayList<ServerInfo> getServerInfoList() {
        return mServerInfoList;
    }

    private DBService mDBService;

    public void setDBService(DBService dbs) {
        mDBService = dbs;
    }

    public DBService getmDBService() {
        return mDBService;
    }
}
