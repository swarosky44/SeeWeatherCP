package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class HourlyForecastEntity implements Serializable {

    @SerializedName("date")
    public String date;

    @SerializedName("hum")
    public String hum;

    @SerializedName("pop")
    public String pop;

    @SerializedName("pres")
    public String pres;

    @SerializedName("tmp")
    public String tmp;

    @SerializedName("wind")
    public WindEntity wind;

}
