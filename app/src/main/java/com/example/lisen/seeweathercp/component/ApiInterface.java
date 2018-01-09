package com.example.lisen.seeweathercp.component;

import com.example.lisen.seeweathercp.modules.main.domain.WeatherAPI;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lisen on 2017/12/19.
 */

public interface ApiInterface {

    String HOST = "https://free-api.heweather.com/v5/";

    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);

    @GET("weather")
    Flowable<WeatherAPI> mWeatherAPIF(@Query("city") String city, @Query("key") String key);

}
