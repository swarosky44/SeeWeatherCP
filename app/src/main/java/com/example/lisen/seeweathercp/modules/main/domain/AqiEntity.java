package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class AqiEntity implements Serializable {

    @SerializedName("city")
    public CityEntity city;

}
