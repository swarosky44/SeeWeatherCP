package com.example.lisen.seeweathercp.component;

import android.content.Context;
import android.os.Build;

import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.common.utils.VersionUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by lisen on 2017/12/13.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static Thread.UncaughtExceptionHandler mDefaultHandler = null;

    private Context mContext = null;

    private final String TAG = CrashHandler.class.getSimpleName();

    public CrashHandler(Context context) {
        this.mContext = context;
    }

    // 初始化，设置该 CrashHandler 为程序的默认处理器
    public static void init(CrashHandler crashHandler) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(e.toString());
        PLog.e(TAG, e.toString());
        PLog.e(TAG, collectCrashDeviceInfo());
        PLog.e(TAG, getCrashInfo(e));

        // 崩溃后自动初始化数据
        SharedPreferencesUtil.getInstance().setCityName("北京");
        OrmLite.getInstance().deleteDatabase();
        // 调用系统错误机制
        mDefaultHandler.uncaughtException(t, e);
    }

    // 得道程序崩溃的详细信息
    private String getCrashInfo(Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.setStackTrace(e.getStackTrace());
        e.printStackTrace();
        return result.toString();
    }

    // 收集程序崩溃的设备信息
    private String collectCrashDeviceInfo() {

        String versionName = VersionUtil.getVersion(mContext);
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;

        return versionName + " " + model + " " + androidVersion + " " + manufacturer;
    }
}
