package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisen on 2017/12/19.
 */

public class WeatherAPI {

    @SerializedName("HeWeather5")
    @Expose
    public List<Weather> mWeathers = new ArrayList<>();

}
