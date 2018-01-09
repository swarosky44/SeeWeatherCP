package com.example.lisen.seeweathercp.component;

import android.util.Log;

import com.example.lisen.seeweathercp.BuildConfig;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.common.utils.TimeUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by lisen on 2017/12/13.
 */

public class PLog {
    private static boolean isDebug = BuildConfig.DEBUG;
    private static final String PATH = BaseApplication.getAppCacheDir();
    private static String PLOG_FILE_NAME = "log.txt";

    // 是否写入日志文件
    public static final boolean PLOG_WRITE_TO_FILE = true;

    // 错误信息
    public static void e(String TAG, String msg) {
        Log.e(TAG, log(msg));
        if (PLOG_WRITE_TO_FILE) {
            writeLogtoFile("e", TAG, msg);
        }
    }

    // 警告信息
    public static void w(String TAG, String msg) {
        if (isDebug) {
            Log.w(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("w", TAG, msg);
            }
        }
    }

    // 调试信息
    public static void d(String TAG, String msg) {
        if (isDebug) {
            Log.d(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("d", TAG, msg);
            }
        }
    }

    public static void i(String TAG, String msg) {
        if (isDebug) {
            Log.i(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("i", TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        e(getClassName(), msg);
    }

    public static void w(String msg) {
        w(getClassName(), msg);
    }

    public static void d(String msg) {
        d(getClassName(), msg);
    }

    public static void i(String msg) {
        i(getClassName(), msg);
    }

    private static void writeLogtoFile(String msglogtype, String tag, String msg) {
        isExist(PATH);
        String needWriteMessage = "\r\n"
                + TimeUtil.getNowMDHMSTime()
                + "\r\n"
                + msglogtype
                + "      "
                + tag
                + "\r\n"
                + msg;
        File file = new File(PATH, PLOG_FILE_NAME);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(fileWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除日志文件
    public static void delFile() {
        File file = new File(PATH, PLOG_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    // 判断文件夹是否存在，不存在则创建
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                PLog.e(e.getMessage());
            }
        }
    }

    // 返回当前的类名
    private static String getClassName() {

        String result;
        StackTraceElement thisMethodStack = Thread.currentThread().getStackTrace()[2];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result.substring(lastIndex + 1);
        int i = result.indexOf("$");
        return i == -1 ? result : result.substring(0, i);

    }

    // 打印 Log 行数信息
    private static String log(String message) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[5];
        String className = targetElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1) + ".java";
        int lineNumber = targetElement.getLineNumber();
        if (lineNumber < 0) lineNumber = 0;
        return "(" + className + ":" + lineNumber + ")" + message;

    }
}
