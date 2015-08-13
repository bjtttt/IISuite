package com.keysight.guozhitao.iisuite.helper.msgresp;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/12/2015.
 */
public class MessagePackageInfo implements Serializable {

    public enum MessagePackageType {
        Pulse,
        Request,
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
        OK,
        ERROR_INDEX,
        ERROR_RESERVED,
        ERROR_MULTIPLE,
        ERROR_MSG_PACK_TYPE,
        ERROR_PACK_TOTAL,
        ERROR_PACK_INDEX,
        ERROR_LEN,
        ERROR_LEN_MISMATCH,
        ERROR_PULSE_LEN,
        ERROR_CRC,
    }

    private int mIndex = 0;
    private boolean mIsData = true;
    private boolean mIsMultiple = false;
    private MessagePackageType mMsgPackageType = MessagePackageType.Pulse;
    private int mPackageTotal = 0;
    private int mPackageIndex = 0;
    private int mLen = 0;
    private byte mCRC = 0;

    private byte[] mSource = null;

    /*
    mMsgCheckType will should be set when the response arrives.
    When mIsMultiple is false,
    if mMsgCheckType is MessageCheckType.PULSE_DATA,
    this message should be removed from the sending queue.
    When mIsMultiple is true,
    if mMsgCheckType is MessageCheckType.PULSE_DATA and all mMsgCheckTypes of all sub MessagePackageInfos are MessageCheckType.PULSE_DATA,
    all sub MessagePackageInfos will be removed from the sending queue.
     */
    private MessageCheckType mMsgCheckType = MessageCheckType.OK;

    /*
    Constructor
     */
    public MessagePackageInfo() { }

    public MessagePackageInfo(byte[] ba) {
        mSource = ba;
    }

    public void setSource(byte[] ba) {
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

    public void setMsgPackageType(MessagePackageType msgPackageType) { mMsgPackageType = msgPackageType; }

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

    public byte[] getData() {
        if(mSource == null)
            return null;

        byte[] ba = new byte[mLen];
        for (int i = 0; i < mLen; i++) {
            ba[i] = mSource[i + 18];
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
