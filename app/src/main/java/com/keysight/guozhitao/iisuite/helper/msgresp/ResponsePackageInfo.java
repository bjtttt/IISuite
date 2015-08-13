package com.keysight.guozhitao.iisuite.helper.msgresp;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/12/2015.
 */
public class ResponsePackageInfo implements Serializable {

    public enum ResponseCheckType {
        OK,
        ERROR_INVALID,
        ERROR_INDEX,
        ERROR_RESP_INDEX,
        ERROR_RESERVED,
        ERROR_MSG_CHECK_TYPE,
        ERROR_PACK_TOTAL,
        ERROR_PACK_INDEX,
        ERROR_LEN,
        ERROR_LEN_MISMATCH,
        ERROR_CRC
    }

    private int mIndex = 0;
    private int mResponseIndex = 0;
    private boolean mIsMultiple = false;
    private MessagePackageInfo.MessageCheckType mMsgCheckType = MessagePackageInfo.MessageCheckType.OK;
    private int mPackageTotal = 0;
    private int mPackageIndex = 0;
    private int mLen = 0;
    private byte mCRC = (byte)0;

    private byte[] mSource;

    /*
    mRespCheckType will be set by the receiver, the mobile client.
     */
    private ResponseCheckType mRespCheckType = ResponseCheckType.OK;

    /*
    Constructor
     */
    public ResponsePackageInfo(byte[] ba) {
        mSource = ba;
    }

    public byte[] getSource() {
        return mSource;
    }

    public void setResponseCheckType(ResponseCheckType responseCheckType) {
        mRespCheckType = responseCheckType;
    }

    public ResponseCheckType getResponseCheckType() {
        return mRespCheckType;
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

    public void setIsMultiple(boolean isMultiple) {
        mIsMultiple = isMultiple;
    }

    public boolean getIsMultiple() {
        return mIsMultiple;
    }

    public void setMessageCheckType(MessagePackageInfo.MessageCheckType msgCheckType) {
        mMsgCheckType = msgCheckType;
    }

    public MessagePackageInfo.MessageCheckType getMessageCheckType() {
        return mMsgCheckType;
    }

    public void setPackageTotal(int packageTotal) {
        mPackageTotal = packageTotal;
    }

    public int getPackageTotal() {
        return mPackageTotal;
    }

    public void setPackageIndex(int packageIndex) {
        mPackageIndex = packageIndex;
    }

    public int getPackageIndex() {
        return mPackageIndex;
    }

    public void setLen(int len) {
        mLen = len;
    }

    public int getLen() {
        return mLen;
    }

    public byte[] getData() {
        if(mSource == null)
            return null;

        byte[] ba = new byte[mLen];
        for (int i = 0; i < mLen; i++) {
            ba[i] = mSource[i + 22];
        }
        return ba;
    }

    public void setCRC (byte crc) {
        mCRC = crc;
    }

    public byte getCRC() {
        return mCRC;
    }
}
