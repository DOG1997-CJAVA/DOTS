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
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.myapplication.service.SocketService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class ControlTestActivity extends Activity implements View.OnClickListener {
    private final String[] status = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "309", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"}; //备注 测试主板故障 20通道临时切换为309
    //"309", "310", "311", "312", "313", "314", "315", "316", "401", "402", "403", "404"
    private static final int CONNECTED_RESPONSE = 0;
    private static final int RESPONSE_TIMEOUT = 1;
    private static final int SEND_RESPONSE = 2;
    private static final int RECEIVER_RESPONSE = 3;
    private static final String HOST = "192.168.4.1";
    private static final int PORT = 8086;
    Socket socket = null;
    private Button btn_connect;
    private PrintStream out;
    private ConnectThread mConnectThread;   //TCP连接线程
    private ServiceConnection sc;
    private int channel = 1;
    public SocketService socketService;
    public List<ToggleButton> bts = new ArrayList<ToggleButton>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controltest);
        bindSocketService();
        ButterKnife.bind(this);
        Resources res = getResources();
        Chronometer metronome = (Chronometer) this.findViewById(R.id.chronometer);
        metronome.setFormat("通道打开计时:s%");
        for (int i = 1; i < 41; i++) {//实际机箱只有40个电磁阀 预留的八个暂不初始化
            int id = res.getIdentifier("btn" + i, "id", getPackageName());
            ToggleButton btn = (ToggleButton) findViewById(id);
            int finalI = i;
            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {//开
                        btnSetState(false);
                        btn.setClickable(true);
                        btn.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                        socketService.sendOrder(status[finalI - 1]);
                        metronome.setBase(SystemClock.elapsedRealtime());//复位，设置基准时间，非开启时间，从0开启
                        metronome.start();
                    } else {//关
                        metronome.stop();//暂停但不复位，供观察记录
                        btnSetState(true);
                        btn.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        socketService.sendOrder("410");//关闭所有气味通道 打开清洗气路
                    }
                }
            });
            bts.add(btn);
        }

        ToggleButton btn_pumb1 = (ToggleButton) findViewById(R.id.pumb1);//pumb1
        btn_pumb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_pumb1.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("401");
                } else {
                    btn_pumb1.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("403");
                }
            }
        });

        ToggleButton btn_pumb2 = (ToggleButton) findViewById(R.id.pumb2);//pumb2
        btn_pumb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_pumb2.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("402");
                } else {
                    btn_pumb2.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("404");
                }
            }
        });

        ToggleButton btn_value_const = (ToggleButton) findViewById(R.id.btn_value_const);//常开/延时自动关闭 模式切换 进入手动控制界面默认常开
        btn_value_const.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_value_const.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("405");
                } else {
                    btn_value_const.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("406");
                }
            }
        });

        ToggleButton btn_connect = (ToggleButton) findViewById(R.id.btn_connect);
        if (btn_connect != null) {
            btn_connect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn_connect.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    } else {
                        btn_connect.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    }
                }
            });
        }

        ToggleButton btn_fan = (ToggleButton) findViewById(R.id.btn_fan);
        if (btn_connect != null) {
            btn_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn_fan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                        socketService.sendOrder("407");
                    } else {
                        btn_fan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        socketService.sendOrder("408");
                    }
                }
            });
        }

        ToggleButton btn_clean = (ToggleButton) findViewById(R.id.btn_clean);
        if (btn_connect != null) {
            btn_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn_clean.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                        socketService.sendOrder("411");//打开清洗 关闭加味
                    } else {
                        btn_clean.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        socketService.sendOrder("412");//关闭清洗 打开加味
                    }
                }
            });
        }

        ToggleButton btn_auto_scan = (ToggleButton) findViewById(R.id.btn_auto_scan);
        btn_auto_scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_auto_scan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    cdt.start();
                    toastMsg("40通道 自动扫描检测已开始");
                } else {
                    //手动点击取消 结束自动扫描
                    btn_auto_scan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    cdt.cancel();
                    socketService.sendOrder("410");//清除状态标志位
                    channel = 1;
                    btnSetState(true);//取消扫描，恢复原样，使能各个按键的点击
                    for (int i = 0; i < 40; i++) {
                        bts.get(i).setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    }
                    toastMsg("自动扫描检测已完成或已取消");
                }
            }
        });
    }

    private CountDownTimer cdt = new CountDownTimer(80000, 2000) {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTick(long millisUntilFinished) {
            btnSetState(false);
            ToggleButton my_btn = (ToggleButton) bts.get(channel - 1);//btn1开始
            my_btn.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
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
                bts.get(i).setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
            }
            ToggleButton btn_auto_scan = (ToggleButton) findViewById(R.id.btn_auto_scan);
            btn_auto_scan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
            btn_auto_scan.setChecked(false);
            channel = 1;
        }
    };

    private void btnSetState(boolean state) {
        for (int j = 0; j < 40; j++) {
            ToggleButton my_btn = (ToggleButton) bts.get(j);
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
                socketService.sendOrder("405");//初始化 关闭所有可能打开的通道 打开清洗气路
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("405");
                    }
                }, 200);
                socketService.sendOrder("410");
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("410");
                    }
                }, 200);
                socketService.sendOrder("407");
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("407");
                    }
                }, 200);
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
        }
        super.onDestroy();//恢复测试模式 初始化状态
        socketService.sendOrder("410");//关闭所有可能打开的通道
        Timer my_delay_timer = new Timer();
        my_delay_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                socketService.sendOrder("410");
            }
        }, 100);
        socketService.sendOrder("406");  //切换回延时模式
        my_delay_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                socketService.sendOrder("406");
            }
        }, 100);
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        toastMsg("管理测试界面已销毁");
    }

    private Handler handler = new Handler() {
        // 在这里进行UI操作，将结果显示到界面上
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED_RESPONSE:
                    btn_connect.setTextColor(Color.parseColor("#216F02"));
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
        switch (v.getId()) {
            case R.id.btn1:
                toastMsg("101");
                break;
            /*                 case R.id.btn1:
                socketService.sendOrder("101");
                break;
 case R.id.btn2:
               socketService.sendOrder("102");
                break;
            case R.id.btn3:
                socketService.sendOrder("103");
                break;
            case R.id.btn4:
                socketService.sendOrder("104");
                break;
            case R.id.btn5:
                socketService.sendOrder("105");
                break;
            case R.id.btn6:
                socketService.sendOrder("106");
                break;
            case R.id.btn7:
                socketService.sendOrder("107");
                break;
            case R.id.btn8:
                socketService.sendOrder("108");
                break;
            case R.id.btn9:
                socketService.sendOrder("109");
                break;
            case R.id.btn10:
                socketService.sendOrder("110");
                break;

            case R.id.btn11:
                socketService.sendOrder("111");
                break;
            case R.id.btn12:
                socketService.sendOrder("112");
                break;
            case R.id.btn13:
                socketService.sendOrder("113");
                break;
            case R.id.btn14:
                socketService.sendOrder("114");
                break;
            case R.id.btn15:
                socketService.sendOrder("115");
                break;
            case R.id.btn16:
                socketService.sendOrder("116");
                break;
            case R.id.btn17:
                socketService.sendOrder("201");
                break;
            case R.id.btn18:
                socketService.sendOrder("202");
                break;
            case R.id.btn19:
                socketService.sendOrder("203");
                break;
            case R.id.btn20:
                socketService.sendOrder("204");
                break;
            case R.id.btn21:
                socketService.sendOrder("205");
                break;
            case R.id.btn22:
                socketService.sendOrder("206");
                break;
            case R.id.btn23:
                socketService.sendOrder("207");
                break;
            case R.id.btn24:
                socketService.sendOrder("208");
                break;
            case R.id.btn25:
                socketService.sendOrder("209");
                break;
            case R.id.btn26:
                socketService.sendOrder("210");
                break;
            case R.id.btn27:
                socketService.sendOrder("211");
                break;
            case R.id.btn28:
                socketService.sendOrder("212");
                break;
            case R.id.btn29:
                socketService.sendOrder("213");
                break;
            case R.id.btn30:
                socketService.sendOrder("214");
                break;
            case R.id.btn31:
                socketService.sendOrder("215");
                break;
            case R.id.btn32:
                socketService.sendOrder("216");
                break;
            case R.id.btn33:
                socketService.sendOrder("301");
                break;
            case R.id.btn34:
                socketService.sendOrder("302");
                break;
            case R.id.btn35:
                socketService.sendOrder("303");
                break;
            case R.id.btn36:
                socketService.sendOrder("304");
                break;
            case R.id.btn37:
                socketService.sendOrder("305");
                break;
            case R.id.btn38:
                socketService.sendOrder("306");
                break;
            case R.id.btn39:
                socketService.sendOrder("307");
                break;
            case R.id.btn40:
                socketService.sendOrder("308");
                break;
            case R.id.btn41:
                socketService.sendOrder("309");
                break;
            case R.id.btn42:
                socketService.sendOrder("310");
                break;
            case R.id.btn43:
                socketService.sendOrder("311");
                break;
            case R.id.btn44:
                socketService.sendOrder("312");
                break;
            case R.id.btn45:
                socketService.sendOrder("313");
                break;
            case R.id.btn46:
                socketService.sendOrder("314");
                break;
            case R.id.btn47:
                socketService.sendOrder("315");
                break;
            case R.id.btn48:
                socketService.sendOrder("316");
                break;
            case R.id.pumb1:
                if (!pump1) {
                    socketService.sendOrder("401");
                    pump1 = true;
                } else {
                    socketService.sendOrder("403");
                    pump1 = false;
                }
                break;
            case R.id.pumb2:
                if (!pump2) {
                    socketService.sendOrder("402");
                    pump2 = true;
                } else {
                    socketService.sendOrder("404");
                    pump2 = false;
                }
                break;*/
/*            case R.id.btn_value_const:
                socketService.sendOrder("405");
                break;
            case R.id.btn_value_7s:
                socketService.sendOrder("406");
                break;*/
            default:
                break;
        }
    }

    private void Connect() {
//        // 开启线程来发起网络请求
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    socket = new Socket();
//                    socket.connect(new InetSocketAddress(HOST, PORT), 4000);
//                    if (socket != null) {
//                        Message message = new Message();
//                        message.what = CONNECTED_RESPONSE;
//                        handler.sendMessage(message);
//                    }
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                    Message message = new Message();
//                    message.what = RESPONSE_TIMEOUT;
//                    handler.sendMessage(message);
//                }
//            }
//        }).start();
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
                btn_connect.setText("连接");
                Toast.makeText(ControlTestActivity.this, "连接已关闭", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = RESPONSE_TIMEOUT;
                handler.sendMessage(message);
            }
        }

    }

    private void sendData(String data) {
        if (out != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                }
            }).start();
        }
    }

    //    private void TCPSend (final String data){
//        if (socket == null){
//            Toast.makeText(ControlTestActivity.this,"socket为空",Toast.LENGTH_SHORT).show();
//            return;}
//        // 开启线程来发起网络请求
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
//                    writer.println(data);
//                    Toast.makeText(ControlTestActivity.this,"发送结束",Toast.LENGTH_SHORT).show();
//                    writer.flush();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                    Message message = new Message();
//                    message.what = SEND_RESPONSE;
//                    // 将服务器返回的结果存放到Message中
//                    message.obj = "操作失败！";
//                    handler.sendMessage(message);
//                }
//            }
//        }).start();
//    }
    private class ConnectThread extends Thread {
        private String ip;
        private int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                out = new PrintStream(socket.getOutputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_connect.setText("断开");
                        Toast.makeText(ControlTestActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                    }
                });
                //new HeartBeatThread().start();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ControlTestActivity.this, "连接失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

}
