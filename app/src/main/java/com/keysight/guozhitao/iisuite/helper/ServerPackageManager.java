package com.keysight.guozhitao.iisuite.helper;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/*

To Server :
Socket Package Definition :
INDEX       : 2 bytes
PROPERTY    : 2 bytes
              bit15 : message type
                      0 - pulse
                      1 - data
              bit14 : 1 - multiple, 0 - single
              bit7 ~ bit0 : valid only when bit15 is 1
                             0 - SyncInstrumentFromServer
                             1 - SyncInstrumentToServer
                             2 - SyncInstrumentMergeServer
                             3 - SyncServerFromServer
                             4 - SyncServerToServer
                             5 - SyncServerMergeServer
PACKAGE     : 4 bytes
              bit31 ~ bit16 : total
              bit15 ~ bit0  : index
DATA LENGTH : 2 bytes
DATA        : 0 ~ 502 bytes
CRC         : 1 byte

From Server :
Socket Package Definition :
INDEX       : 2 bytes
RERPONSE INDEX  : 2 bytes
PROPERTY    : 2 bytes
              bit15 : 1 - multiple, 0 - single
              bit7 ~ bit0 : valid only when bit15 is 1
                             0 - OK/ACK
                             1 - Error : CRC
                             2 - Error : Data Length
                             3 - Error : Message Type
                             4 - Error : Package Number
                             5 - Error :
PACKAGE     : 4 bytes
              bit31 ~ bit16 : total
              bit15 ~ bit0  : index
DATA LENGTH : 2 bytes
DATA        : 0 ~ 502 bytes
CRC         : 1 byte

 */

/**
 * Created by cn569363 on 7/24/2015.
 */
public class ServerPackageManager implements Serializable {

    public enum MessageCategory {
        Pulse,
        Data
    }

    public enum MessageType {
        SyncInstrumentFromServer,
        SyncInstrumentToServer,
        SyncInstrumentMergeServer,
        SyncServerFromServer,
        SyncServerToServer,
        SyncServerMergeServer
    }

    public enum ResponseType {
        Pulse,
        Error,

    }

    public final static int MAX_MESSAGE_INDEX = 65535;
    public final static int MAX_MESSAGE_INFORMATION_LENGTH = 11;
    public final static int MAX_MESSAGE_RESPONSE_LENGTH = 13;
    public final static int MAX_MESSAGE_BODY_LENGTH = 502;

    private static int mMessageIndex = 0;

    private ServerPackageManager() {
    }

    private static final ServerPackageManager mServerPackageManager = new ServerPackageManager();

    public static ServerPackageManager getInstance() {
        return mServerPackageManager;
    }

    public ArrayList<byte[]> getMessages(MessageCategory msgCat, MessageType msgType, String s) {
        if (s == null || s.length() < 1)
            return getMessages(msgCat, msgType, new byte[]{});
        else {
            try {
                byte[] bs = s.getBytes("UTF-8");
                return getMessages(msgCat, msgType, bs);
            } catch (UnsupportedEncodingException e) {
                byte[] bs = s.getBytes();
                return getMessages(msgCat, msgType, bs);
            }
        }
    }

    public ArrayList<byte[]> getMessages(MessageCategory msgCat, MessageType msgType, byte[] ba) {
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
            messageList.add(getMessage(msgCat, msgType, baItem, messageCount, messageIndex++));
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

    private byte[] getMessage(MessageCategory msgCat, MessageType msgType, byte[] ba, int totalPackage, int indexPackage) {
        if (mMessageIndex > MAX_MESSAGE_INDEX || mMessageIndex < 1)
            mMessageIndex = 1;

        int msgLen = ba.length;
        int totalLen = MAX_MESSAGE_INFORMATION_LENGTH + msgLen;
        byte[] baFinal = new byte[totalLen];

        baFinal[0] = (byte) ((mMessageIndex >> 8) & 0xF);
        baFinal[1] = (byte) (mMessageIndex & 0xF);
        if (msgCat == MessageCategory.Pulse)
            baFinal[2] = (byte) 0;
        else
            baFinal[2] = (byte) 8;
        if (totalPackage > 1)
            baFinal[2] = (byte) (baFinal[2] | 0x4);

        switch(msgType) {
            default:
                throw new IllegalArgumentException("ServerPackageManager::getMessage : Wrong message type - " + msgType.toString());
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

    //private
}
