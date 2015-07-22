package com.keysight.guozhitao.iisuite.helper;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/*

Instrument :

Server :
Socket Package Definition :
INDEX       : 2 bytes
PROPERTY    : 1 byte
              bit7 ~ bit4 : reserved
              bit3 ~ bit1 : message type
                            0 - pulse
                            1 - instrument data
                            2 - scpi data
                            3 - log data
              bit0 : 1 - multiple, 0 - single
PACKAGE     : 4 bytes
              bit31 ~ bit16 : total
              bit15 ~ bit0  : index
DATA LENGTH : 2 bytes
DATA        : 0 ~ 502 bytes
CRC         : 1 byte

 */

/**
 * Created by cn569363 on 7/20/2015.
 */
public class SocketService implements Serializable {

    private GlobalSettings mGlobalSettings;

    private Socket mInstrumentSocket = null;

    public Socket getInstrumentSocket() {
        return mInstrumentSocket;
    }

    private Socket mServerSocket = null;

    public Socket getServerSocket() {
        return mServerSocket;
    }

    public SocketService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;
    }

    private void OpenServerSocket() throws IOException {
        CloseServerSocket();

        mServerSocket = new Socket(mGlobalSettings.getCurrentServerInfo().getServer(), GlobalSettings.SERVER_SOCKET_PORT);
    }

    public void SafeOpenServerSocket() {
        try {
            OpenServerSocket();
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    private void CloseServerSocket() throws IOException {
        if(!mServerSocket.isClosed()) {
            mServerSocket.close();
            mServerSocket = null;
        }
    }

    private void SendServerData(String s) throws IOException {
        if(s == null)
            throw new IllegalArgumentException("SocketService::SendData(null)");
        if(s.isEmpty())
            throw new IllegalArgumentException("SocketService::SendData(\"\")");

        byte[] ba = s.getBytes();
        SendServerData(ba);
    }

    private void SendServerData(byte[] ba) throws IOException {
        if(mServerSocket == null)
            OpenServerSocket();



        if(!mGlobalSettings.getCurrentInstrumentInfo().getConnected())
            CloseServerSocket();
    }
}
