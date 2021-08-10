package com.example.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.myapplication.databinding.ActivityControltestBinding;
import com.example.myapplication.service.SocketService;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class ControlTestActivity extends BaseActivity implements View.OnClickListener {
    private final String[] status = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"};
    private static final int CONNECTED_RESPONSE = 0;
    private static final int RESPONSE_TIMEOUT = 1;
    private static final int SEND_RESPONSE = 2;
    private static final int RECEIVER_RESPONSE = 3;
    private ActivityControltestBinding bindingcontrol;
    Socket socket = null;

    private ServiceConnection sc;
    private int channel = 1;
    public SocketService socketService;
    public List<ToggleButton> bts = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingcontrol = ActivityControltestBinding.inflate(getLayoutInflater());
        setContentView(bindingcontrol.getRoot());
        bindSocketService();
        ButterKnife.bind(this);
        Resources res = getResources();
        bindingcontrol.chronometer.setFormat("通道打开计时:s%");
        for (int i = 1; i < 41; i++) {//实际机箱只有40个电磁阀 预留的八个暂不初始化
            int id = res.getIdentifier("btn" + i, "id", getPackageName());
            ToggleButton btn = findViewById(id);
            int finalI = i;
            btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {//开
                    btnSetState(false);
                    btn.setClickable(true);
                    btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                    socketService.sendOrder(status[finalI - 1]);
                    bindingcontrol.chronometer.setBase(SystemClock.elapsedRealtime());//复位，设置基准时间，非开启时间，从0开启
                    bindingcontrol.chronometer.start();
                } else {//关
                    bindingcontrol.chronometer.stop();//暂停但不复位，供观察记录
                    btnSetState(true);
                    btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                    socketService.sendOrder("410");//关闭所有气味通道 打开清洗气路
                }
            });
            bts.add(btn);
        }

        bindingcontrol.pumb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.pumb1.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("401");
            } else {
                bindingcontrol.pumb1.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("403");
            }
        });

        bindingcontrol.pumb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.pumb2.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("402");
            } else {
                bindingcontrol.pumb2.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("404");
            }
        });

        //常开/延时自动关闭 模式切换 进入手动控制界面默认常开
        bindingcontrol.btnValueConst.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnValueConst.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("405");
            } else {
                bindingcontrol.btnValueConst.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("406");
            }
        });

        bindingcontrol.btnConnect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnConnect.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
            } else {
                bindingcontrol.btnConnect.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            }
        });

        bindingcontrol.btnFan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnFan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("407");
            } else {
                bindingcontrol.btnFan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("408");
            }
        });

        bindingcontrol.btnClean.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnClean.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("411");//打开清洗 关闭加味
            } else {
                bindingcontrol.btnClean.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("412");//关闭清洗 打开加味
            }
        });

        bindingcontrol.btnAutoScan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                cdt.start();
                toastMsg("40通道 自动扫描检测已开始");
            } else {
                //手动点击取消 结束自动扫描
                bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                cdt.cancel();
                socketService.sendOrder("410");//中断扫描 复位已打开的电磁阀
                channel = 1;
                btnSetState(true);//取消扫描，恢复原样，使能各个按键的点击
                for (int i = 0; i < 40; i++) {
                    bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                }
                toastMsg("自动扫描检测已完成或已取消");
            }
        });
    }

    private CountDownTimer cdt = new CountDownTimer(20000, 500) {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTick(long millisUntilFinished) {
            btnSetState(false);
            ToggleButton my_btn = bts.get(channel - 1);//btn1开始
            my_btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
            socketService.sendOrder(status[channel - 1]);//索引0开始 打开各个通道
            System.out.println(channel - 1);
            channel++;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onFinish() {
            socketService.sendOrder("410");
            btnSetState(true);//自动扫描结束，恢复原样，使能各个按键的点击
            for (int i = 0; i < 40; i++) {//依旧只自动扫描前40个通道
                bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            }
            bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            bindingcontrol.btnAutoScan.setChecked(false);
            channel = 1;
        }
    };

    private void btnSetState(boolean state) {
        for (int j = 0; j < 40; j++) {
            ToggleButton my_btn = bts.get(j);
            my_btn.setClickable(state);
        }
    }

    private void bindSocketService() {
        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
                Timer my_delay_timer = new Timer();
                socketService.sendOrder("414");
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("414");
                    }
                }, 100);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    /*因为Toast是要运行在主线程的  所以需要到主线程哪里去显示toast*/
    private void toastMsg(final String msg) {
        handler.post(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
            socketService.sendOrder("410");
        }
        super.onDestroy();                  //恢复测试模式 初始化状态
        Timer my_delay_timer = new Timer();
        socketService.sendOrder("413");  //切换回默认工作状态 打开气泵以及排风扇 关闭所有气味通道
        my_delay_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                socketService.sendOrder("413");
            }
        }, 100);
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        toastMsg("手动管理界面已销毁");
    }

    //指定主线程 Looper.myLooper() 避免内存泄漏
    private final Handler handler = new Handler(Looper.myLooper()) {
        // 在这里进行UI操作，将结果显示到界面上 重写处理消息的方法
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED_RESPONSE:
                    bindingcontrol.btnConnect.setTextColor(Color.parseColor("#216F02"));
                    break;
                case RESPONSE_TIMEOUT:
                    Toast.makeText(getApplicationContext(), "连接失败！", Toast.LENGTH_SHORT).show();
                case RECEIVER_RESPONSE:
                case SEND_RESPONSE:
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            toastMsg("101");
        }
    }

/*    private void Connect() {
        if (socket == null || !socket.isConnected()) {
            mConnectThread = new ConnectThread(HOST, PORT);
            mConnectThread.start();
            Message message = new Message();
            message.what = CONNECTED_RESPONSE;
            handler.sendMessage(message);
        } else if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                socket = null;   //  清空mSocket
                bindingcontrol.btnConnect.setText("连接");
                Toast.makeText(ControlTestActivity.this, "连接已关闭", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = RESPONSE_TIMEOUT;
                handler.sendMessage(message);
            }
        }
    }*/

/*    private void sendData(String data) {
        if (out != null) {
            new Thread(() -> {
                try { //输出流写入发送编辑框的信息并指定类型UTF-8，注意要加换行
                    String str = data;
                    if (socket == null) {
                        return;
                    }
                    BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    outputStream.write(str);
                    // 输出流发送至服务器
                    outputStream.flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }*/

    private class ConnectThread extends Thread {
        private final String ip;
        private final int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                PrintStream out = new PrintStream(socket.getOutputStream());
                runOnUiThread(() -> {
                    bindingcontrol.btnConnect.setText("断开");
                    Toast.makeText(ControlTestActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                });
                //new HeartBeatThread().start();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ControlTestActivity.this, "连接失败", Toast.LENGTH_LONG).show());
            }
        }
    }

}
