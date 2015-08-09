package com.keysight.guozhitao.iisuite.helper;

/**
 * Created by cn569363 on 8/9/2015.
 */
public class ServerResponsePackageInfo {

    private int mMessageIndex = 0;

    public int getMessageIndex() {
        return mMessageIndex;
    }

    public void setMessageIndex(int msgIndex) {
        if(msgIndex > ServerPackageManager.MAX_MESSAGE_INDEX || msgIndex < 1)
            throw new IllegalArgumentException("ServerResponsePackageInfo::setMessageIndex(" + String.valueOf(msgIndex) + ")");
        mMessageIndex = msgIndex;
    }

    public ServerResponsePackageInfo() {
    }

    public Boolean parseServerResponsePackage(byte[] ba) {
        return true;
    }
}
