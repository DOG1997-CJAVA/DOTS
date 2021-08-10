package com.example.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.common.EventMsg;
import com.example.myapplication.db.Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    /*socket*/
    private Socket socket;
    /*连接线程*/
    private Thread connectThread;
    private Timer timer = new Timer();
    private OutputStream outputStream;
    private SocketBinder sockerBinder = new SocketBinder();
    private String ip;
    private String port;
    private TimerTask task;

    /*默认不打开重连*/
    private boolean isReConnect = true;
    private Handler handler = new Handler(Looper.getMainLooper());

    //onBind()：在服务没有绑定过任何ServiceConnection时才会调用，
    // 主要是返回服务的IBinder下转类对象给活动，实现活动控制服务
    @Override
    public IBinder onBind(Intent intent) {
        return sockerBinder;
    }

    //bindService()：绑定服务，主要是为了让活动与服务之间可以沟通而搭建一条桥梁
    //通过ServiceConnection把Activity中的IBinder下转类（子类）对象 指向 服务中的IBinder下转类实例，
    public class SocketBinder extends Binder {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        public SocketService getService() {
            return SocketService.this;
        }
    }

    //onCreate()：每次服务创建的时候调用，在任何Context中调用startService()时，
    //如果该服务还没创建，就会先调用onCreate()，再去调用onStartCommand()，
    //如果已经创建，则不会再调用onCreate()，直接调用onStartCommand()；

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*拿到传递过来的ip和端口号*/
        ip = intent.getStringExtra(Constants.INTENT_IP);
        port = intent.getStringExtra(Constants.INTENT_PORT);
        /*初始化socket*/
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    private void toastMsg(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*初始化socket*/
    private void initSocket() {
        if (socket == null && connectThread == null) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    socket = new Socket();
                    try {
                        /*超时时间为2秒*/
                        socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 2000);
                        /*连接成功的话  发送心跳包*/
                        if (socket.isConnected()) {
                            /*因为Toast是要运行在主线程的  这里是子线程  所以需要到主线程哪里去显示toast*/
                            toastMsg("已连接到仪器");
                            /*发送连接成功的消息*/
                            EventMsg msg = new EventMsg();
                            for(int i=0;i<10;i++){
                            msg.setTag(Constants.CONNET_SUCCESS);
                            EventBus.getDefault().post(msg);
                            /*发送心跳数据*/}
                            sendBeatData();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            //toastMsg("连接超时，请检查");
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNET_FAIL);
                            EventBus.getDefault().post(msg);
                            releaseSocket();
                        } else if (e instanceof NoRouteToHostException) {
                            toastMsg("该IP地址不存在，请检查");
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNET_FAIL);
                            EventBus.getDefault().post(msg);
                            stopSelf();
                        } else if (e instanceof ConnectException) {
                            toastMsg("连接异常或被拒绝，请检查");
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNET_FAIL);
                            EventBus.getDefault().post(msg);
                            stopSelf();
                        }
                    }
                }
            });
            /*启动连接线程*/
            connectThread.start();
        }
    }



    /*发送数据*/
    public void sendOrder(final String order) {
        if (socket != null && socket.isConnected()) {
            /*发送指令*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        if (outputStream != null) {
                            //设置输出格式为gbk
                            outputStream.write((order).getBytes("gbk"));
                            outputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            toastMsg("连接出错,数据发送失败，请重试");
        }
    }

    /*定时发送心跳数据 保持本客户端连接*/
    private void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        /*这里的编码方式根据你的需求去改*/
                        outputStream.write(("keep_connect").getBytes("gbk"));
                        outputStream.flush();
                        EventMsg msg = new EventMsg();
                        msg.setTag(Constants.CONNET_SUCCESS);
                        EventBus.getDefault().post(msg);
                    } catch (Exception e) {
                        /*发送失败说明socket断开了或者出现了其他错误*/
                        toastMsg("连接断开，正在重连");
                        EventMsg msg = new EventMsg();
                        msg.setTag(Constants.CONNET_FAIL);
                        EventBus.getDefault().post(msg);
                        /*重连*/
                        releaseSocket();
                        e.printStackTrace();
                    }
                }
            };
        }
        timer.schedule(task, 0, 20000);
    }

    /*释放资源*/
    private void releaseSocket() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            socket = null;
        }
        if (connectThread != null) {
            connectThread = null;
        }
        /*若开启自动重连 重新初始化socket*/
        if (isReConnect) {
            initSocket();
        }
    }

    //onDestroy()：在服务销毁的时候调用
    // 在调用stopService()：停止服务，有可能会触发onDestroy()；
    //stopService()与unbindService()有可能会触发onDestroy()，
    //要触发就必须满足：服务停止，没有ServiceConnection绑定：
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SocketService", "onDestroy");
        toastMsg("onDestroy() 服务销毁");
        isReConnect = false;
        releaseSocket();
    }
}
