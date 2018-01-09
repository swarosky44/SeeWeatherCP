package com.example.lisen.seeweathercp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.components.support.RxFragment;

/**
 * Created by lisen on 2017/12/15.
 */

public abstract class BaseFragment extends RxFragment {

    protected boolean mIsCreateView = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mIsCreateView) {
            lazyload();
        }
    }

    // 视图加载之前，初始化数据
    protected abstract void lazyload();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyload();
        }
    }

    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }
}
