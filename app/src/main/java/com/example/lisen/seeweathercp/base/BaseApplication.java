package com.example.lisen.seeweathercp.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatDelegate;

import com.example.lisen.seeweathercp.BuildConfig;
import com.example.lisen.seeweathercp.component.CrashHandler;
import com.example.lisen.seeweathercp.component.PLog;
import com.facebook.stetho.Stetho;
import com.github.moduth.blockcanary.BlockCanary;
import com.hugo.watcher.Watcher;
import com.squareup.leakcanary.LeakCanary;

import butterknife.ButterKnife;
import im.fir.sdk.FIR;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by lisen on 2017/12/13.
 */

public class BaseApplication extends Application {

    private static String sCacheDir;

    private static Context sAppContext;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        CrashHandler.init(new CrashHandler(getApplicationContext()));
        if (!BuildConfig.DEBUG) {
            FIR.init(this);
        } else {
            Watcher.getInstance().start(this);
            Stetho.initializeWithDefaults(this);
        }
        // 检测主线程的卡顿
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        // 内存泄露检测
        LeakCanary.install(this);
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable != null) {
                PLog.e(throwable.toString());
            } else {
                PLog.e("call onError but exception is null");
            }
        });

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }
}
