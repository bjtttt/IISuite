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
    public final static int MAX_MESSAGE_BODY_LENGTH = 502;

    private static int mMessageIndex = 0;
    private static int mResponseIndex = 0;

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

    public ArrayList<byte[]> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType) {
        return composeMsgs(msgPackageType, new byte[]{});
    }

    public ArrayList<byte[]> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType, String s) {
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

    public ArrayList<byte[]> composeMsgs(MessagePackageInfo.MessagePackageType msgPackageType, byte[] ba) {
        if (ba == null)
            ba = new byte[]{};

        int messageLen = ba.length;
        int messageCount = 1;
        int messageIndex = 0;
        if (messageLen > MAX_MESSAGE_BODY_LENGTH)
            messageCount = (messageLen - (messageLen % MAX_MESSAGE_BODY_LENGTH)) / MAX_MESSAGE_BODY_LENGTH;

        ArrayList<byte[]> msgList = splitMessage(ba);
        ArrayList<byte[]> messageList = new ArrayList<>();

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

    public byte[] composePulse() {
        return composeMsg(MessagePackageInfo.MessagePackageType.Pulse, null, 0, 0);
    }

    private byte[] composeMsg(MessagePackageInfo.MessagePackageType msgPackageType, byte[] ba, int totalPackage, int indexPackage) {
        if (mMessageIndex > MAX_MESSAGE_INDEX || mMessageIndex < 1)
            mMessageIndex = 1;

        int msgLen = ba.length;
        int totalLen = MESSAGE_INFORMATION_LENGTH + msgLen;
        byte[] baFinal = new byte[totalLen];

        baFinal[0] = (byte) ((mMessageIndex >> 24) & 0xFF);
        baFinal[1] = (byte) ((mMessageIndex >> 16) & 0xFF);
        baFinal[2] = (byte) ((mMessageIndex >> 8) & 0xFF);
        baFinal[3] = (byte) (mMessageIndex & 0xFF);
        baFinal[4] = (byte)0;
        baFinal[5] = (byte)0;
        if (msgPackageType == MessagePackageInfo.MessagePackageType.Pulse)
            baFinal[6] = (byte) 0;
        else
            baFinal[6] = (byte) 8;
        baFinal[6] = (byte) (baFinal[6] | ((msgPackageType.ordinal() >> 8) & 0x7F));
        baFinal[7] = (byte) (msgPackageType.ordinal() & 0xFF);
        if (totalPackage > 1)
            baFinal[6] = (byte) (baFinal[6] | 0x8);

        switch(msgPackageType) {
            default:
            case Pulse:
            case SyncInstrumentFromServer:
                baFinal[3] = (byte) 0;
                break;
            case SyncInstrumentToServer:
                baFinal[3] = (byte) 1;
                break;
            case SyncInstrumentMergeServer:
                baFinal[3] = (byte) 2;
                break;
            case SyncServerFromServer:
                baFinal[3] = (byte) 3;
                break;
            case SyncServerToServer:
                baFinal[3] = (byte) 4;
                break;
            case SyncServerMergeServer:
                baFinal[3] = (byte) 5;
                break;
        }

        baFinal[4] = (byte) ((totalPackage >> 8) & 0xF);
        baFinal[5] = (byte) (totalPackage & 0xF);
        baFinal[6] = (byte) ((indexPackage >> 8) & 0xF);
        baFinal[7] = (byte) (indexPackage & 0xF);

        baFinal[8] = (byte) ((msgLen >> 8) & 0xF);
        baFinal[9] = (byte) (msgLen & 0xF);

        for (int i = 10; i < 10 + msgLen; i++) {
            baFinal[i] = ba[i - 10];
        }

        byte byteXOR = (byte) 0;
        if (msgLen < 1) {
            baFinal[totalLen - 1] = byteXOR;
        } else {
            if (msgLen == 1) {
                baFinal[totalLen - 1] = baFinal[totalLen - 2];
            } else {
                byteXOR = ba[0];
                for (int i = 1; i < msgLen; i++) {
                    byteXOR = (byte) (byteXOR ^ ba[i]);
                }
                baFinal[totalLen - 1] = byteXOR;
            }
        }

        return baFinal;
    }

    public ResponsePackageInfo.ResponseType parseResponsePackage(byte[] ba) {
        if(ba == null || ba.length < MESSAGE_RESPONSE_LENGTH)
            return ResponseType.INVALID_RESPONSE;

        int dataLen = ba.length - MESSAGE_RESPONSE_LENGTH;
        byte[] baResponse = new byte[dataLen];

        int index = (int)ba[0] << 8 | (int)ba[1];
        if(index < 1 || index > MAX_MESSAGE_INDEX){
            mLogService.Log(LogService.LogType.ERROR, "Server Response : INDEX ERROR " + String.valueOf(index));
            return ResponseType.ERROR_INDEX;
        }
        else
            mLogService.Log(LogService.LogType.INFORMATION, "Server Response : INDEX " + String.valueOf(index));
        int respIndex = (int)ba[2] << 8 | (int)ba[3];
        if(index < 1 || index > MAX_MESSAGE_INDEX){
            mLogService.Log(LogService.LogType.ERROR, "Server Response : RESPONSE INDEX ERROR " + String.valueOf(respIndex));
            return ResponseType.ERROR_RESP_INDEX;
        }
        else
            mLogService.Log(LogService.LogType.INFORMATION, "Server Response : RESPONSE INDEX " + String.valueOf(respIndex));

        boolean isMultiple = ((int)ba[4] & 0x8) == 0x8;

        int respType = ((int)ba[4] & 0x7) << 8 | (int)ba[5];


        int packageTotal = (int)ba[6] << 8 | (int)ba[7];
        if(isMultiple == false && packageTotal != 0) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : MULTIPLE ERROR" + String.valueOf(packageTotal));
            return ResponseType.ERROR_PACK_TOTAL;
        }
        if(packageTotal < 1 || packageTotal > MAX_MESSAGE_INDEX){
            mLogService.Log(LogService.LogType.ERROR, "Server Response : PACKAGE TOTAL ERROR" + String.valueOf(packageTotal));
            return ResponseType.ERROR_PACK_TOTAL;
        }
        else
            mLogService.Log(LogService.LogType.INFORMATION, "Server Response : PACKAGE TOTAL " + String.valueOf(packageTotal));
        int packageIndex = (int)ba[8] << 8 | (int)ba[9];
        if(packageIndex < 1 || packageIndex > packageTotal){
            mLogService.Log(LogService.LogType.ERROR, "Server Response : PACKAGE INDEX ERROR" + String.valueOf(packageIndex));
            return ResponseType.ERROR_PACK_INDEX;
        }
        else
            mLogService.Log(LogService.LogType.INFORMATION, "Server Response : PACKAGE INDEX " + String.valueOf(index));
        int len = (int)ba[10] << 8 | (int)ba[11];
        if(len < 0 || len > MAX_MESSAGE_BODY_LENGTH) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : LENGTH ERROR" + String.valueOf(len));
            return ResponseType.ERROR_LEN;
        }
        if(len != dataLen) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : LENGTH MISMATCH ERROR - LEN " + String.valueOf(len) + " V.S. DATA LEN " + String.valueOf(dataLen));
            return ResponseType.ERROR_LEN_MISMATCH;
        }
        mLogService.Log(LogService.LogType.INFORMATION, "Server Response : LEN " + String.valueOf(len));

        byte crc = (byte)0;
        for(int i=0; i<len; i++) {
            if(i == 0)
                crc = ba[i+11];
            baResponse[i] = ba[i + 11];
        }
        if(crc != ba[ba.length -1]) {
            mLogService.Log(LogService.LogType.ERROR, "Server Response : CRC ERROR - CALC CRC " + String.valueOf(crc) + " V.S. CRC " + String.valueOf(ba[ba.length -1]));
            return ResponseType.ERROR_CRC;
        }
        else
            mLogService.Log(LogService.LogType.INFORMATION, "Server Response : CRC " + String.valueOf(crc));

        return ResponseType.ACK_DATA;
    }
}
