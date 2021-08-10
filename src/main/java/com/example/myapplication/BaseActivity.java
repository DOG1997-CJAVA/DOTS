package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.myapplication.language.FunApplication;
import com.example.myapplication.language.Utility;

/**
 * @Creator LAN
 * @Date 2021/8/6
 * @Description: BaseLanguageActivity继承BaseActivity，具体Activity如PageActivity继承BaseLanguageActivity。
 * BaseLanguageActivity就只处理Language 语言切换
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Utility.resetActivityTitle(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(FunApplication.localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}