package com.gelostech.zoomsta.commoners;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.lang.ref.WeakReference;

/**
 * Created by tirgei on 10/31/17.
 */

public class FbUtil {
    private WeakReference<Context> weakReference;

    public FbUtil(Context context){
        weakReference = new WeakReference(context);
    }

    public void createCookie(Context context){
        if (Build.VERSION.SDK_INT >= 22) {
            android.webkit.CookieManager.getInstance().removeAllCookies(null);
            android.webkit.CookieManager.getInstance().flush();
            return;
        }
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        cookieSyncManager.startSync();
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();
        cookieSyncManager.stopSync();
        cookieSyncManager.sync();

    }

    public boolean isValid(String cookie){
        Log.d("ValidityCheck", "" + cookie);
        return !TextUtils.isEmpty(cookie) && cookie.contains("sessionid");
    }


}
