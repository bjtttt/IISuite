package com.keysight.guozhitao.iisuite.helper;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by cn569363 on 7/20/2015.
 */
public class SocketService implements Serializable {

    private GlobalSettings mGlobalSettings;

    private Socket mSocket = null;

    public SocketService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;
    }

    public void OpenSocket() throws IOException {
        CloseSocket();

        mSocket = new Socket(mGlobalSettings.getCurrentServerInfo().getServer(), GlobalSettings.SOCKET_PORT);
    }

    public void CloseSocket() throws IOException {
        if(!mSocket.isClosed()) {
            mSocket.close();
            mSocket = null;
        }
    }

    public void SendData(byte[] ba) throws IOException {
        if(mSocket == null)
            OpenSocket();
        
        if(!mGlobalSettings.getCurrentInstrumentInfo().getConnected())
            CloseSocket();
    }
}
