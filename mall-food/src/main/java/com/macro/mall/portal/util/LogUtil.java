package com.ottapppay.driver.util;

import android.util.Log;

import static com.ottapppay.driver.BuildConfig.DEBUG;

public class LogUtil {

    private final static String TAG = "ottpay";

    public static void d(String logString) {
        if (!isDebug())
            return;
        Log.d(TAG, logString);
    }

    public static void d(String paramString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.d(TAG, paramString, paramThrowable);
    }

    public static void e(Object paramObject) {
        if (!isDebug())
            return;
        System.err.println(paramObject);
    }

    public static void e(String logString) {
        if (!isDebug())
            return;
        Log.e(TAG, logString);
    }

    public static void e(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.e(TAG, logString, paramThrowable);
    }

    public static void i(String logString) {
        if (!isDebug())
            return;
        Log.i(TAG, logString);
    }

    public static void i(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.i(TAG, logString, paramThrowable);
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void p(Object paramObject) {
        if (!isDebug())
            return;
        System.out.println(paramObject);
    }

    public static void v(String logString) {
        if (!isDebug())
            return;
        Log.v(TAG, logString);
    }

    public static void v(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.v(TAG, logString, paramThrowable);
    }

    public static void w(String logString) {
        if (!isDebug())
            return;
        Log.w(TAG, logString);
    }

    public static void w(String logString,
                         Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.w(TAG, logString, paramThrowable);
    }

    public static void w(Throwable paramThrowable) {
        if (!isDebug())
            return;
        Log.w(TAG, paramThrowable);
    }

}