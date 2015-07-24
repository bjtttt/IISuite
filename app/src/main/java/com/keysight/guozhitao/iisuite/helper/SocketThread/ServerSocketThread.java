package com.keysight.guozhitao.iisuite.helper.SocketThread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class ServerSocketThread extends Thread {

    public final static int OPEN_SERVER = 0x00;
    public final static int CLOSE_SERVER = 0x01;
    public final static int SEND_DATA_TO_SERVER = 0x02;

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    private InputStream mServerInputStream;
    private OutputStream mServerOutputStream;

    private Socket mServerSocket = null;

    public ServerSocketThread(GlobalSettings globalSettings) {
        super();

        mGlobalSettings = globalSettings;
    }

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    default:
                        break;
                    case OPEN_SERVER:
                        safeOpenServerSocket();
                        break;
                    case CLOSE_SERVER:
                        safeCloseServerSocket();
                        break;
                    case SEND_DATA_TO_SERVER:
                        //sendDataToServerMessage(msg.getData().getByteArray("log"));
                        break;
                }
            }
        };
    }

    @Override
    public void run() {
        //super.run();

        Looper.prepare();
        createHandler();
        Looper.loop();
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
}
