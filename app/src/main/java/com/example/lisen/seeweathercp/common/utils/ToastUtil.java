package com.example.lisen.seeweathercp.common.utils;

import android.widget.Toast;

import com.example.lisen.seeweathercp.base.BaseApplication;

/**
 * Created by lisen on 2017/12/19.
 */

public class ToastUtil {

    public static void showShort(String msg) {
        Toast.makeText(BaseApplication.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLoing(String msg) {
        Toast.makeText(BaseApplication.getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

}
