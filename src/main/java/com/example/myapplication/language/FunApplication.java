package com.example.myapplication.language;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class FunApplication extends Application {

    public static final String TAG = "FunApplication";

    // for the sake of simplicity. use DI in real apps instead
    public static LocaleManager localeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Utility.bypassHiddenApiRestrictions();
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }
}
