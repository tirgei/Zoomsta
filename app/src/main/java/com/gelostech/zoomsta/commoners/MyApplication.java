package com.gelostech.zoomsta.commoners;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by tirgei on 10/31/17.
 */

public class MyApplication extends MultiDexApplication {
    private static MyApplication myApplication;
    private FbUtil fbUtil;

    public static MyApplication getMyApplication(){
        return myApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public synchronized FbUtil getUtil(){
        if(fbUtil == null){
            fbUtil = new FbUtil(this.getApplicationContext());
        }

        return fbUtil;
    }
}
