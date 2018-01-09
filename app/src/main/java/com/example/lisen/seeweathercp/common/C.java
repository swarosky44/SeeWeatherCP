package com.example.lisen.seeweathercp.common;

import com.example.lisen.seeweathercp.BuildConfig;
import com.example.lisen.seeweathercp.base.BaseApplication;

import java.io.File;

/**
 * Created by lisen on 2017/12/14.
 */

// 常量类
public class C {

    public static final String API_TOKEN = BuildConfig.FirToken;
    public static final String KEY = BuildConfig.WeatherKey;
    public static final String MULTI_CHECK = "multi_check";
    public static final String ORM_NAME = "cities.db";
    public static final String UNKNOWN_CITY = "unknown city";
    public static final String NET_CACHE = BaseApplication.getAppCacheDir() + File.separator + "NetCache";

}
