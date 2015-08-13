package com.keysight.guozhitao.iisuite.helper.msgresp;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.LogService;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/*

To Server :
Socket Package Definition :
INDEX       : 4 bytes
              Min - 1, Max - Integer.MAX_VALUE
RESERVED    : 2 bytes
PROPERTY    : 2 bytes
              bit15 : 1 - multiple, 0 - single
              bit14 ~ bit0 : MessagePackageInfo.MessagePackageType
PACKAGE     : 8 bytes
              bit63 ~ bit32 : total
                              Min - 0, Max - Integer.MAX_VALUE
              bit31 ~ bit0  : index
                              When total == 0, index == 0.
                              When total > 0,
                              Min - 1, Max - total
DATA LENGTH : 2 bytes
DATA        : 0 ~ 1000 bytes
CRC         : 1 byte

From Server :
Socket Package Definition :
INDEX           : 4 bytes
                  Min - 1, Max - Integer.MAX_VALUE
RERPONSE INDEX  : 4 bytes
                  Min - 1, Max - Integer.MAX_VALUE
RESERVED        : 2 bytes
PROPERTY        : 2 bytes
                  bit15 : 1 - multiple, 0 - single
                  bit14 ~ bit0 : MessagePackageInfo.MessageCheckType
PACKAGE     : 8 bytes
              bit63 ~ bit32 : total
                              Min - 0, Max - Integer.MAX_VALUE
              bit31 ~ bit0  : index
                              When total == 0, index == 0.
                              When total > 0,
                              Min - 1, Max - total
DATA LENGTH : 2 bytes
DATA        : 0 ~ 1000 bytes
CRC         : 1 byte

 */

/**
 * Created by cn569363 on 7/24/2015.
 */
public class ServerPackageManager implements Serializable {

    public final static int MAX_MESSAGE_INDEX = Integer.MAX_VALUE;  // 0x7FFF
    public final static int MESSAGE_INFORMATION_LENGTH = 19;
    public final static int MESSAGE_RESPONSE_LENGTH = 23;
    public final static int MAX_MESSAGE_BODY_LENGTH = 4096;

    private static int mMessageIndex = 0;

    private LogService mLogService;
    private GlobalSettings mGlobalSettings;

    private ServerPackageManager() {
    }

    private static final ServerPackageManager mServerPackageManager = new ServerPackageManager();

    public static ServerPackageManager getInstance() {
        return mServerPackageManager;
    }

    public void setGlobalSettings(GlobalSettings gs) {
        mGlobalSettings = gs;
        mLogService = gs.getLogService();
    }

    public ArrayList<MessagePackageInfo> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType) {
        return composeMsgs(msgPackageType, new byte[]{});
    }

    public ArrayList<MessagePackageInfo> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType, String s) {
        if (s == null || s.length() < 1)
            return composeMsgs(msgPackageType, new byte[]{});
        else {
            try {
                byte[] bs = s.getBytes("UTF-8");
                return composeMsgs(msgPackageType, bs);
            } catch (UnsupportedEncodingException e) {
                byte[] bs = s.getBytes();
                return composeMsgs(msgPackageType, bs);
            }
        }
    }

    public ArrayList<MessagePackageInfo> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType, byte[] ba) {
        if (ba == null)
            ba = new byte[]{};

        int messageLen = ba.length;
        int messageCount = 1;
        int messageIndex = 0;
        if (messageLen > MAX_MESSAGE_BODY_LENGTH)
            messageCount = (messageLen - (messageLen % MAX_MESSAGE_BODY_LENGTH)) / MAX_MESSAGE_BODY_LENGTH;

        ArrayList<byte[]> msgList = splitMessage(ba);
        ArrayList<MessagePackageInfo> messageList = new ArrayList<>();

        for (byte[] baItem : msgList) {
            messageList.add(composeMsg(msgPackageType, baItem, messageCount, messageIndex++));
        }

        return messageList;
    }

    private ArrayList<byte[]> splitMessage(byte[] ba) {
        if (ba == null)
            ba = new byte[]{};

        ArrayList<byte[]> messageList = new ArrayList<>();

        int messageLen = ba.length;
        int messageIndex = 0;
        if (messageLen > MAX_MESSAGE_BODY_LENGTH) {
            while (messageLen > 0) {
                int len = messageLen > MAX_MESSAGE_BODY_LENGTH ? MAX_MESSAGE_BODY_LENGTH : messageLen;
                byte[] baItem = new byte[len];
                for (int i = 0; i < len; i++) {
                    baItem[i] = ba[MAX_MESSAGE_BODY_LENGTH * messageIndex + i];
                }
                messageLen = messageLen - MAX_MESSAGE_BODY_LENGTH;
                messageIndex++;
            }
        } else {
            messageList.add(ba);
        }

        return messageList;
    }

    private MessagePackageInfo composeMsg(MessagePackageInfo.MessagePackageType msgPackageType, byte[] ba, int totalPackage, int indexPackage) {
        MessagePackageInfo mpi = new MessagePackageInfo();

        if (mMessageIndex > MAX_MESSAGE_INDEX || mMessageIndex < 1)
            mMessageIndex = 1;

        int msgLen = 0;
        if(ba == null)
            msgLen = 0;
        else
            msgLen = ba.length;

        int totalLen = MESSAGE_INFORMATION_LENGTH + msgLen;
        byte[] baFinal = new byte[totalLen];

        baFinal[0] = (byte) ((mMessageIndex >> 24) & 0xFF);
        baFinal[1] = (byte) ((mMessageIndex >> 16) & 0xFF);
        baFinal[2] = (byte) ((mMessageIndex >> 8) & 0xFF);
        baFinal[3] = (byte) (mMessageIndex & 0xFF);
        mpi.setIndex(mMessageIndex);

        baFinal[4] = (byte)0;
        baFinal[5] = (byte)0;

        baFinal[6] = (byte) ((msgPackageType.ordinal() >> 8) & 0x7F);
        baFinal[7] = (byte) (msgPackageType.ordinal() & 0xFF);
        mpi.setMsgPackageType(msgPackageType);
        if(msgPackageType != MessagePackageInfo.MessagePackageType.Pulse) {
            if (totalPackage > 1) {
                baFinal[6] = (byte) (baFinal[6] | 0x8);
                mpi.setIsMultiple(true);

                baFinal[8] = (byte) ((totalPackage >> 24) & 0xFF);
                baFinal[9] = (byte) ((totalPackage >> 16) & 0xFF);
                baFinal[10] = (byte) ((totalPackage >> 8) & 0xFF);
                baFinal[11] = (byte) (totalPackage & 0xFF);
                mpi.setPackageTotal(totalPackage);

                baFinal[12] = (byte) ((indexPackage >> 24) & 0xFF);
                baFinal[13] = (byte) ((indexPackage >> 16) & 0xFF);
                baFinal[14] = (byte) ((indexPackage >> 8) & 0xFF);
                baFinal[15] = (byte) (indexPackage & 0xFF);
                mpi.setPackageIndex(indexPackage);
            }
            else
                mpi.setIsMultiple(false);
        }
        else
            mpi.setIsMultiple(false);

        baFinal[16] = (byte) ((msgLen >> 8) & 0xFF);
        baFinal[17] = (byte) (msgLen & 0xFF);
        mpi.setLen(msgLen);

        byte crc = (byte) 0;
        for (int i = 0; i < msgLen; i++) {
            crc = (byte) (crc ^ ba[i]);
            baFinal[i + 18] = ba[i];
        }
        baFinal[baFinal.length - 1] = crc;
        mpi.setCRC(crc);
        mpi.setSource(baFinal);

        mMessageIndex++;

        return mpi;
    }

    /*
    The caller should make sure ba != null
     */
    public ResponsePackageInfo parseResponsePackage(byte[] ba) {
        ResponsePackageInfo rpi = new ResponsePackageInfo(ba);

        if (ba == null || ba.length < MESSAGE_RESPONSE_LENGTH) {
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_INVALID);
            return rpi;
        }

        int dataLen = ba.length - MESSAGE_RESPONSE_LENGTH;
        byte[] baResponse = new byte[dataLen];

        int index = (int) ba[0] << 24 | (int) ba[1] << 16 | (int) ba[2] << 8 | (int) ba[3];
        if (index < 1 || index > MAX_MESSAGE_INDEX) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : INDEX ERROR " + String.valueOf(index));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_INDEX);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : INDEX " + String.valueOf(index));
        rpi.setIndex(index);

        int respIndex = (int) ba[4] << 24 | (int) ba[5] << 16 | (int) ba[6] << 8 | (int) ba[7];
        if (index < 1 || index > MAX_MESSAGE_INDEX) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : RESPONSE INDEX ERROR " + String.valueOf(respIndex));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_RESP_INDEX);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : RESPONSE INDEX " + String.valueOf(respIndex));
        rpi.setResponseIndex(respIndex);

        int reserved = (int) ba[8] << 8 | (int) ba[9];
        if (reserved != 0) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : RESERVED ERROR " + String.valueOf(reserved));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_RESERVED);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : RESERVED " + String.valueOf(reserved));

        boolean isMultiple = ((int) ba[10] & 0x8) == 0x8;
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : MULTIPLE " + String.valueOf(isMultiple));
        rpi.setIsMultiple(isMultiple);

        int msgCheckTypeInteger = ((int) ba[10] & 0x7) << 8 | (int) ba[11];
        MessagePackageInfo.MessageCheckType[] msgCheckTypeArray = MessagePackageInfo.MessageCheckType.values();
        if (msgCheckTypeInteger < 0 || msgCheckTypeInteger > msgCheckTypeArray.length - 1) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : MSG CHECK TYPE ERROR " + String.valueOf(msgCheckTypeInteger));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_MSG_CHECK_TYPE);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : MSG CHECK TYPE " + String.valueOf(msgCheckTypeArray[msgCheckTypeInteger]));
        rpi.setMessageCheckType(msgCheckTypeArray[msgCheckTypeInteger]);

        int packageTotal = (int) ba[12] << 8 | (int) ba[13] << 8 | (int) ba[14] << 8 | (int) ba[15];
        if ((isMultiple == false && packageTotal != 0) || (isMultiple == true && packageTotal < 1)) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : PACKAGE TOTAL ERROR" + String.valueOf(packageTotal));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_PACK_TOTAL);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : PACKAGE TOTAL " + String.valueOf(packageTotal));
        rpi.setPackageTotal(packageTotal);

        int packageIndex = (int) ba[16] << 8 | (int) ba[17] << 8 | (int) ba[18] << 8 | (int) ba[19];
        if ((isMultiple == false && packageIndex != 0) || (isMultiple == true && (packageIndex < 1 || packageIndex > packageTotal))) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : PACKAGE INDEX ERROR" + String.valueOf(packageIndex));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_PACK_INDEX);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : PACKAGE INDEX " + String.valueOf(packageIndex));
        rpi.setPackageIndex(packageIndex);

        int len = (int) ba[20] << 8 | (int) ba[21];
        if (len < 0 || len > MAX_MESSAGE_BODY_LENGTH) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : LEN ERROR" + String.valueOf(len));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_LEN);
            return rpi;
        }
        if (len != dataLen) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : LENGTH MISMATCH ERROR - LEN " + String.valueOf(len) + " V.S. DATA LEN " + String.valueOf(dataLen));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_LEN_MISMATCH);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : LEN " + String.valueOf(len));
        rpi.setLen(len);

        byte crc = (byte) 0;
        for (int i = 0; i < len; i++) {
            crc = (byte)(crc ^ ba[i + 22]);
            baResponse[i] = ba[i + 22];
        }
        if (crc != ba[ba.length - 1]) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : CRC ERROR - CALC CRC " + String.valueOf(crc) + " V.S. CRC " + String.valueOf(ba[ba.length - 1]));
            rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.ERROR_CRC);
            return rpi;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : CRC " + String.valueOf(crc));
        rpi.setCRC(crc);

        rpi.setResponseCheckType(ResponsePackageInfo.ResponseCheckType.OK);
        return rpi;
    }
}
