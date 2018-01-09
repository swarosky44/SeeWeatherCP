package com.example.lisen.seeweathercp.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.lisen.seeweathercp.R;
import com.example.lisen.seeweathercp.common.utils.SharedPreferencesUtil;
import com.example.lisen.seeweathercp.modules.main.domain.Weather;
import com.example.lisen.seeweathercp.modules.main.ui.MainActivity;
import com.squareup.haha.perflib.Main;

/**
 * Created by lisen on 2018/1/8.
 */

public class NotificationHelper {

    private static final int NOTIFICATION_ID = 233;

    public static void showWeatherNotification(Context context, @NonNull Weather weather) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                .setSmallIcon(SharedPreferencesUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = SharedPreferencesUtil.getInstance().getNotificationModel();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

}
