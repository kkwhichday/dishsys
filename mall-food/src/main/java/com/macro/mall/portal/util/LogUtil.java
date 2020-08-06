package com.macro.mall.portal.util;


import com.macro.mall.portal.service.impl.WxPayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    private static final Logger Log = LoggerFactory.getLogger(WxPayServiceImpl.class);

    private final static String TAG = "ottpay";

    public static void d(String logString) {
        if (!isDebug())
            return;
        Log.debug(TAG, logString);
    }

    public static void d(String paramString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.debug(TAG, paramString, paramThrowable);
    }

    public static void e(Object paramObject) {
        if (!isDebug())
            return;
        System.err.println(paramObject);
    }

    public static void e(String logString) {
        if (!isDebug())
            return;
        Log.error(TAG, logString);
    }

    public static void e(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.error(TAG, logString, paramThrowable);
    }

    public static void i(String logString) {
        if (!isDebug())
            return;
        Log.info(TAG, logString);
    }

    public static void i(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.info(TAG, logString, paramThrowable);
    }

    public static boolean isDebug() {
        return false;
    }

    public static void p(Object paramObject) {
        if (!isDebug())
            return;
        System.out.println(paramObject);
    }

    public static void v(String logString) {
        if (!isDebug())
            return;
        Log.info(TAG, logString);
    }

    public static void v(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.info(TAG, logString, paramThrowable);
    }

    public static void w(String logString) {
        if (!isDebug())
            return;
        Log.warn(TAG, logString);
    }

    public static void w(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.warn(TAG, logString, paramThrowable);
    }

    public static void w(Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.warn(TAG, paramThrowable);
    }

}