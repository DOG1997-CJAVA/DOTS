package com.example.myapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.myapplication.common.EventMsg;
import com.example.myapplication.db.Constants;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.service.SocketService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConnectActivity extends BaseActivity {

    @BindView(R.id.ipTv)
    EditText ipTv;
    @BindView(R.id.portTv)
    EditText portTv;
    @BindView(R.id.connectBtn)
    Button connectBtn;
    @BindView(R.id.btnT)
    Button btnT;

    private boolean isConnectSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);
        ButterKnife.bind(this);


        /*register EventBus*/
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @OnClick(R.id.connectBtn)
    public void onViewClicked() {

        String ip = ipTv.getText().toString().trim();
        String port = portTv.getText().toString().trim();

        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            Toast.makeText(this, "ip和端口号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //先判断 Service是否正在运行 如果正在运行  给出提示  防止启动多个service
        if (isServiceRunning()) {
            Toast.makeText(this, "连接服务已运行", Toast.LENGTH_SHORT).show();
            return;
        }
        //Intent intent = new Intent(this, SocketService.class);
        //unbindService(SocketService);
        //stopService(intent);
        /*启动service*/
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra(Constants.INTENT_IP, ip);
        intent.putExtra(Constants.INTENT_PORT, port);
        startService(intent);
    }

    @OnClick(R.id.btnT) //返回
    public void onView1Clicked() {
        Intent intent = new Intent(ConnectActivity.this,MainActivity.class);
        startActivity(intent);

    }
    /*连接成功的话  直接进去主页面*/

    @Subscribe(threadMode = ThreadMode.MAIN)
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
    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if ("com.example.myapplication.service.SocketService".equals(aInfo.service.getClassName())) return true;
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
