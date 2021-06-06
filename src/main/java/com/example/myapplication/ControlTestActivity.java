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

import butterknife.ButterKnife;

public class ControlTestActivity extends Activity implements View.OnClickListener {
    //四个通道的对应的状态码
    private String[] status = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "401", "402", "403", "404"};
    private static final int CONNECTED_RESPONSE = 0;
    private static final int RESPONSE_TIMEOUT = 1;
    private static final int SEND_RESPONSE = 2;
    private static final int RECEIVER_RESPONSE = 3;
    private static final String HOST = "192.168.4.1";
    private static final int PORT = 8086;
    private static boolean pump1 = false, pump2 = false;
    Socket socket = null;
    private Button btn_connect;
    private PrintStream out;    //  打印输出流
    private ConnectThread mConnectThread;   //  TCP连接线程
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
        //socketService.sendOrder("405");//进入手动 或者扫描界面 自动切换常开 互斥 调试模式
        Resources res = getResources();
        Chronometer metronome = (Chronometer) this.findViewById(R.id.chronometer);
        metronome.setFormat("通道打开计时:s%");
        for (int i = 1; i < 49; i++) {
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
                        socketService.sendOrder("407");
                    }
                }
            });
            bts.add(btn);
        }

        ToggleButton btn49 = (ToggleButton) findViewById(R.id.pumb1);
        btn49.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn49.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("401");
                } else {
                    btn49.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("403");
                }
            }
        });

        ToggleButton btn50 = (ToggleButton) findViewById(R.id.pumb2);
        btn50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn50.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("402");
                } else {
                    btn50.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("404");
                }
            }
        });

        ToggleButton btn51 = (ToggleButton) findViewById(R.id.btn_value_const);
        btn51.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn51.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    socketService.sendOrder("405");
                } else {
                    btn51.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    socketService.sendOrder("406");
                }
            }
        });

        ToggleButton btn53 = (ToggleButton) findViewById(R.id.btn_connect);
        if (btn_connect != null) {
            btn53.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn53.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                    } else {
                        btn53.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                    }
                }
            });
        }

        ToggleButton btn54 = (ToggleButton) findViewById(R.id.btn_auto_scan);
            btn54.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn54.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                        cdt.start();
                        toastMsg("48通道 自动扫描检测已开始");
                    } else {
                        //手动点击取消 结束自动扫描
                        btn54.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        cdt.cancel();
                        channel = 1;
                        btnSetState(true);//取消扫描，恢复原样，使能各个按键的点击
                        for(int i = 0; i<48 ;i++) {
                            bts.get(i).setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        }
                        toastMsg("自动扫描检测已取消");
                    }
                }
            });

        ToggleButton btn55 = (ToggleButton) findViewById(R.id.btn_fan);
        if (btn_connect != null) {
            btn53.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn55.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
                        socketService.sendOrder("501"); //501开启排风扇
                    } else {
                        btn55.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
                        socketService.sendOrder("500"); //502关闭排风扇
                    }
                }
            });
        }
    }

    private void btnSetState(boolean state) {
        for (int j = 0; j < 48; j++) {
/*            System.out.println(j);
            System.out.println(bts.size());
            System.out.println(bts.get(j));*/
            ToggleButton my_btn = (ToggleButton) bts.get(j);
            my_btn.setClickable(state);
        }
    }

    private CountDownTimer cdt = new CountDownTimer(96000,2000) {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTick(long millisUntilFinished) {
            btnSetState(false);
            ToggleButton my_btn = (ToggleButton) bts.get(channel-1);//btn1开始
            my_btn.setBackgroundDrawable(getDrawable(R.drawable.bt_shape));
            socketService.sendOrder(status[channel-1]);//索引0开始
            System.out.println(channel-1);
            channel++;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onFinish() {
            btnSetState(true);//自动扫描结束，恢复原样，使能各个按键的点击
            for(int i = 0; i<48 ;i++) {
                bts.get(i).setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
            }
            ToggleButton btn_auto_scan = (ToggleButton) findViewById(R.id.btn_auto_scan);
            btn_auto_scan.setBackgroundDrawable(getDrawable(R.drawable.bt_shape1));
            btn_auto_scan.setChecked(false);
            channel = 1;
        }
    };

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
        if(cdt!=null){
            cdt.cancel();
            cdt = null;
        }
        super.onDestroy();
/*      socketService.sendOrder("406");//恢复测试模式 初始化状态
        socketService.sendOrder("102");
        socketService.sendOrder("102");*/
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
