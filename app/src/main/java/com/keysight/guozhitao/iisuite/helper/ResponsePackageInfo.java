package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/12/2015.
 */
public class ResponsePackageInfo implements Serializable {

    public enum ResponseCheckType {
        ACK_DATA,
        INVALID_RESPONSE,
        ERROR_INDEX,
        ERROR_RESP_INDEX,
        ERROR_MULTIPLE,
        ERROR_PACK_TOTAL,
        ERROR_PACK_INDEX,
        ERROR_LEN,
        ERROR_LEN_MISMATCH,
        ERROR_CRC,
    }

    private int mIndex = 0;
    private int mResponseIndex = 0;
    private boolean mIsMultiple = false;
    private MessagePackageInfo.MessageCheckType mMsgType = MessagePackageInfo.MessageCheckType.DATA;
    private int mPackageTotal = 0;
    private int mPackageIndex = 0;
    private int mLength = 0;
    private byte[] mData = null;
    private byte mCrc = (byte)0;

    private byte[] mSource;
    private ResponseCheckType mRespType = ResponseCheckType.ACK_DATA;

    public ResponsePackageInfo(byte[] ba) {
        mSource = ba;
    }

    public byte[] getSource() {
        return mSource;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex(){
        return mIndex;
    }

    public void setResponseIndex(int respIndex) {
        mResponseIndex = respIndex;
    }

    public int getResponseIndex() {
        return mResponseIndex;
    }

    public void setIsMultiple
}
