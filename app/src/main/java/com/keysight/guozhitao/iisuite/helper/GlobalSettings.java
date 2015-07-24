package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cn569363 on 7/16/2015.
 */
public class GlobalSettings implements Serializable {

    public final static int MIN_TIMEOUT = 5;
    public final static int MAX_TIMEOUT = 3600 * 24;

    public final static int INSTRUMENT_SOCKET_PORT = 5923;
    public final static int SERVER_SOCKET_PORT = 6910;

    public final static int SERVER_PULSE_INTERVAL = 5000;

    private InstrumentInfo mCurrentInstrumentInfo = null;
    private ArrayList<InstrumentInfo> mInstrInfoList = new ArrayList<InstrumentInfo>();
    private ServerInfo mCurrentServerInfo = null;
    private ArrayList<ServerInfo> mServerInfoList = new ArrayList<ServerInfo>();

    private DBService mDBService;
    private SocketService mSocketService;
    private MessageThread mMessageThread = new MessageThread(this);

    private ServerPackageManager mServerPackageManager = null;

    private GlobalSettings() {
        mSocketService = new SocketService(this);
        mServerPackageManager = ServerPackageManager.getInstance();
    }
    private static final GlobalSettings mGlobalSettings = new GlobalSettings();

    public static GlobalSettings getInstance() {
        return mGlobalSettings;
    }

    public InstrumentInfo getCurrentInstrumentInfo() {
        return mCurrentInstrumentInfo;
    }

    public void setCurrentInstrumentInfo(int i) {
        if(i < -1 || i >= mInstrInfoList.size())
            throw new IndexOutOfBoundsException(String.format("GlobalSettings::setCurrentInstrumentInfo(%d)", i));

        if(i == -1)
            mCurrentInstrumentInfo = null;
        else
            mCurrentInstrumentInfo = mInstrInfoList.get(i);
    }

    public void setCurrentInstrumentInfo(InstrumentInfo ii) {
        if(ii == null)
            throw new NullPointerException("GlobalSettings::setCurrentInstrumentInfo(null)");

        boolean bFind = false;
        for(InstrumentInfo info : mInstrInfoList) {
            if(info.equals(ii)) {
                bFind = true;
                break;
            }
        }
        if(!bFind) {
            throw new IllegalArgumentException("GlobalSettings::setCurrentInstrumentInfo(InstrumentInfo)");
        }

        mCurrentInstrumentInfo = ii;
    }

    public void setCurrentInstrumentInfo(String connection) {
        if(connection == null)
            throw new NullPointerException("GlobalSettings::setCurrentInstrumentInfo(null)");
        if(connection.length() < 1)
            throw new NullPointerException("GlobalSettings::setCurrentInstrumentInfo(empty)");


        boolean bFind = false;
        InstrumentInfo ii = null;
        for(InstrumentInfo info : mInstrInfoList) {
            if(info.getConnection().compareTo(connection.trim()) == 0) {
                bFind = true;
                ii = info;
                break;
            }
        }
        if(!bFind) {
            throw new IllegalArgumentException("GlobalSettings::setCurrentInstrumentInfo(InstrumentInfo)");
        }

        mCurrentInstrumentInfo = ii;
    }

    public ArrayList<InstrumentInfo> getInstrumentInfoList() { return mInstrInfoList; }

    public ServerInfo getCurrentServerInfo() { return mCurrentServerInfo; }

    public void setCurrentServerInfo(int i) {
        if(i < -1 || i >= mServerInfoList.size())
            throw new IndexOutOfBoundsException(String.format("GlobalSettings::setCurrentServerInfo(%d)", i));

        if(i == -1)
            mCurrentServerInfo = null;
        else
            mCurrentServerInfo = mServerInfoList.get(i);
    }

    public void setCurrentServerInfo(ServerInfo si) {
        if(si == null)
            throw new NullPointerException("GlobalSettings::setCurrentServerInfo(null)");

        boolean bFind = false;
        for(ServerInfo info : mServerInfoList) {
            if(info.equals(si)) {
                bFind = true;
                break;
            }
        }
        if(!bFind) {
            throw new IllegalArgumentException("GlobalSettings::setCurrentServerInfo(ServerInfo)");
        }

        mCurrentServerInfo = si;
    }

    public void setCurrentServerInfo(String server) {
        if(server == null)
            throw new NullPointerException("GlobalSettings::setCurrentServerInfo(null)");
        if(server.length() < 1)
            throw new NullPointerException("GlobalSettings::setCurrentServerInfo(empty)");

        boolean bFind = false;
        ServerInfo si = null;
        for(ServerInfo info : mServerInfoList) {
            if(info.getServer().compareTo(server.trim()) == 0) {
                bFind = true;
                si = info;
                break;
            }
        }
        if(!bFind) {
            throw new IllegalArgumentException("GlobalSettings::setCurrentServerInfo(ServerInfo)");
        }

        mCurrentServerInfo = si;
    }

    public ArrayList<ServerInfo> getServerInfoList() {
        return mServerInfoList;
    }

    public void setDBService(DBService dbs) { mDBService = dbs; }

    public DBService getDBService() { return mDBService; }

    public MessageThread getMessageThread() { return mMessageThread; }

    //public void setSocketService(SocketService socketService) { mSocketService = socketService; }

    public SocketService getSocketService() { return mSocketService; }
}
