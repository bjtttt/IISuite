package com.keysight.guozhitao.iisuite.helper;

import java.io.Serializable;

/**
 * Created by cn569363 on 7/21/2015.
 */
public class LogService implements Serializable {

    public enum LogType {
        NONE,
        INFORMATION,
        OK,
        ERROR,
        WARNING
    }

    private GlobalSettings mGlobalSettings;

    public LogService(GlobalSettings globalSettings) {
        mGlobalSettings = globalSettings;
    }

    public void Log(LogType lt, String msg) {

    }
}
