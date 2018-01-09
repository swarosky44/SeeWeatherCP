package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class DailyForecastEntity implements Serializable {

    @SerializedName("cond")
    public CondEntity cond;
    @SerializedName("date")
    public String date;
    @SerializedName("hum")
    public String hum;
    @SerializedName("pcpn")
    public String pcpn;
    @SerializedName("pop")
    public String pop;
    @SerializedName("pres")
    public String pres;
    /**
     * max : 19
     * min : 7
     */

    @SerializedName("tmp")
    public TmpEntity tmp;
    @SerializedName("vis")
    public String vis;

    @SerializedName("wind")
    public WindEntity wind;

}
