package com.example.lisen.seeweathercp.modules.main.domain;

/**
 * Created by lisen on 2017/12/19.
 */

public class ChangeCityEvent {

    String city;
    boolean isSetting;

    public ChangeCityEvent() {}

    public ChangeCityEvent(boolean isSetting) {
        this.isSetting = isSetting;
    }

    public ChangeCityEvent(String city) {
        this.city = city;
    }

}
