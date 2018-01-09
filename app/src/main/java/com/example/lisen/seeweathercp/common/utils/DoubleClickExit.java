package com.example.lisen.seeweathercp.common.utils;

/**
 * Created by lisen on 2018/1/5.
 */

public class DoubleClickExit {

    public static long mLastClick = 0L;
    private static final int THRESHOLD = 2000;

    public static boolean check() {
        long now = System.currentTimeMillis();
        boolean b = now - mLastClick < THRESHOLD;
        mLastClick = now;
        return b;
    }

}
