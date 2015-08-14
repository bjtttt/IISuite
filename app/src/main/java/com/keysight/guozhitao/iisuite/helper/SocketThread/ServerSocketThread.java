package com.keysight.guozhitao.iisuite.helper.socketthread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.LogService;
import com.keysight.guozhitao.iisuite.helper.msgresp.MessagePackageInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class ServerSocketThread extends Thread implements Serializable {

    public final static int OPEN_SERVER = 0x00;
    public final static int CLOSE_SERVER = 0x01;
    public final static int SEND_DATA_TO_SERVER = 0x02;

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    private InputStream mServerInputStream;
    private OutputStream mServerOutputStream;

    private Socket mServerSocket = null;
    private boolean mInSocketOperation = false;

    public ServerSocketThread(GlobalSettings globalSettings) {
        super();

        mGlobalSettings = globalSettings;
    }

    public synchronized void setInSocketOperation(boolean inSocketOperation) {
        mInSocketOperation = inSocketOperation;
    }

    public synchronized boolean getInSocketOperation() {
        return mInSocketOperation;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                setInSocketOperation(true);
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
                        MessagePackageInfo.MessagePackageType t = (MessagePackageInfo.MessagePackageType)msg.getData().get("TYPE");
                        String s = (String)msg.getData().get("MSG");
                        ArrayList<MessagePackageInfo> msgArray = mGlobalSettings.getServerPackageManager().composeMsgs(t, s);
                        for(MessagePackageInfo mpi : msgArray) {
                            safeSendServerData(mpi.getSource());
                        }
                        break;
                }
                setInSocketOperation(false);
            }
        };
        Looper.loop();
    }

    public Socket getServerSocket() { return mServerSocket; }

    public boolean isServerSocketAvailable() { return mServerSocket != null; }

    public void safeOpenServerSocket() {
        safeCloseServerSocket();

        try {
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "Try to create the server socket.");

            mServerSocket = new Socket(mGlobalSettings.getCurrentServerInfo().getServer(), GlobalSettings.SERVER_SOCKET_PORT);
            mServerOutputStream = mServerSocket.getOutputStream();

            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "The server socket is created.");
        }
        catch(Exception e) {
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "Fail to create the server socket.");
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, e.getMessage());

            mServerSocket = null;
        }
    }

    public void safeCloseServerSocket() {
        if(mServerSocket == null)
            return;

        try {
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "Try to close the server socket.");

            if(!mServerSocket.isClosed()) {
                mServerSocket.close();
            }

            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "The server socket is closed.");
        }
        catch(Exception e) {
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "Fail to close the server socket.");
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, e.getMessage());
        }
        finally {
            mServerSocket = null;
        }
    }

    private void sendServerData(String s) throws IOException {
        if(s == null || s.isEmpty())
            return;

        byte[] ba = null;
        try {
            ba = s.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            ba = s.getBytes();
        }
        sendServerData(ba);
    }

    private void sendServerData(byte[] ba) throws IOException {
        if(ba == null || ba.length < 1)
            return;

        if(mServerSocket == null && mGlobalSettings.getCurrentServerInfo().getAutoConnection() == true)
            safeOpenServerSocket();
        else {
            mGlobalSettings.getLogService().Log(LogService.LogType.ERROR, "No socket to server.");
            return;
        }

        mServerOutputStream.write(ba);
    }

    public void safeSendServerData(String s){
        try {
            sendServerData(s);
        }
        catch(Exception e) {
            mServerSocket = null;
        }
    }

    public void safeSendServerData(byte[] ba){
        try {
            sendServerData(ba);
        }
        catch(Exception e) {
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, "Fail to send data to server.");
            mGlobalSettings.getLogService().Log(LogService.LogType.WARNING, e.getMessage());
            safeCloseServerSocket();
        }
    }
}
