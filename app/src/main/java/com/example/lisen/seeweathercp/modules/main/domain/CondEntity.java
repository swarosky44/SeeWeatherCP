package com.example.lisen.seeweathercp.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lisen on 2017/12/15.
 */

public class CondEntity implements Serializable {

    @SerializedName("code")
    public String code;

    @SerializedName("txt")
    public String txt;

    @SerializedName("txt_d")
    public String txtDay;

}
