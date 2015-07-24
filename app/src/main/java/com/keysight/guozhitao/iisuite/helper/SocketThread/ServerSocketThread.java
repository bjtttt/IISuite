package com.keysight.guozhitao.iisuite.helper.SocketThread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class ServerSocketThread extends Thread {

    public final static int OPEN_SERVER = 0x00;
    public final static int CLOSE_SERVER = 0x01;
    public final static int SEND_DATA_TO_SERVER = 0x02;

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

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
                        mGlobalSettings.getSocketService().safeOpenServerSocket();
                        break;
                    case CLOSE_SERVER:
                        mGlobalSettings.getSocketService().safeCloseServerSocket();
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
}
