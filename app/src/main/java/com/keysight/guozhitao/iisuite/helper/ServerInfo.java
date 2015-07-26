package com.keysight.guozhitao.iisuite.helper;

import android.provider.Settings;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class ServerInfo implements Serializable {
    private String mServer = "";
    private int mTimeout = GlobalSettings.MIN_TIMEOUT;
    private boolean mConnected = true;

    public ServerInfo() {
    }

    public void setServer(String s) {
        mServer = s.trim();
    }

    public String getServer() {
        return mServer.trim();
    }

    public void setTimeout(int i) {
        if(i < GlobalSettings.MIN_TIMEOUT)
            throw new IllegalArgumentException(String.format("InstrumentInfo:setTimeout(%d)", i));
        if(i > GlobalSettings.MAX_TIMEOUT)
            throw new IllegalArgumentException(String.format("InstrumentInfo:setTimeout(%d)", i));
        mTimeout = i;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public boolean getConnected() {
        return mConnected;
    }

    public void setConnected(boolean b) {
        mConnected = b;
    }

    public String getServerConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append("Timeout ");
        sb.append(getTimeout() + "s");//, ");
        //sb.append(getConnected()? "Connected" : "Unconnected");

        return sb.toString();
    }
}
