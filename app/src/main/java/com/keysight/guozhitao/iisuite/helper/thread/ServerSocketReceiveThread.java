package com.keysight.guozhitao.iisuite.helper.thread;

import android.os.Handler;
import android.os.Looper;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.msgresp.ResponsePackageInfo;
import com.keysight.guozhitao.iisuite.helper.msgresp.ServerPackageManager;

import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by cn569363 on 8/18/2015.
 */
public class ServerSocketReceiveThread extends Thread implements Serializable {

    public final static String ERROR_GET_INPUT_STREAM = "Fail to get the input stream.";
    public final static String ERROR_CLOSE_INPUT_STREAM = "Fail to close the input stream.";
    public final static String ERROR_RECEIVING_SERVER_DATA = "Fail to receive the server data.";

    private GlobalSettings mGlobalSettings;

    private Socket mSocket;
    private InputStream mServerInputStream;

    private Handler mHandler;

    private byte[] mReceiverBuffer;

    public ServerSocketReceiveThread(GlobalSettings gs) {
        mGlobalSettings = gs;

        mReceiverBuffer = new byte[ServerPackageManager.MAX_MESSAGE_BODY_LENGTH + ServerPackageManager.MAX_MESSAGE_BODY_MARGIN];
    }

    public void setSocket(Socket s) {
        try {
            if (s == null) {
                if (mServerInputStream != null) {
                    mServerInputStream.close();
                    mServerInputStream = null;
                }
            } else {
                mSocket = s;
                mServerInputStream = s.getInputStream();
            }
        }
        catch(Exception e) {
            if(s == null)
                mGlobalSettings.toastMessage(ERROR_CLOSE_INPUT_STREAM + "\n" + e.getMessage());
            else
                mGlobalSettings.toastMessage(ERROR_GET_INPUT_STREAM + "\n" + e.getMessage());
        }
        finally {
            if(s == null)
                mServerInputStream = null;
        }
    }

    public boolean isSocketActive() {
        return mSocket != null;
    }

    @Override
    public void run() {
        while(mSocket != null) {
            try {
                int len = mServerInputStream.read(mReceiverBuffer);
                ResponsePackageInfo rpi = mGlobalSettings.getServerPackageManager().parseResponsePackage(mReceiverBuffer, len);
            }
            catch (Exception e) {
                mGlobalSettings.toastMessage(ERROR_RECEIVING_SERVER_DATA + "\n" + e.getMessage());
            }
        }
    }
}
