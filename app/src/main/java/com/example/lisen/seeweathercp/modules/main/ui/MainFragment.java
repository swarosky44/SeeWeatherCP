package com.example.lisen.seeweathercp.modules.main.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseFragment;
import com.example.lisen.seeweathercp.common.utils.RxUtil;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.common.utils.ToastUtil;
import com.example.lisen.seeweathercp.common.utils.VersionUtil;
import com.example.lisen.seeweathercp.component.PLog;
import com.example.lisen.seeweathercp.component.RetrofitSingleTon;
import com.example.lisen.seeweathercp.component.RxBus;
import com.example.lisen.seeweathercp.modules.main.adapter.WeatherAdapter;
import com.example.lisen.seeweathercp.modules.main.domain.ChangeCityEvent;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.security.auth.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by lisen on 2017/12/15.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_erro)
    ImageView mIvError;

    private static Weather mWeather = new Weather();
    private WeatherAdapter mWeatherAdapter;

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault()
                .toObservable(ChangeCityEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(event -> isVisible())
                .doOnNext(event -> {
                    mRefreshLayout.setRefreshing(true);
                    load();
                })
                .subscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.content_main, container, false);
            ButterKnife.bind(this, view);
        }
        mIsCreateView = true;
        
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        new RxPermissions(getActivity())
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .doOnNext(o -> mRefreshLayout.setRefreshing(true))
                .doOnNext(granted -> {
                    if (granted) {
                        location();
                    } else {
                        load();
                    }
                })
                .subscribe();
    }

    @Override
    protected void lazyload() {

    }

    private void initView() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mRefreshLayout.setOnRefreshListener(() -> mRefreshLayout.postDelayed(this::load, 1000));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherAdapter = new WeatherAdapter(mWeather);
        mRecyclerView.setAdapter(mWeatherAdapter);
    }

    private void location() {

    }

    private void load() {
        fetchDataByNetWork()
                .doOnSubscribe(aLong -> mRefreshLayout.setRefreshing(true))
                .doOnError(throwable -> {
                    PLog.w("MainFragment", throwable.toString());
                    mIvError.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    SharedPreferencesUtil.getInstance().setCityName("北京");
                    safeSetTitle("找不到城市啦");
                })
                .doOnNext(weather -> {
                    mIvError.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    mWeather.status = weather.status;
                    mWeather.aqi = weather.aqi;
                    mWeather.basic = weather.basic;
                    mWeather.suggestion = weather.suggestion;
                    mWeather.now = weather.now;
                    mWeather.dailyForecast = weather.dailyForecast;
                    mWeather.hourlyForecast = weather.hourlyForecast;
                    safeSetTitle(weather.basic.city);
                    mWeatherAdapter.notifyDataSetChanged();
                    // NotificationHelper.showWeatherNotification(getActivity(), weather);
                })
                .doOnComplete(() -> {
                    mRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    ToastUtil.showShort(getString(R.string.complete));
                })
                .subscribe();
    }

    private Observable<Weather> fetchDataByNetWork() {
        String cityName = SharedPreferencesUtil.getInstance().getCityName();
        return RetrofitSingleTon.getInstance()
                .fetchWeather(cityName)
                .compose(RxUtil.fragmentLifecycle(this));
    }
}
