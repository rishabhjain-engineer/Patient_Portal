package com.hs.userportal;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

public class AppAplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
