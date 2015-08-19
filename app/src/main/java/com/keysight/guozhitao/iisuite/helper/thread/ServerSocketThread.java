package com.keysight.guozhitao.iisuite.helper.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.keysight.guozhitao.iisuite.activity.MainActivity;
import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.LogService;
import com.keysight.guozhitao.iisuite.helper.ServerInfo;
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
    public final static int RCV_DATA_FROM_SERVER = 0x03;

    public final static String MSG_TRY_TO_CREATE_SERVER_SOCKET = "Try to create the server socket.";
    public final static String MSG_SOCKET_CREATION_OK = "The server socket is created.";
    public final static String MSG_SOCKET_CREATION_FAILURE = "Fail to create the server socket.";
    public final static String MSG_TRY_TO_CLOSE_SERVER_SOCKET = "Try to close the server socket.";
    public final static String MSG_SOCKET_CLOSE_OK = "The server socket is closed.";
    public final static String MSG_SOCKET_CLOSE_FAILURE = "Fail to close the server socket.";
    public final static String MSG_SOCKET_DATA_FAILURE = "Fail to send data to the server socket.";
    public final static String MSG_NO_SELECTED_SERVER = "No selected server.";
    public final static String MSG_NO_ACTIVE_SERVER_SOCKET = "No active socket to the server.";
    public final static String UTF8_CONVERSION_ERROR = "Fail to convert the string to be of the UTF-8 format.";

    /*
    Be set when OPEN_SERVER and be reset when CLOSE_SERVER
     */
    private String mServer = "";

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    private InputStream mServerInputStream;
    private OutputStream mServerOutputStream;

    private Socket mServerSocket = null;
    private boolean mInSocketOperation = false;

    private ServerSocketReceiveThread mRcvThread;

    public ServerSocketThread(GlobalSettings globalSettings) {
        super();
        mGlobalSettings = globalSettings;
    }

    public Handler getServerSocketThreadHandler() {
        return mHandler;
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
                        mServer = ((CharSequence)msg.getData().get(GlobalSettings.KEY_SERVER)).toString();
                        safeOpenServerSocket();
                        if(isServerSocketAvailable()){
                            mRcvThread = new ServerSocketReceiveThread(mGlobalSettings);
                            mRcvThread.setSocket(mServerSocket);
                            mRcvThread.start();
                        }
                        break;
                    case CLOSE_SERVER:
                        if(mRcvThread != null)
                            mRcvThread.setSocket(null);
                        safeCloseServerSocket();
                        mServer = "";
                        mGlobalSettings.setCurrentServerInfo(-1);
                        break;
                    case SEND_DATA_TO_SERVER:
                        MessagePackageInfo.MessagePackageType t = (MessagePackageInfo.MessagePackageType)msg.getData().get("TYPE");
                        String s = ((CharSequence)msg.getData().get(GlobalSettings.KEY_MSG_SHORT)).toString();
                        ArrayList<MessagePackageInfo> msgArray = mGlobalSettings.getServerPackageManager().composeMsgs(t, s);
                        for(MessagePackageInfo mpi : msgArray) {
                            safeSendServerData(mpi.getSource());
                        }
                        mInSocketOperation = false;
                        break;
                    case RCV_DATA_FROM_SERVER:
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
            mServerSocket = new Socket(mServer, GlobalSettings.SERVER_SOCKET_PORT);
            mServerOutputStream = mServerSocket.getOutputStream();
            mGlobalSettings.setCurrentServerInfo(mServer);
        }
        catch(Exception e) {
            mGlobalSettings.toastMessage(MSG_SOCKET_CREATION_FAILURE + "\n" + e.getMessage());

            mServerSocket = null;
        }
    }

    public void safeCloseServerSocket() {
        if(mServerSocket == null)
            return;
        try {
            if(!mServerSocket.isClosed()) {
                mServerOutputStream.close();
                mServerSocket.close();
            }
        }
        catch(Exception e) {
            mGlobalSettings.toastMessage(MSG_SOCKET_CLOSE_FAILURE + "\n" + e.getMessage());
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
            ba = s.getBytes(GlobalSettings.KEY_UTF8);
        }
        catch (UnsupportedEncodingException e) {
            mGlobalSettings.toastMessage(UTF8_CONVERSION_ERROR + "\n" + e.getMessage());
            ba = s.getBytes();
        }
        sendServerData(ba);
    }

    private void sendServerData(byte[] ba) throws IOException {
        if(ba == null || ba.length < 1)
            return;
        if(mGlobalSettings.getCurrentServerInfo() == null)
            mGlobalSettings.toastMessage(MSG_NO_SELECTED_SERVER);
        else {
            if (mServerSocket == null) {
                ServerInfo si = mGlobalSettings.getServerInfo(mServer);
                if (si != null &&  si.getAutoConnection() == true)
                    safeOpenServerSocket();

                if (mServerSocket != null) {
                    mServerOutputStream.write(ba);
                    mServerOutputStream.flush();
                }
                else
                    mGlobalSettings.toastMessage(MSG_NO_ACTIVE_SERVER_SOCKET);
            }
            else
                mGlobalSettings.toastMessage(MSG_NO_ACTIVE_SERVER_SOCKET);
        }
    }

    public void safeSendServerData(String s){
        try {
            sendServerData(s);
        }
        catch(Exception e) {
            mGlobalSettings.toastMessage(MSG_SOCKET_DATA_FAILURE + "\n" + e.getMessage());
            safeCloseServerSocket();
        }
    }

    public void safeSendServerData(byte[] ba){
        try {
            sendServerData(ba);
        } catch (Exception e) {
            mGlobalSettings.toastMessage(MSG_SOCKET_DATA_FAILURE + "\n" + e.getMessage());
            safeCloseServerSocket();
        }
    }
}
