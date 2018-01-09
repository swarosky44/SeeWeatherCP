package com.example.lisen.seeweathercp.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.lisen.seeweathercp.component.RetrofitSingleTon;

import retrofit2.Retrofit;

/**
 * Created by lisen on 2017/12/14.
 */

public class VersionUtil {

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "找不到版本号";
        }
    }

    // 版本号
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void checkVersion(Context context) {
//        RetrofitSingleton.getInstance()
//                .fetchVersion()
//                .doOnNext(version -> {
//                    String firVersionName = version.versionShort;
//                    String currentVersionName = VersionUtil.getVersion(context);
//                    if (currentVersionName.compareTo(firVersionName) < 0) {
//                        if (!SharedPreferencesUtil.getInstance().getString("version", "").equals(version.versionShort)) {
//                            showUpdateDialog(version, context);
//                        }
//                    }
//                })
//                .subscribe();
    }

    public static void checkVersion(Context context, boolean force) {
//        RetrofitSingleTon.getInstance()
//                .fetchVersion()
//                .doOnNext(version -> {
//                    String firVersionName = version.versionShort;
//                    String currentVersionName = VersionUtil.getVersion(context);
//                    if (currentVersionName.compareTo(firVersionName) < 0) {
//                        showUpdateDialog(version, context);
//                    } else {
//                        ToastUtil.showShort("已经是最新版本(⌐■_■)");
//                    }
//                })
//                .subscribe();
    }

}
