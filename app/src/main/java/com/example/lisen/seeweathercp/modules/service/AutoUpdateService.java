package com.example.lisen.seeweathercp.modules.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.component.NotificationHelper;
import com.example.lisen.seeweathercp.component.RetrofitSingleTon;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by lisen on 2018/1/8.
 */

public class AutoUpdateService extends Service {

    private final String TAG = AutoUpdateService.class.getSimpleName();
    private Disposable mDisposable;
    private boolean mIsUnSubscribed = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (mIsUnSubscribed) {
                unSubscribed();
                if (SharedPreferencesUtil.getInstance().getAutoUpdate() != 0) {
                    mDisposable = Observable.interval(SharedPreferencesUtil.getInstance().getAutoUpdate(), TimeUnit.HOURS)
                            .doOnNext(aLong -> {
                                mIsUnSubscribed = false;
                                fetchDataByNetWork();
                            })
                            .subscribe();
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        mIsUnSubscribed = true;
        if (mDisposable != null && mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = SharedPreferencesUtil.getInstance().getCityName();
        RetrofitSingleTon.getInstance()
                .fetchWeather(cityName)
                .subscribe(weather -> NotificationHelper.showWeatherNotification(AutoUpdateService.this, weather));
    }
}
