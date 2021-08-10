package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option2Activity;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import butterknife.ButterKnife;

public class Ready2Activity extends BaseActivity {
    final int TIME = 5;    //定义时间长度 只读常量
    final int TIMER_MSG = 0x001;    //定义消息代码
    private ProgressBar timer;    //声明水平进度条
    private int mProgressStatus = 0;    //定义完成进度
    private Button btnCountTimer;
    private TextView testPleaseWait;
    int accept1;
    int status;
    static int retry_2 =1;//未实现设置为2
    private ServiceConnection sc;
    public SocketService socketService;

    private static final String HOST = "192.168.4.1";
    private static final int PORT = 8086 ;
    Socket socket = null;
    private Button btn_connect;
    private PrintStream out;    //  打印输出流
    private ConnectThread mConnectThread;   //  TCP连接线程
    private TextView testRemindText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        bindSocketService();
        ButterKnife.bind(this);
        timer = (ProgressBar) findViewById(R.id.progressBar);      //获取进度条组件
        timer.setVisibility(View.VISIBLE);
        testRemindText = (TextView) findViewById(R.id.test_remind_text);
        testRemindText.setVisibility(View.VISIBLE);
        testPleaseWait = (TextView) findViewById(R.id.text_please_wait);
        testPleaseWait.setVisibility(View.INVISIBLE);
        btnCountTimer = (Button)findViewById(R.id.btnPrepareToStart);
        btnCountTimer.setVisibility(View.VISIBLE);
        btnCountTimer.setOnClickListener(new View.OnClickListener() { //点击准备后开启倒计时
            @Override
            public void onClick(View v) {
                if (sc!=null&&socketService!=null) { //连接无误
                    socketService.sendOrder("" + (101 + accept1));
                    MyCountTimer myCountTimer = new MyCountTimer( btnCountTimer, "");
                    myCountTimer.start();
                    new Thread(new Runnable() { //新开启 一个线程
                        @Override
                        public void run() { //线程执行任务的主要代码
                            try {
                                Thread.sleep(3000); // 休眠3秒 等到倒计时321结束
                                handler.sendEmptyMessage(TIMER_MSG);//发送消息，启动进度条
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else
                    {
                    Toast.makeText(Ready2Activity.this,"网络异常！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getRetryData(Context context){//通过上下文拿到MangementTabActivity
        // static修饰的变量和方法,从属于类
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry_2 = retryCount.getInt("retry_time",1);
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
        bindService(intent, sc, BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);

    }
    Handler  handler = new Handler() {
        @Override
        public void  handleMessage(Message msg) {
            //当前进度大于0
            TextView text_appear = (TextView) findViewById(R.id.text_appear);
            if (TIME - mProgressStatus > 0) {
                btnCountTimer.setVisibility(View.INVISIBLE);
                text_appear.setText("闻");
                mProgressStatus++;         //进度+1
                timer.setProgress( mProgressStatus);            //从左到右更新进度条的显示进度
                handler.sendEmptyMessageDelayed(TIMER_MSG, 500);    //延迟半秒发送消息
            }
            else if(status< retry_2)
            {
                AlertDialog dialog = new AlertDialog.Builder(Ready2Activity.this)
                        .setIcon(R.mipmap.wenhao)//设置标题的图片 更换问好
                        .setTitle(" ")//设置对话框的标题
                        .setMessage("您是否闻到了气味？")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Button NegativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                text_appear.setText("");
                                btnCountTimer.setVisibility(View.VISIBLE);
                                btnCountTimer.setText("点击再闻一次");
                                //进度条归零
                                mProgressStatus=0;
                                timer.setProgress( mProgressStatus);
                                //重闻状态+1
                                status++;
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Button PositiveButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                text_appear.setText("");
                                //设置准备按钮为空字符串
                                testPleaseWait.setVisibility(View.VISIBLE);
                                btnCountTimer.setVisibility(View.INVISIBLE);
                                mProgressStatus = 0;
                                timer.setProgress( mProgressStatus);
                                timer.setVisibility(View.INVISIBLE);
                                testRemindText.setVisibility(View.INVISIBLE);
                                Intent intent=new Intent(Ready2Activity.this, Option2Activity.class);
                                intent.putExtra("send2",accept1);
                                startActivityForResult(intent, 0);
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();       //提示时间已到
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
            }else if(retry_2 ==status){
                text_appear.setText("");
                testPleaseWait.setVisibility(View.VISIBLE);
                btnCountTimer.setText("准备");
                btnCountTimer.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                timer.setProgress( mProgressStatus);
                timer.setVisibility(View.INVISIBLE);
                testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready2Activity.this, Option2Activity.class);//打开答题界面
                intent.putExtra("send2",accept1);
                status=0;
                startActivityForResult(intent, 0);
            }
        }
    };

    /**
     * 复写onActivityResult方法
     * 当ReadyActivity页面关闭时，接收Option12Activiy页面传递过来的数据。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                accept1 = b.getInt("send3");//str即为回传的值
                if(accept1>=1){
                    //如果是第二个题，则显示下一个气味
                    btnCountTimer.setText("下一个气味");
                    btnCountTimer.setVisibility(View.VISIBLE);
                    testPleaseWait.setVisibility(View.INVISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    TextView testRemindText = (TextView) findViewById(R.id.test_remind_text);
                    testRemindText.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }


    private class ConnectThread extends Thread{
        private String ip;
        private int port;
        public ConnectThread(String ip,int port){
            this.ip=ip;
            this.port=port;
        }
        @Override
        public void run(){
            try {
                socket=new Socket(ip,port);
                out = new PrintStream(socket.getOutputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
// 这里处理逻辑代码，该方法仅适用于2.0或更新版的sdk
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.talk)//设置标题的图片
                .setTitle("提示")//设置对话框的标题
                .setMessage("是否退出当前页面？退出将不会保留任何数据")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Ready2Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
        return;
    }
}
