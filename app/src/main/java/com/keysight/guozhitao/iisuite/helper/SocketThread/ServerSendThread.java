package com.keysight.guozhitao.iisuite.helper.SocketThread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class ServerSendThread extends Thread {

    public final static int OPEN_SERVER = 0x00;
    public final static int CLOSE_SERVER = 0x01;
    public final static int SEND_DATA_TO_SERVER = 0x02;

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    public ServerSendThread(GlobalSettings globalSettings) {
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
                        break;
                    case CLOSE_SERVER:
                        //sendCloseServerMessage();
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
