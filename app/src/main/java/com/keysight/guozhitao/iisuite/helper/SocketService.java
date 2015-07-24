package com.keysight.guozhitao.iisuite.helper;

import com.keysight.guozhitao.iisuite.helper.SocketThread.InstrumentSocketThread;
import com.keysight.guozhitao.iisuite.helper.SocketThread.ServerSocketThread;

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

    private InstrumentSocketThread mInstrumentSendThread;
    private ServerSocketThread mServerSendThread;

    public SocketService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;

        mInstrumentSendThread = new InstrumentSocketThread(globalSettings);
        mServerSendThread = new ServerSocketThread(globalSettings);

        mInstrumentSendThread.start();
        mServerSendThread.start();
    }

    public Socket getInstrumentSocket() { return mInstrumentSocket; }

    public boolean isInstrumentSocketAvailable() {
        return mInstrumentSocket != null;
    }

    public Socket getServerSocket() { return mServerSocket; }

    public boolean isServerSocketAvailable() { return mServerSocket != null; }

    private void openServerSocket() throws IOException {
        closeServerSocket();

        mServerSocket = new Socket(mGlobalSettings.getCurrentServerInfo().getServer(), GlobalSettings.SERVER_SOCKET_PORT);
    }

    public void safeOpenServerSocket() {
        try {
            openServerSocket();
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    private void closeServerSocket() throws IOException {
        if(!mServerSocket.isClosed()) {
            mServerSocket.close();
            mServerSocket = null;
        }
    }

    public void safeCloseServerSocket() {
        try {
            closeServerSocket();
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    protected void sendServerData(String s) throws IOException {
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
        sendServerData(ba);
    }

    public void safeSendServerData(String sMessage){
        try {
            sendServerData(sMessage);
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    private void sendServerData(byte[] baMessage) throws IOException {
        if(mServerSocket == null)
            openServerSocket();



        if(!mGlobalSettings.getCurrentInstrumentInfo().getConnected())
            closeServerSocket();
    }

    public void safeSendServerData(byte[] baMessage){
        try {
            sendServerData(baMessage);
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    public InstrumentSocketThread getInstrumentSendThread() { return mInstrumentSendThread; }

    public ServerSocketThread getServerSendThread() { return mServerSendThread; }
}
