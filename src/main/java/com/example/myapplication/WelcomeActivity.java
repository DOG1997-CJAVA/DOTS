package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.common.EventMsg;
import com.example.myapplication.db.Constants;
import com.example.myapplication.service.SocketService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.skip)
     TextView skip;
    private int TIME = 3;
    private boolean isSkip = false;
    private boolean isConnectSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        /*register EventBus*/
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // 将欢迎界面系统自带的标题栏隐藏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case -2:
                        skip.setText("跳过( " + TIME + "s )");
                        break;
                    case 1:
                        // 这里记得要判断是否选择跳过，防止重复加载LoginActivity
                        if (!isSkip) {
                            String ip = "192.168.4.1";
                            String port = "8086";
                            //启动service
                            Intent intent_socket = new Intent(getApplicationContext(), SocketService.class);
                            intent_socket.putExtra(Constants.INTENT_IP, ip);
                            intent_socket.putExtra(Constants.INTENT_PORT, port);
                            startService(intent_socket);
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            isSkip = true;
                            startActivity(intent);
                            WelcomeActivity.this.finish();
                        }
                        break;
                }
            }
        };

        new Thread(new Runnable() {  // 开启一个线程倒计时
            @Override
            public void run() {
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
            }
        }).start();
    }

        @OnClick(R.id.skip)
        public void onViewClicked() {
            String ip = "192.168.4.1";
            String port = "8086";

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                Toast.makeText(this, "ip和端口号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            /*先判断 Service是否正在运行 如果正在运行  给出提示  防止启动多个service*/
            if (isServiceRunning("com.example.myapplication.service.SocketService")) {
                Toast.makeText(this, "连接服务已运行", Toast.LENGTH_SHORT).show();
                return;
            }

            /*启动service*/
            Intent intent = new Intent(getApplicationContext(), SocketService.class);
            intent.putExtra(Constants.INTENT_IP, ip);
            intent.putExtra(Constants.INTENT_PORT, port);
            startService(intent);
        }

        /*连接成功的话  直接进去主页面*/
        @Subscribe(threadMode = ThreadMode.MAIN) //订阅事件
        public void skipToMainActivity(EventMsg msg) {
            if (msg.getTag().equals(Constants.CONNET_SUCCESS)) {
                /*接收到这个消息说明连接成功*/
                isConnectSuccess = true;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        /**
         * 判断服务是否运行
         */
        private boolean isServiceRunning(final String className) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
            if (info == null || info.size() == 0) return false;
            for (ActivityManager.RunningServiceInfo aInfo : info) {
                if (className.equals(aInfo.service.getClassName())) return true;
            }
            return false;
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            /*unregister EventBus*/
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }

            /*如果没有连接成功  则退出的时候停止服务 */
            if (!isConnectSuccess) {
                Intent intent = new Intent(this, SocketService.class);
                stopService(intent);
            }
        }
    }



