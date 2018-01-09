package com.example.lisen.seeweathercp.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by lisen on 2017/12/18.
 */

public class Util {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String safeText(String msg) {
        return TextUtils.isEmpty(msg) ? "" : msg;
    }

    // 匹配掉无用信息
    public static String replaceInfo(String city) {
        city = safeText(city).replace("API没有", "");
        return city;
    }

    // 匹配掉错误信息
    public static String replaceCity(String city) {
        city = safeText(city).replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
        return city;
    }
}
