package com.example.lisen.seeweathercp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by lisen on 2017/12/14.
 */

public abstract class BaseActivity extends RxAppCompatActivity {

    private static String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        ButterKnife.bind(this);
    }

    protected abstract int layoutId();

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void setdayTheme(AppCompatActivity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        activity.recreate();
    }

    public static void setNightTheme(AppCompatActivity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        activity.recreate();
    }

    public void setTheme(AppCompatActivity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        activity.recreate();
    }
}
