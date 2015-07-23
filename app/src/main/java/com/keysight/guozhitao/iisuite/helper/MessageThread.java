package com.keysight.guozhitao.iisuite.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created by cn569363 on 7/23/2015.
 */
public class MessageThread extends Thread implements Serializable {

    public final static int INSTRUMENT_MESSAGE = 0x00;
    public final static int SERVER_MESSAGE = 0x01;

    private Handler mHandler = null;
    private GlobalSettings mGlobalSettings = null;

    public MessageThread(GlobalSettings globalSettings) {
        super();

        mGlobalSettings = globalSettings;
    }

    /*
    public Handler getHandler() {
        return mHandler;
    }
    */

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    default:
                        break;
                    case INSTRUMENT_MESSAGE:
                        break;
                    case SERVER_MESSAGE:
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

    public void sendMessage(Message msg) {
        mHandler.sendMessage(msg);
    }

    public void sendOpenInstrumentMessage() {
        Message msg = new Message();
        msg.what = 0x00;
        mHandler.sendMessage(msg);
    }

    public void sendCloseInstrumentMessage() {
        Message msg = new Message();
        msg.what = 0x01;
        mHandler.sendMessage(msg);
    }

    public void sendDataToInstrumentMessage(String scpi) {
        Message msg = new Message();
        msg.what = 0x02;
        Bundle bundle = new Bundle();
        bundle.putCharSequence("scpi", scpi);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    public void sendOpenServerMessage() {
        Message msg = new Message();
        msg.what = 0x03;
        mHandler.sendMessage(msg);
    }

    public void sendCloseServerMessage() {
        Message msg = new Message();
        msg.what = 0x04;
        mHandler.sendMessage(msg);
    }

    public void sendDataToServerMessage(String log) {
        try {
            sendDataToServerMessage(log.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            sendDataToServerMessage(log.getBytes());
        }
    }

    public void sendDataToServerMessage(byte[] log) {
        Message msg = new Message();
        msg.what = 0x05;
        Bundle bundle = new Bundle();
        bundle.putByteArray("log", log);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}
