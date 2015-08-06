package com.keysight.guozhitao.iisuite.helper.SocketThread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class InstrumentSocketThread extends Thread implements Serializable {

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    private InputStream mInstrumentInputStream;
    private OutputStream mInstrumentOutputStream;

    private Socket mInstrumentSocket = null;

    public InstrumentSocketThread(GlobalSettings globalSettings) {
        super();

        mGlobalSettings = globalSettings;
    }

    public Socket getInstrumentSocket() { return mInstrumentSocket; }

    public boolean isInstrumentSocketAvailable() {
        return mInstrumentSocket != null;
    }

    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    default:
                        break;
                }
            }
        };
        Looper.loop();
    }
}
