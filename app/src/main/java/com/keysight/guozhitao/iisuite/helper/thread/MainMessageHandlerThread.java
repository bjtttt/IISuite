package com.keysight.guozhitao.iisuite.helper.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

import java.io.Serializable;

/**
 * Created by cn569363 on 8/16/2015.
 */

public class MainMessageHandlerThread extends HandlerThread implements
            Serializable,
            Handler.Callback {

    public final static int TOAST_MSG = 0;
    public final static int PROGRESS_DIALOG_SHOW = 1;
    public final static int PROGRESS_DIALOG_HIDE = 2;

    private GlobalSettings mGlobalSettings;
    private Activity mMainActivity;
    private ProgressDialog mProgressDialog;

    private Handler handler, callback;

    public MainMessageHandlerThread(GlobalSettings globalSettings, Activity act, String name) {
        super(name);

        mGlobalSettings = globalSettings;
        mMainActivity = act;
    }

    public MainMessageHandlerThread(GlobalSettings globalSettings, Activity act, String name, int priority) {
        super(name, priority);

        mGlobalSettings = globalSettings;
        mMainActivity = act;
    }

    public void setCallback(Handler cb) {
        callback = cb;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper(), this);
        mGlobalSettings.setMainHandler(handler);
    }

    @Override
    public boolean handleMessage(Message msg) {
        callback.sendMessage(msg);
        return true;
    }
}
