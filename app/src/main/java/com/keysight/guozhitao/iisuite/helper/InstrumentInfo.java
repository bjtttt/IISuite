package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class InstrumentInfo implements Serializable {
    private String mConnection = "";
    private int mTimeout = 5;
    private boolean mIDN = false;
    private boolean mSCPI = false;
    private boolean mConnected = false;
    private boolean mLocked = false;

    public InstrumentInfo() {
    }

    public void setConnection(String s) {
        mConnection = s.trim();
    }

    public String getConnection() {
        return mConnection.trim();
    }

    public void setTimeout(int i) {
        if(i < 5)
            throw new IllegalArgumentException(String.format("InstrumentInfo:setTimeout(%d)", i));
        mTimeout = i;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public void setIDN(boolean b) {
        mIDN = b;
    }

    public boolean getIDN() {
        return mIDN;
    }

    public void setSCPI(boolean b) {
        mSCPI = b;
    }

    public boolean getSCPI() {
        return mSCPI;
    }

    public boolean getConnected() {
        return mConnected;
    }

    public void setConnected(boolean b) {
        mConnected = b;
    }

    public void setLocked(boolean b) {
        mLocked = b;
    }

    public boolean getLocked() {
        return mLocked;
    }

    public String getInstrumentConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimeout() + "s, ");
        sb.append(getConnected()? "Connected, " : "Unconnected, ");
        sb.append(getLocked()? "Locked, " : "Unlocked, ");
        sb.append(getIDN()? "Auto IDN, " : "None IDN, ");
        sb.append(getSCPI()? "Auto SCPI Tree" : "None SCPI Tree");

        return sb.toString();
    }
}
