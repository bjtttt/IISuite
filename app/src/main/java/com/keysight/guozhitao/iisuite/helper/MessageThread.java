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

    public final static int OPEN_INSTRUMENT = 0x00;
    public final static int CLOSE_INSTRUMENT = 0x01;
    public final static int SEND_DATA_TO_INSTRUMENT = 0x02;
    public final static int OPEN_SERVER = 0x03;
    public final static int CLOSE_SERVER = 0x04;
    public final static int SEND_DATA_TO_SERVER = 0x05;

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
                    case OPEN_INSTRUMENT:
                        //sendOpenInstrumentMessage();
                        break;
                    case CLOSE_INSTRUMENT:
                        //sendCloseInstrumentMessage();
                        break;
                    case SEND_DATA_TO_INSTRUMENT:
                        //sendDataToInstrumentMessage(msg.getData().getString("scpi"));
                        break;
                    case OPEN_SERVER:
                        //sendOpenServerMessage();
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
