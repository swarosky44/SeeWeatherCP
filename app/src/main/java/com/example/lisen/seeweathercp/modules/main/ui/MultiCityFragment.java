package com.example.lisen.seeweathercp.modules.main.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.BaseFragment;
import com.example.lisen.seeweathercp.common.C;
import com.example.lisen.seeweathercp.common.utils.RxUtil;
import com.example.lisen.seeweathercp.common.utils.Util;
import com.example.lisen.seeweathercp.component.OrmLite;
import com.example.lisen.seeweathercp.component.PLog;
import com.example.lisen.seeweathercp.component.RetrofitSingleTon;
import com.example.lisen.seeweathercp.component.RxBus;
import com.example.lisen.seeweathercp.modules.main.adapter.MultiCityAdapter;
import com.example.lisen.seeweathercp.modules.main.domain.CityORM;
import com.example.lisen.seeweathercp.modules.main.domain.MultiUpdateEvent;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by lisen on 2017/12/29.
 */

public class MultiCityFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.empty)
    LinearLayout mLayout;

    private View view;
    private List<Weather> mWeathers;
    private MultiCityAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_multicity, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 貌似无用代码
        RxBus.getDefault()
                .toObservable(MultiUpdateEvent.class)
                .doOnNext(event -> multiLoad())
                .subscribe();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        multiLoad();
    }

    @Override
    protected void lazyload() {

    }

    private void initView() {
        mWeathers = new ArrayList<>();
        mAdapter = new MultiCityAdapter(mWeathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnMultiCityClick(new MultiCityAdapter.onMultiCityClick() {
            @Override
            public void longClick(String city) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("是否删除该城市？")
                        .setPositiveButton("删除", (dialog, which) -> {
                            OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                            multiLoad();
                            Snackbar.make(getView(), String.format(Locale.CHINA, "已经将%s删掉了 Ծ‸ Ծ", city), Snackbar.LENGTH_LONG)
                                    .setAction("撤销", v -> {
                                        OrmLite.getInstance().save(new CityORM(city));
                                        multiLoad();
                                    })
                                    .show();
                        })
                        .show();
            }

            @Override
            public void click(Weather weather) {
                DetailCityActivity.launch(getContext(), weather);
            }
        });

        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright
            );
            mRefreshLayout.setOnRefreshListener(() -> mRefreshLayout.postDelayed(this::multiLoad, 1000));
        }
    }

    private void multiLoad() {
        mWeathers.clear();
        Observable.create((ObservableOnSubscribe<CityORM>) emitter -> {
           try {
               PLog.d("MulitiCityFragment", OrmLite.getInstance().query(CityORM.class).toString());
               for (CityORM cityORM : OrmLite.getInstance().query(CityORM.class)) {
                   emitter.onNext(cityORM);
               }
               emitter.onComplete();
           } catch (Exception e) {
               emitter.onError(e);
           }
        }).doOnSubscribe(subscription -> mRefreshLayout.setRefreshing(true))
                .map(city -> Util.replaceCity(city.getName()))
                .distinct()
                .flatMap(cityName -> RetrofitSingleTon.getInstance().fetchWeather(cityName))
                .filter(weather -> !C.UNKNOWN_CITY.equals(weather.status))
                .take(3)
                .compose(RxUtil.fragmentLifecycle(this))
                .doOnNext(weather -> mWeathers.add(weather))
                .doOnComplete(() -> {
                    mRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.isEmpty()) {
                        mLayout.setVisibility(View.VISIBLE);
                    } else {
                        mLayout.setVisibility(View.GONE);
                    }
                })
                .doOnError(error -> {
                    PLog.w("MultiCityFragment", error.toString());
                    if (mAdapter.isEmpty() && mLayout != null) {
                        mLayout.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe();
    }
}
