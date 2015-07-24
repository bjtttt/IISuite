package com.keysight.guozhitao.iisuite.helper.SocketThread;

import android.os.Handler;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class InstrumentSocketThread extends Thread {

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings;

    public InstrumentSocketThread(GlobalSettings globalSettings) {
        super();

        mGlobalSettings = globalSettings;
    }
}
