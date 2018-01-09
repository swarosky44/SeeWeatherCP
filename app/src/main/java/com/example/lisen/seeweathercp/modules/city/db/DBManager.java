package com.example.lisen.seeweathercp.modules.city.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.component.PLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lisen on 2018/1/2.
 */

public class DBManager {

    private static String TAG = DBManager.class.getSimpleName();
    public static final String DB_NAME = "china_city.db";
    public static final String PACKAGE_NAME = "com.example.lisen.seeweathercp";
    // 手机内存放数据库的位置 /data/data/com.xx.xx/xx.db
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context context;

    private DBManager() {}

    public static DBManager getInstance() {
        return DBManagerHolder.instance;
    }

    private static final class DBManagerHolder {
        public static final DBManager instance = new DBManager();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    @Nullable
    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {
                InputStream is = BaseApplication.getAppContext().getResources().openRawResource(R.raw.china_city);
                FileOutputStream fos = new FileOutputStream(dbfile);
                int BUFFER_SIZE = 400000;
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        } catch (FileNotFoundException e) {
            PLog.e("File nt found");
            e.printStackTrace();
        } catch (IOException e) {
            PLog.e("IO Exception");
            e.printStackTrace();
        }

        return null;
    }

    public void closeDatabase() {
        if (this.database != null) {
            this.database.close();
        }
    }
}
