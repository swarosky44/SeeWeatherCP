package com.example.lisen.seeweathercp.component;

import android.util.Log;

import com.example.lisen.seeweathercp.BuildConfig;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.common.C;
import com.example.lisen.seeweathercp.common.utils.RxUtil;
import com.example.lisen.seeweathercp.common.utils.ToastUtil;
import com.example.lisen.seeweathercp.common.utils.Util;
import com.example.lisen.seeweathercp.modules.main.domain.CityORM;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lisen on 2017/12/19.
 */

public class RetrofitSingleTon {

    private static ApiInterface sApiService = null;
    private static Retrofit sRetrofit = null;
    private static OkHttpClient sOkHttpClient = null;

    private RetrofitSingleTon() {
        init();
    }

    public static RetrofitSingleTon getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    private static class SingleTonHolder {
        private static final RetrofitSingleTon INSTANCE = new RetrofitSingleTon();
    }

    private void init() {
        initOkHttp();
        initRetrofit();
        sApiService = sRetrofit.create(ApiInterface.class);
    }

    private void initRetrofit() {

        sRetrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.HOST)
                .client(sOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    private void initOkHttp() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        File cacheFile = new File(C.NET_CACHE);
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!Util.isNetworkConnected(BaseApplication.getAppContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            Response.Builder newBuilder = response.newBuilder();
            if (Util.isNetworkConnected(BaseApplication.getAppContext())) {
                int maxAge = 0;
                newBuilder.header("Cache-control", "public, max-age=" + maxAge);
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                newBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
            }
            return newBuilder.build();
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        // 超时设置
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        // 错误重连
        builder.retryOnConnectionFailure(true);
        sOkHttpClient = builder.build();
    }

    private static Consumer<Throwable> disposeFailureInfo(Throwable t) {
        return throwable -> {
            if (t.toString().contains("GaiException")
                    || t.toString().contains("SocketTimeoutException")
                    || t.toString().contains("UnknownHostException")) {
                ToastUtil.showShort("网络问题");
            } else if (t.toString().contains("API没有")) {
                OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", Util.replaceInfo(t.getMessage())));
                ToastUtil.showShort("错误：" + t.getMessage());
            }
        };
    }

    public Observable<Weather> fetchWeather(String city) {
        return sApiService.mWeatherAPI(city, C.KEY)
                .flatMap(weather -> {
                    String status = weather.mWeathers.get(0).status;
                    if ("no more requests".equals(status)) {
                        return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                    } else if ("unknown city".equals(status)) {
                        return Observable.error(new RuntimeException(String.format("API没有%s", city)));
                    }
                    return Observable.just(weather);
                })
                .map(weather -> weather.mWeathers.get(0))
                .doOnError(RetrofitSingleTon::disposeFailureInfo)
                .compose(RxUtil.io());
    }

}
