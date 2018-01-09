package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class NowEntity implements Serializable {

    @SerializedName("cond")
    public CondEntity cond;

    @SerializedName("fl")
    public String fl;

    @SerializedName("hum")
    public String hum;

    @SerializedName("pcpn")
    public String pcpn;

    @SerializedName("pres")
    public String pres;

    @SerializedName("tmp")
    public String tmp;

    @SerializedName("vis")
    public String vis;

    @SerializedName("wind")
    public WindEntity wind;

}
