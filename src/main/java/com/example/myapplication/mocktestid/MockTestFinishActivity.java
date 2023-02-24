package com.example.myapplication.mocktestid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.R;
import com.example.myapplication.TabActivity;
import com.example.myapplication.databinding.ActivityFinishMockTestBinding;

import org.greenrobot.eventbus.EventBus;

public class MockTestFinishActivity extends BaseActivity {
    private ActivityFinishMockTestBinding finishMockTestBinding;
    private int TIME = 3;
    private boolean isSkip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishMockTestBinding = ActivityFinishMockTestBinding.inflate(getLayoutInflater());
        setContentView(finishMockTestBinding.getRoot());

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
                        finishMockTestBinding.skipMock.setText(getString(R.string.test_finish_return) + "( " + TIME + "s )");
                        break;
                    case 1:
                        if (!isSkip) {
                            Intent intent = new Intent(MockTestFinishActivity.this, TabActivity.class);
                            isSkip = true;
                            startActivity(intent);
                            MockTestFinishActivity.this.finish();
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}