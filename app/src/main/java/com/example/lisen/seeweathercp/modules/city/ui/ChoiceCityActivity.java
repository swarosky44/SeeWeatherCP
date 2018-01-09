package com.example.lisen.seeweathercp.modules.city.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.ToolbarActivity;
import com.example.lisen.seeweathercp.common.C;
import com.example.lisen.seeweathercp.common.Irrelevant;
import com.example.lisen.seeweathercp.common.utils.RxUtil;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.common.utils.Util;
import com.example.lisen.seeweathercp.component.OrmLite;
import com.example.lisen.seeweathercp.component.RxBus;
import com.example.lisen.seeweathercp.modules.city.adapter.CityAdapter;
import com.example.lisen.seeweathercp.modules.city.db.DBManager;
import com.example.lisen.seeweathercp.modules.city.db.WeatherDB;
import com.example.lisen.seeweathercp.modules.city.domain.City;
import com.example.lisen.seeweathercp.modules.city.domain.Province;
import com.example.lisen.seeweathercp.modules.main.domain.ChangeCityEvent;
import com.example.lisen.seeweathercp.modules.main.domain.CityORM;
import com.example.lisen.seeweathercp.modules.main.domain.MultiUpdateEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;

/**
 * Created by lisen on 2017/12/29.
 */

public class ChoiceCityActivity extends ToolbarActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private List<Province> provinceList = new ArrayList<>();
    private List<City> cityList;
    private CityAdapter mAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;
    private boolean isChecked = false;

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_choice_city;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        Observable.create(emitter -> {
            DBManager.getInstance().openDatabase();
            emitter.onNext(Irrelevant.INSTANCE);
            emitter.onComplete();
        })
                .compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .doOnNext(o -> {
                    initRecyclerView();
                    queryProvinces();
                })
                .subscribe();

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(C.MULTI_CHECK, false);
        if (isChecked && SharedPreferencesUtil.getInstance().getBoolean("Tips", true)) {
            showTips();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_city_menu, menu);
        menu.getItem(0).setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.multi_check) {
            if (isChecked) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            isChecked = item.isChecked();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_PROVINCE) {
            quit();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getInstance().closeDatabase();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChoiceCityActivity.class));
    }

    private void initView() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(pos);
                mRecyclerView.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                String city = Util.replaceCity(cityList.get(pos).mCityName);
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdateEvent());
                } else {
                    SharedPreferencesUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                quit();
            }
        });
    }

    private void queryProvinces() {
        getToolbar().setTitle("选择省份");
        Flowable.create((FlowableOnSubscribe<String>) emitter -> {
           if (provinceList.isEmpty()) {
               provinceList.addAll(WeatherDB.loadProvinces(DBManager.getInstance().getDatabase()));
           }
           dataList.clear();
           for (Province province : provinceList) {
               emitter.onNext(province.mProName);
           }
           emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(proName -> dataList.add(proName))
                .doOnComplete(() -> {
                    mProgressBar.setVisibility(View.GONE);
                    currentLevel = LEVEL_PROVINCE;
                    mAdapter.notifyDataSetChanged();
                })
                .subscribe();
    }

    private void queryCities() {
        getToolbar().setTitle("选择城市");
        dataList.clear();
        mAdapter.notifyDataSetChanged();

        Flowable.create((FlowableOnSubscribe<String>) emitter -> {
            cityList = WeatherDB.loadCities(DBManager.getInstance().getDatabase(), selectedProvince.mProSort);
            for (City city : cityList) {
                emitter.onNext(city.mCityName);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(proName -> dataList.add(proName))
                .doOnComplete(() -> {
                    currentLevel = LEVEL_CITY;
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(0);
                })
                .subscribe();
    }

    private void showTips() {
        new AlertDialog.Builder(this)
                .setTitle("多城市管理模式")
                .setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                        + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)")
                .setPositiveButton("明白", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("不再提示", (dialog, which) -> SharedPreferencesUtil.getInstance().putBoolean("Tips", false))
                .show();
    }

    private void quit() {
        ChoiceCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
