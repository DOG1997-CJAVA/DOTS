package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.example.myapplication.databinding.ActivityFinishTestBinding;
import com.example.myapplication.language.BaseActivity;

import org.greenrobot.eventbus.EventBus;

public class TestFinishActivity extends BaseActivity {
    private ActivityFinishTestBinding finishTestBinding;
    private int TIME = 3;
    private boolean isSkip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishTestBinding = ActivityFinishTestBinding.inflate(getLayoutInflater());
        setContentView(finishTestBinding.getRoot());

        // 将欢迎界面系统自带的标题栏隐藏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case -2:
                        finishTestBinding.skip.setText(getString(R.string.test_finish_return) + "( " + TIME + "s )");
                        break;
                    case 1:
                        // 这里记得要判断是否选择跳过，防止重复加载LoginActivity
                        if (!isSkip) {
                            Intent intent = new Intent(TestFinishActivity.this, MainActivity.class);
                            isSkip = true;
                            startActivity(intent);
                            TestFinishActivity.this.finish();
                        }
                        break;
                }
            }
        };

        // 开启一个线程倒计时
        new Thread(() -> {
            for (; TIME > 0; TIME--) {
                handler.sendEmptyMessage(-2);
                if (TIME <= 0) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(1);
        }).start();
    }


    protected void onDestroy() {
        super.onDestroy();
        /*unregister EventBus*/
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}