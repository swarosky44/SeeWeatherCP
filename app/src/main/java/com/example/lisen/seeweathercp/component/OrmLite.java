package com.example.lisen.seeweathercp.component;

import com.example.lisen.seeweathercp.BuildConfig;
import com.example.lisen.seeweathercp.base.BaseApplication;
import com.example.lisen.seeweathercp.common.C;
import com.litesuits.orm.LiteOrm;
/**
 * Created by lisen on 2017/12/14.
 */

public class OrmLite {

    static LiteOrm sLiteOrm;

    public static LiteOrm getInstance() {
        getOrmHolder();
        return sLiteOrm;
    }

    private static OrmLite getOrmHolder() {
        return OrmHolder.sInstance;
    }

    private OrmLite() {
        if (sLiteOrm == null) {
            sLiteOrm = LiteOrm.newSingleInstance(BaseApplication.getAppContext(), C.ORM_NAME);
        }
        sLiteOrm.setDebugged(BuildConfig.DEBUG);
    }

    private static class OrmHolder {
        private static final OrmLite sInstance = new OrmLite();
    }

}
