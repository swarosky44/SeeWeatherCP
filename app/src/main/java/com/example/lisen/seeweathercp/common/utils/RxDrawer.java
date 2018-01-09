package com.example.lisen.seeweathercp.common.utils;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.example.lisen.seeweathercp.common.Irrelevant;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableSerialized;

/**
 * Created by lisen on 2018/1/5.
 */

public class RxDrawer {

    private static final float OFFSET_THRESHOLD = 0.03f;

    public static Observable<Irrelevant> close(final DrawerLayout drawer) {
        return Observable.create(emitter -> {
            drawer.closeDrawer(GravityCompat.START);
            DrawerLayout.DrawerListener listener = new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                    if (slideOffset < OFFSET_THRESHOLD) {
                        emitter.onNext(Irrelevant.INSTANCE);
                        emitter.onComplete();
                        drawer.removeDrawerListener(this);
                    }
                }
            };
            drawer.addDrawerListener(listener);
        });
    }

}
