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

Server :
Socket Package Definition :
INDEX       : 2 bytes
PROPERTY    : 1 byte
              bit7 ~ bit2 : reserved
              bit1 : message type
                     0 - pulse
                     1 - data
              bit0 : 1 - multiple, 0 - single
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

    public enum MessageType {
        Pulse,
        Data
    }

    private final static int MAX_MESSAGE_INDEX = 65535;
    private final static int MAX_MESSAGE_INFORMATION_LENGTH = 10;
    private final static int MAX_MESSAGE_BODY_LENGTH = 502;

    private static int mMessageIndex = 0;

    private ServerPackageManager() {
    }

    private static final ServerPackageManager mServerPackageManager = new ServerPackageManager();

    public static ServerPackageManager getInstance() {
        return mServerPackageManager;
    }

    public ArrayList<byte[]> getMessages(MessageType msgType, String s) {
        if (s == null || s.length() < 1)
            return getMessages(msgType, new byte[]{});
        else {
            try {
                byte[] bs = s.getBytes("UTF-8");
                return getMessages(msgType, bs);
            } catch (UnsupportedEncodingException e) {
                byte[] bs = s.getBytes();
                return getMessages(msgType, bs);
            }
        }
    }

    public ArrayList<byte[]> getMessages(MessageType msgType, byte[] ba) {
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
            messageList.add(getMessage(msgType, baItem, messageCount, messageIndex++));
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

    private byte[] getMessage(MessageType msgType, byte[] ba, int totalPackage, int indexPackage) {
        if (mMessageIndex > MAX_MESSAGE_INDEX || mMessageIndex < 0)
            mMessageIndex = 0;

        int msgLen = ba.length;
        int totalLen = MAX_MESSAGE_INFORMATION_LENGTH + msgLen;
        byte[] baFinal = new byte[totalLen];

        baFinal[0] = (byte) ((mMessageIndex >> 8) & 0xF);
        baFinal[1] = (byte) (mMessageIndex & 0xF);
        if (msgType == MessageType.Pulse)
            baFinal[2] = (byte) 0;
        else
            baFinal[2] = (byte) 2;
        if (totalPackage > 1)
            baFinal[2] = (byte) (baFinal[2] | 0x1);

        baFinal[3] = (byte) ((totalPackage >> 8) & 0xF);
        baFinal[4] = (byte) (totalPackage & 0xF);
        baFinal[5] = (byte) ((indexPackage >> 8) & 0xF);
        baFinal[6] = (byte) (indexPackage & 0xF);

        baFinal[7] = (byte) ((msgLen >> 8) & 0xF);
        baFinal[8] = (byte) (msgLen & 0xF);

        for (int i = 9; i < 9 + msgLen; i++) {
            baFinal[i] = ba[i - 9];
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
}
