package com.keysight.guozhitao.iisuite.helper;

import android.os.Handler;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class MessageThread extends Thread implements Serializable {

    private Handler mHandler = null;

    public void MessageThread() {

    }

    public Handler getHandler() {
        return mHandler;
    }

    private void createHandler() {
        mHandler = new Handler() {
            
        }
    }

    @Override
    public void run() {
        super.run();
    }
}
