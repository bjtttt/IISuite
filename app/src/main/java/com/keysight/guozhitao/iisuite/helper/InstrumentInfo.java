package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/9/2015.
 */
public class InstrumentInfo implements Serializable {
    private String mConnection;
    private boolean mIDN;
    private boolean mSCPI;
    private boolean mConnected;
    private boolean mLocked;

    public InstrumentInfo() {
    }

    public void setConnection(String s) {
        mConnection = s;
    }

    public String getConnection() {
        return mConnection;
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
        sb.append(getConnected()? "Connected, " : "Unconnected, ");
        sb.append(getLocked()? "Locked, " : "Unlocked, ");
        sb.append(getLocked()? "Auto IDN, " : "None IDN, ");
        sb.append(getLocked()? "Auto SCPI Tree" : "None SCPI Tree");

        return sb.toString();
    }
}
