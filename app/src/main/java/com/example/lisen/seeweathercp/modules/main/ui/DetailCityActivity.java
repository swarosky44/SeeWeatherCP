package com.example.lisen.seeweathercp.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.ToolbarActivity;
import com.example.lisen.seeweathercp.modules.main.adapter.WeatherAdapter;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;

import butterknife.BindView;

public class DetailCityActivity extends ToolbarActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewWithData();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_detail_city;
    }

    private void initViewWithData() {
        Intent intent = getIntent();
        Weather weather = (Weather) intent.getSerializableExtra("weather");
        if (weather == null) {
            finish();
        }

        setTitle(weather.basic.city);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        WeatherAdapter mAdapter = new WeatherAdapter(weather);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static void launch(Context context, Weather weather) {
        Intent intent = new Intent(context, DetailCityActivity.class);
        intent.putExtra("weather", weather);
        context.startActivity(intent);
    }

}
