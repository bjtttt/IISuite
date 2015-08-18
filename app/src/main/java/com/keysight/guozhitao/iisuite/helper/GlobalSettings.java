package com.keysight.guozhitao.iisuite.helper;

import android.os.Handler;

import com.keysight.guozhitao.iisuite.helper.msgresp.MessagePackageInfo;
import com.keysight.guozhitao.iisuite.helper.msgresp.ResponsePackageInfo;
import com.keysight.guozhitao.iisuite.helper.msgresp.ServerPackageManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cn569363 on 7/16/2015.
 */
public class GlobalSettings implements Serializable {

    public final static String KEY_MSG_SHORT = "MSG";
    public final static String KEY_SERVER = "SERVER";
    public final static String KEY_UTF8 = "UTF-8";

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

    private LogService mLogService;

    private ServerPackageManager mServerPackageManager = null;

    private ArrayList<MessagePackageInfo> mMsgPackageInfoSentArray = new ArrayList<MessagePackageInfo>();
    private ArrayList<ResponsePackageInfo> mRespPackageInfoSentArray = new ArrayList<ResponsePackageInfo>();

    private Handler mMainHandler;

    private GlobalSettings() {
        mServerPackageManager = ServerPackageManager.getInstance();
        mServerPackageManager.setGlobalSettings(this);

        mSocketService = new SocketService(this);
        mLogService = new LogService(this);
    }

    private static final GlobalSettings mGlobalSettings = new GlobalSettings();

    public static GlobalSettings getInstance() {
        return mGlobalSettings;
    }

    public void setMainHandler(Handler h) {
        mMainHandler = h;
    }

    public Handler getMainhandler() {
        return mMainHandler;
    }

    public ServerPackageManager getServerPackageManager() {
        return mServerPackageManager;
    }

    public ArrayList<MessagePackageInfo> getMessagePackageInfoSentArray() {
        return mMsgPackageInfoSentArray;
    }

    public ArrayList<ResponsePackageInfo> getResponsePackageInfoSentArray() {
        return mRespPackageInfoSentArray;
    }

    public InstrumentInfo getCurrentInstrumentInfo() {
        return mCurrentInstrumentInfo;
    }

    public void setCurrentInstrumentInfo(int i) {
        if(i < -1 || i >= mInstrInfoList.size())
            mCurrentInstrumentInfo = null;
        else
            mCurrentInstrumentInfo = mInstrInfoList.get(i);
    }

    public void setCurrentInstrumentInfo(InstrumentInfo ii) {
        if(ii == null)
            mCurrentInstrumentInfo = null;
        else {
            boolean bFind = false;
            for (InstrumentInfo info : mInstrInfoList) {
                if (info.equals(ii)) {
                    bFind = true;
                    break;
                }
            }
            if (bFind == true)
                mCurrentInstrumentInfo = ii;
            else
                mCurrentInstrumentInfo = null;
        }
    }

    public void setCurrentInstrumentInfo(String connection) {
        if(connection == null || connection.length() < 1)
            mCurrentInstrumentInfo = null;
        else {
            boolean bFind = false;
            InstrumentInfo ii = null;
            for (InstrumentInfo info : mInstrInfoList) {
                if (info.getConnection().compareTo(connection.trim()) == 0) {
                    bFind = true;
                    ii = info;
                    break;
                }
            }
            if (bFind == true)
                mCurrentInstrumentInfo = ii;
            else
                mCurrentInstrumentInfo = null;
        }
    }

    public ArrayList<InstrumentInfo> getInstrumentInfoList() { return mInstrInfoList; }

    public ServerInfo getCurrentServerInfo() { return mCurrentServerInfo; }

    public void setCurrentServerInfo(int i) {
        if(i < -1 || i >= mServerInfoList.size())
            mCurrentServerInfo = null;
        else
            mCurrentServerInfo = mServerInfoList.get(i);
    }

    public void setCurrentServerInfo(ServerInfo si) {
        if(si == null)
            mCurrentServerInfo = null;
        else {
            boolean bFind = false;
            for (ServerInfo info : mServerInfoList) {
                if (info.equals(si)) {
                    bFind = true;
                    break;
                }
            }
            if (bFind == true)
                mCurrentServerInfo = si;
            else
                mCurrentServerInfo = null;
        }
    }

    public void setCurrentServerInfo(String server) {
        if(server == null || server.length() < 1)
            mCurrentServerInfo = null;
        else {
            boolean bFind = false;
            ServerInfo si = null;
            for (ServerInfo info : mServerInfoList) {
                if (info.getServer().compareTo(server.trim()) == 0) {
                    bFind = true;
                    si = info;
                    break;
                }
            }
            if (bFind == true)
                mCurrentServerInfo = si;
            else
                mCurrentServerInfo = null;
        }
    }

    public ServerInfo getServerInfo(String server) {
        for (ServerInfo info : mServerInfoList) {
            if (info.getServer().compareTo(server.trim()) == 0) {
                return info;
            }
        }
        return null;
    }

    public ArrayList<ServerInfo> getServerInfoList() {
        return mServerInfoList;
    }

    public void setDBService(DBService dbs) { mDBService = dbs; }

    public DBService getDBService() { return mDBService; }

    public MessageThread getMessageThread() { return mMessageThread; }

    public SocketService getSocketService() { return mSocketService; }

    public LogService getLogService() { return mLogService; }
}
