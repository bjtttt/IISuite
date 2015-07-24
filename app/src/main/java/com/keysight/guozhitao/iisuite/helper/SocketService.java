package com.keysight.guozhitao.iisuite.helper;

import com.keysight.guozhitao.iisuite.helper.SocketThread.InstrumentSocketThread;
import com.keysight.guozhitao.iisuite.helper.SocketThread.ServerSocketThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/*

Instrument :

Server :
Socket Package Definition :
INDEX       : 2 bytes
PROPERTY    : 1 byte
              bit7 ~ bit4 : reserved
              bit3 ~ bit1 : message type
                            0 - pulse
                            1 - instrument data
                            2 - scpi data
                            3 - log data
              bit0 : 1 - multiple, 0 - single
PACKAGE     : 4 bytes
              bit31 ~ bit16 : total
              bit15 ~ bit0  : index
DATA LENGTH : 2 bytes
DATA        : 0 ~ 502 bytes
CRC         : 1 byte

 */

/**
 * Created by cn569363 on 7/20/2015.
 */
public class SocketService implements Serializable {

    private GlobalSettings mGlobalSettings;

    private InstrumentSocketThread mInstrumentSockethread;
    private ServerSocketThread mServerSocketThread;

    public SocketService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;

        mInstrumentSockethread = new InstrumentSocketThread(globalSettings);
        mServerSocketThread = new ServerSocketThread(globalSettings);

        mInstrumentSockethread.start();
        mServerSocketThread.start();
    }

    public InstrumentSocketThread getInstrumentSendThread() { return mInstrumentSockethread; }

    public ServerSocketThread getServerSendThread() { return mServerSocketThread; }

    /*
    public void StopInstrumentSocketThread() {
    }

    public void StopServerSocketThread() {
    }
    */
}
