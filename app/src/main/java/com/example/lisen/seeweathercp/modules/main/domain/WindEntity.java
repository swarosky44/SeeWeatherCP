package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class WindEntity implements Serializable {

    @SerializedName("deg")
    public String deg;

    @SerializedName("dir")
    public String dir;

    @SerializedName("sc")
    public String sc;

    @SerializedName("spd")
    public String spd;

}
