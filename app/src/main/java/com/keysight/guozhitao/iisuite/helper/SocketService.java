package com.keysight.guozhitao.iisuite.helper;

import com.keysight.guozhitao.iisuite.helper.SocketThread.InstrumentReadThread;
import com.keysight.guozhitao.iisuite.helper.SocketThread.InstrumentSendThread;
import com.keysight.guozhitao.iisuite.helper.SocketThread.ServerReadThread;
import com.keysight.guozhitao.iisuite.helper.SocketThread.ServerSendThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
    private Socket mServerSocket = null;
    private InputStream mInstrumentInputStream;
    private OutputStream mInstrumentOutputStream;
    private InputStream mServerInputStream;
    private OutputStream mServerOutputStream;

    private InstrumentSendThread mInstrumentSendThread;
    private InstrumentReadThread mInstrumentReadThread;
    private ServerSendThread mServerSendThread;
    private ServerReadThread mServerReadThread;

    public SocketService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;

        mInstrumentSendThread = new InstrumentSendThread(globalSettings);
        mInstrumentReadThread = new InstrumentReadThread(globalSettings);
        mServerSendThread = new ServerSendThread(globalSettings);
        mServerReadThread = new ServerReadThread(globalSettings);

        mInstrumentSendThread.start();
        mInstrumentReadThread.start();
        mServerSendThread.start();
        mServerReadThread.start();
    }

    public Socket getInstrumentSocket() { return mInstrumentSocket; }

    public boolean IsInstrumentSocketAvailable() {
        return mInstrumentSocket != null;
    }

    public Socket getServerSocket() { return mServerSocket; }

    public boolean IsServerSocketAvailable() { return mServerSocket != null; }

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

    public void SafeCloseServerSocket() {
        try {
            CloseServerSocket();
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    protected void SendServerData(String s) throws IOException {
        if(s == null)
            throw new IllegalArgumentException("SocketService::SendData(null)");
        if(s.isEmpty())
            throw new IllegalArgumentException("SocketService::SendData(\"\")");

        byte[] ba = null;
        try {
            ba = s.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            ba = s.getBytes();
        }
        SendServerData(ba);
    }

    public void SafeSendServerData(String sMessage){
        try {
            SendServerData(sMessage);
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    private void SendServerData(byte[] baMessage) throws IOException {
        if(mServerSocket == null)
            OpenServerSocket();



        if(!mGlobalSettings.getCurrentInstrumentInfo().getConnected())
            CloseServerSocket();
    }

    public void SafeSendServerData(byte[] baMessage){
        try {
            SendServerData(baMessage);
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    public InstrumentSendThread getInstrumentSendThread() { return mInstrumentSendThread; }

    public InstrumentReadThread getInstrumentReaddThread() { return mInstrumentReadThread; }

    public ServerSendThread getServerSendThread() { return mServerSendThread; }

    public ServerReadThread getServerReadThread() { return mServerReadThread; }
}
