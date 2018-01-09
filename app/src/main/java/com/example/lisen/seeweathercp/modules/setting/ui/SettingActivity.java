package com.example.lisen.seeweathercp.modules.setting.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.base.ToolbarActivity;

/**
 * Created by lisen on 2018/1/8.
 */

public class SettingActivity extends ToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("设置");
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingFragment()).commit();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_setting;
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
