package com.example.lisen.seeweathercp.common.utils;

import android.text.format.Formatter;
import android.util.Log;

import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.component.PLog;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by lisen on 2018/1/8.
 */

public class FileSizeUtil {

    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    public static final int SIZETYPE_GB = 4;

    // 获取指定文件的指定单位的大小
    public static double getFileOrFileSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
         } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败！");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    // 自动计算指定文件或文件夹的大小
    public static String getAutoFileOrFileSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PLog.e("获取文件大小失败");
        }
        return Formatter.formatFileSize(BaseApplication.getAppContext(), blockSize);
    }

    // 获取指定文件大小
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            PLog.e("获取文件大小", "文件不存在！");
        }
        return size;
    }

    // 获取指定文件夹
    private static long getFileSizes(File file) throws Exception {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    // 转换文件大小，指定转换的类型
    private static double FormetFileSize(long files, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) files));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) files / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) files / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) files / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }
}
