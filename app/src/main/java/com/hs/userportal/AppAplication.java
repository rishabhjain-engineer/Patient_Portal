package com.hs.userportal;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

public class AppAplication extends MultiDexApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mContext = this;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
