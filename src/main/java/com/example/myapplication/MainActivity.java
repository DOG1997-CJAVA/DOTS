package com.example.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.common.EventMsg;
import com.example.myapplication.db.Constants;
import com.example.myapplication.service.SocketService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private Button btn100,btn101,btn102;
    private ServiceConnection sc;
    public SocketService socketService;
    public ImageView icon10,icon11;
    //public boolean connect_sur_ero = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindSocketService();// 通过binder拿到service 开启服务
        ButterKnife.bind(this);

        //Intent intent = getIntent();
        //connect_sur_ero = intent.getBooleanExtra("connect_statue",false);
        /*register EventBus*/
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        icon10 = (ImageView)findViewById(R.id.icon_connect_success);
        icon10.setVisibility(View.GONE);//默认链接失败，等待更新
        icon11 = (ImageView)findViewById(R.id.icon_connect_error);
        icon11.setVisibility(View.VISIBLE);
        btn100 = (Button)findViewById(R.id.btn100);
        btn101 = (Button)findViewById(R.id.btn101);
        btn102 = (Button)findViewById(R.id.btn102);
        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到SimulationTesterActivity 模拟测试页面
                Intent intent1=new Intent(MainActivity.this,SimulationTesterActivity.class);
                startActivity(intent1);
            }
        });
        //跳转到SimulationTesterActivity 测试选择页面
        btn101.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(MainActivity.this,TabActivity.class);
                startActivity(intent2);
            }
        });
        btn102.setOnClickListener(new View.OnClickListener() {//管理员界面
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(MainActivity.this,ManagementTabActivity.class);
                startActivity(intent3);
            }
        });
    }
    private void bindSocketService() {
        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);//开启socket服务；自动开启
    }

    //表示无论事件是在哪个线程发布出来的，该事件订阅方法onEvent都会在UI线程中执行
    //订阅事件  Android中只能在UI线程中更新UI
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConnectStatue(EventMsg msg) {
        if (msg.getTag().equals(Constants.CONNET_SUCCESS)) {
            /*接收到这个消息说明连接成功*/
            icon10.setVisibility(View.VISIBLE);
            icon11.setVisibility(View.GONE);
        }
        if(msg.getTag().equals(Constants.CONNET_FAIL)){
            icon11.setVisibility(View.VISIBLE);
            icon10.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭socket服务
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}