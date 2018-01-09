package com.example.lisen.seeweathercp.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.example.lisen.seeweathercp.R;

import butterknife.BindView;

/**
 * Created by lisen on 2017/12/29.
 */

public abstract class ToolbarActivity extends BaseActivity {

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    protected boolean mIsHidden = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mToolbar == null || mAppBar == null) {
            throw new IllegalArgumentException("The subclass of ToolbarActivity must contain a toolbar");
        }
        mToolbar.setOnClickListener(v -> onToolbarClick());
        setSupportActionBar(mToolbar);
        if (canBack()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mAppBar.setElevation(10.6f);
        }
    }

    public void onToolbarClick() {}

    public boolean canBack() {
        return false;
    }

    protected void setAppBarAlpha(float alpha) {
        mAppBar.setAlpha(alpha);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void hideOrShowToolbar() {
        mAppBar.animate()
                .translationY(mIsHidden ? 0 : -mAppBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsHidden = !mIsHidden;
    }

    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = getSupportActionBar();
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }
}
