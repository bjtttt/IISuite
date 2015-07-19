package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class ServerInfo implements Serializable {
    private String mServer;
    private boolean mConnected;

    public ServerInfo() {
    }

    public void setServer(String s) {
        mServer = s.trim();
    }

    public String getServer() {
        return mServer.trim();
    }

    public boolean getConnected() {
        return mConnected;
    }

    public void setConnected(boolean b) {
        mConnected = b;
    }

    public String getServerConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append(getConnected()? "Connected" : "Unconnected");

        return sb.toString();
    }
}
