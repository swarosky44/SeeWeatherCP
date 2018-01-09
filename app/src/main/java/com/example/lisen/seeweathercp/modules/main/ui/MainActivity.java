package com.example.lisen.seeweathercp.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseActivity;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.common.C;
import com.example.lisen.seeweathercp.common.utils.CircularAnimUtil;
import com.example.lisen.seeweathercp.common.utils.DoubleClickExit;
import com.example.lisen.seeweathercp.common.utils.RxDrawer;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.common.utils.ToastUtil;
import com.example.lisen.seeweathercp.modules.city.ui.ChoiceCityActivity;
import com.example.lisen.seeweathercp.modules.launch.FirstActivity;
import com.example.lisen.seeweathercp.modules.main.adapter.HomePagerAdapter;
import com.example.lisen.seeweathercp.modules.service.AutoUpdateService;
import com.example.lisen.seeweathercp.modules.setting.ui.SettingActivity;
import com.squareup.haha.perflib.Main;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private MainFragment mMainFragment;
    private MultiCityFragment mMultiCityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDrawer();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initIcon();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(mDrawerLayout)
                .doOnNext(o -> {
                    switch (item.getItemId()) {
                        case R.id.nav_set:
                            SettingActivity.launch(MainActivity.this);
                            break;
                        case R.id.nav_city:
                            ChoiceCityActivity.launch(MainActivity.this);
                            break;
                        case R.id.nav_multi_cities:
                            mViewPager.setCurrentItem(1);
                            break;
                    }
                })
                .subscribe();
        return false;
    }

    // 初始化基础 View
    private void initView() {
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(v -> showShareDialog());
        HomePagerAdapter mAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mMainFragment = new MainFragment();
        mMultiCityFragment = new MultiCityFragment();
        mAdapter.addTab(mMainFragment, "主页面");
        mAdapter.addTab(mMultiCityFragment, "多城市");
        mViewPager.setAdapter(mAdapter);
        FabVisiblitityChangedListener fabVisiblitityChangedListener = new FabVisiblitityChangedListener();
        mTabLayout.setupWithViewPager(mViewPager, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mFab.isShown()) {
                    fabVisiblitityChangedListener.position = position;
                    mFab.hide(fabVisiblitityChangedListener);
                } else {
                    changeFabState(position);
                    mFab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initDrawer() {
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void initIcon() {
        if (SharedPreferencesUtil.getInstance().getIconType() == 0) {
            SharedPreferencesUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferencesUtil.getInstance().putInt("晴", R.mipmap.type_one_sunny);
            SharedPreferencesUtil.getInstance().putInt("阴", R.mipmap.type_one_cloudy);
            SharedPreferencesUtil.getInstance().putInt("多云", R.mipmap.type_one_cloudy);
            SharedPreferencesUtil.getInstance().putInt("少云", R.mipmap.type_one_cloudy);
            SharedPreferencesUtil.getInstance().putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            SharedPreferencesUtil.getInstance().putInt("小雨", R.mipmap.type_one_light_rain);
            SharedPreferencesUtil.getInstance().putInt("中雨", R.mipmap.type_one_light_rain);
            SharedPreferencesUtil.getInstance().putInt("大雨", R.mipmap.type_one_heavy_rain);
            SharedPreferencesUtil.getInstance().putInt("阵雨", R.mipmap.type_one_thunderstorm);
            SharedPreferencesUtil.getInstance().putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            SharedPreferencesUtil.getInstance().putInt("霾", R.mipmap.type_one_fog);
            SharedPreferencesUtil.getInstance().putInt("雾", R.mipmap.type_one_fog);
        } else {
            SharedPreferencesUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferencesUtil.getInstance().putInt("晴", R.mipmap.type_two_sunny);
            SharedPreferencesUtil.getInstance().putInt("阴", R.mipmap.type_two_cloudy);
            SharedPreferencesUtil.getInstance().putInt("多云", R.mipmap.type_two_cloudy);
            SharedPreferencesUtil.getInstance().putInt("少云", R.mipmap.type_two_cloudy);
            SharedPreferencesUtil.getInstance().putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            SharedPreferencesUtil.getInstance().putInt("小雨", R.mipmap.type_two_light_rain);
            SharedPreferencesUtil.getInstance().putInt("中雨", R.mipmap.type_two_rain);
            SharedPreferencesUtil.getInstance().putInt("大雨", R.mipmap.type_two_rain);
            SharedPreferencesUtil.getInstance().putInt("阵雨", R.mipmap.type_two_rain);
            SharedPreferencesUtil.getInstance().putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            SharedPreferencesUtil.getInstance().putInt("霾", R.mipmap.type_two_haze);
            SharedPreferencesUtil.getInstance().putInt("雾", R.mipmap.type_two_fog);
            SharedPreferencesUtil.getInstance().putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    private void showShareDialog() {
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    private class FabVisiblitityChangedListener extends FloatingActionButton.OnVisibilityChangedListener {

        private int position;

        @Override
        public void onHidden(FloatingActionButton fab) {
            changeFabState(position);
            fab.show();
        }
    }

    private void changeFabState(int position) {
        if (position == 1) {
            mFab.setImageResource(R.drawable.ic_add_24dp);
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
            mFab.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
                intent.putExtra(C.MULTI_CHECK, true);
                CircularAnimUtil.startActivity(MainActivity.this, intent, mFab, R.color.colorPrimary);
            });
        } else {
            mFab.setImageResource(R.drawable.ic_favorite);
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
            mFab.setOnClickListener(v -> showShareDialog());
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!DoubleClickExit.check()) {
                ToastUtil.showShort("再按一次退出");
            } else {
                finish();
            }
        }
    }
}
