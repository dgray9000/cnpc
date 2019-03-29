package com.graysoda.cnpc;

import android.os.Environment;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public final class Constants {
    public static final String iconUrl = "https://raw.githubusercontent.com/atomiclabs/cryptocurrency-icons/master/32/icon/";
    public static final String RESULT = "result";
    public static final String ALLOWANCE = "allowance";
    public static final String SYMBOL = "symbol";
    public static final String ID = "id";
    public static final String BASE = "base";
    public static final String QUOTE = "quote";
    public static final String ROUTE = "route";
    public static final String NAME = "name";
    public static final String EXCHANGE = "exchange";
    public static final String PAIR = "pair";
    public static final String ACTIVE = "active";
    public static final String channelId = "Prices";
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    public static final String REVISION = "revision";

    public static org.apache.log4j.Logger getLogger(Class clazz){

        File logFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "log/cnpc.log");

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                logFile.setReadable(true);
                logFile.setWritable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final LogConfigurator configurator = new LogConfigurator();
        configurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "log/cnpc.log");
        configurator.setRootLevel(Level.ALL);
        configurator.setLevel("org.apache",Level.ALL);
        configurator.setUseFileAppender(true);
        configurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        configurator.setMaxFileSize(1024 * 1024);
        configurator.setImmediateFlush(true);
        configurator.configure();
        Logger logger = Logger.getLogger(clazz);
        return logger;
    }
}
