package com.keysight.guozhitao.iisuite.helper.thread;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/16/2015.
 */
public class MainMessageThread extends Thread implements Serializable {

    public final static int TOAST_MSG = 0;

    private GlobalSettings mGlobalSettings;
    private Handler mHandler;
    private Activity mMainActivity;

    public MainMessageThread(GlobalSettings globalSettings, Activity act) {
        super();

        mGlobalSettings = globalSettings;
        mMainActivity = act;
    }

    @Override
    public void run() {
        //super.run();

        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch(message.what) {
                    default:
                        break;
                    case TOAST_MSG:
                        String msg = message.getData().getCharSequence(GlobalSettings.KEY_MSG_SHORT).toString();
                        Toast.makeText(mMainActivity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        mGlobalSettings.setMainHandler(mHandler);
        Looper.loop();
    }
}
