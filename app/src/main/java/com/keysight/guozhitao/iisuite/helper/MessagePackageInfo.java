package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/12/2015.
 */
public class MessagePackageInfo implements Serializable {

    public enum MessagePackageType {
        None,                           // When mIsData == false
        SyncInstrumentFromServer,
        SyncInstrumentToServer,
        SyncInstrumentMergeServer,
        SyncServerFromServer,
        SyncServerToServer,
        SyncServerMergeServer,
    }

    /*
    This is from the server.
    The message is sent to server, so the server will check the message and report the status.
     */
    public enum MessageCheckType {
        PULSE_DATA,
        INVALID_MESSAGE,
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
    private boolean mIsData = true;
    private boolean mIsMultiple = false;
    private MessagePackageType mMsgPackageType = MessagePackageType.None;
    private int mPackageTotal = 0;
    private int mPackageIndex = 0;
    private int mLen = 0;
    private byte[] mData = null;
    private byte mCRC = 0;

    private byte[] mSource = null;

    private MessageCheckType mMsgCheckType = MessageCheckType.PULSE_DATA;

    public MessagePackageInfo(byte[] ba) {
        mSource = ba;
    }


    public byte[] getSource() {
        return mSource;
    }

    public void setMsgCheckType(MessageCheckType msgCheckType) {
        mMsgCheckType = msgCheckType;
    }

    public MessageCheckType getMsgCheckType() {
        return mMsgCheckType;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIsData(boolean isData) {
        mIsData = isData;
    }

    public boolean getIsData() {
        return  mIsData;
    }

    public void setIsMultiple(boolean isMultiple) {
        mIsMultiple = isMultiple;
    }

    public boolean getIsMultiple() {
        return mIsMultiple;
    }

    public void setMsgPackageType(MessagePackageType msgPackageType) {
        mMsgPackageType = msgPackageType;
    }

    public MessagePackageType getMessagePackageType() {
        return mMsgPackageType;
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

    public void setData(byte[] ba) {
        mData = ba;
    }

    public byte[] getData() {
        return mData;
    }

    public void setCRC (byte crc) {
        mCRC = crc;
    }

    public byte getCRC() {
        return mCRC;
    }
}
