package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
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
import android.content.Context;
import androidx.annotation.Nullable;

import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option12Activity;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

public class Ready12Activity extends Activity {
    final int TIME = 5;    //定义时间长度
    final int TIMER_MSG = 0x001;    //定义消息代码
    private ProgressBar timer;    //声明水平进度条
    private int mProgressStatus = 0;    //定义完成进度
    private Button btnCountTimer;
    private TextView testRemindText;
    private TextView testPleaseWait;
    //接收Option12Activity页面传来的数据
    int accept1;
    //设置重闻的次数
    int status;
    //设置气味的默认重闻次数
    static int retry = 1;
    private ServiceConnection sc;
    public SocketService socketService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        bindSocketService();
        //ButterKnife专注于Android系统的View注入框架,将Android视图和回调方法绑定到成员变量和方法上；可视化一键生成；可以减少大量的findViewById以及setOnClickListener代码，是注解中相对简单易懂的开源框架 。
        ButterKnife.bind(this);
        timer = (ProgressBar) findViewById(R.id.progressBar);      //获取进度条组件
        timer.setVisibility(View.VISIBLE);
        testRemindText = (TextView) findViewById(R.id.test_remind_text);
        testRemindText.setVisibility(View.VISIBLE);
        testPleaseWait = (TextView) findViewById(R.id.text_please_wait);
        testPleaseWait.setVisibility(View.INVISIBLE);
        btnCountTimer = (Button)findViewById(R.id.btnPrepareToStart);
        btnCountTimer.setVisibility(View.VISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry = retryCount.getInt("retry_time",1);
        btnCountTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sc!=null&&socketService!=null) {
                    socketService.sendOrder("" + (101 + accept1));
                    MyCountTimer myCountTimer = new MyCountTimer( btnCountTimer, "");
                    myCountTimer.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000); // 休眠3秒
                                handler.sendEmptyMessage(TIMER_MSG);//发送消息，启动进度条
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(Ready12Activity.this,"网络异常！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getRetryData(Context context){//通过上下文拿到MangementTabActivity
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry = retryCount.getInt("retry_time",1);
    }

    private void bindSocketService() {
        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //通过IBinder实现Android的远程调用(就是跨进程调用)
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                //调用getService()返回当前socket服务
                socketService = binder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        //bindService()方法启动服务。多个客户端可以绑定至同一个服务。如果服务此时还没有加载，bindService()会先加载它。
        bindService(intent, sc, BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭socket服务
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //当前进度大于0
            TextView text_appear = (TextView) findViewById(R.id.text_appear);
            if (TIME - mProgressStatus > 0) {
                btnCountTimer.setVisibility(View.INVISIBLE);
                text_appear.setText("闻");
                text_appear.setEnabled(false);
                mProgressStatus++;
                //从左到右更新进度条的显示进度
                timer.setProgress( mProgressStatus);
                //从右到左更新进度条的显示进度
                //timer.setProgress( TIME-mProgressStatus);
                //延迟半秒发送消息
                handler.sendEmptyMessageDelayed(TIMER_MSG, 500);
            } else if(status<retry){
                AlertDialog dialog = new AlertDialog.Builder(Ready12Activity.this)
                        //设置标题的图片
                        .setIcon(R.mipmap.wenhao)
                        //设置对话框的标题
                        .setTitle(" ")
                        //设置对话框的内容
                        .setMessage("您是否闻到了气味？")
                        //设置对话框的按钮
                        .setNegativeButton("否",new DialogInterface.OnClickListener() {
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
                                Intent intent=new Intent(Ready12Activity.this, Option12Activity.class);
                                intent.putExtra("send2",accept1);
                                startActivityForResult(intent, 0);
                                dialog.dismiss();
                            }
                        }).create();
                //提示时间已到
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
            }else if(retry==status){
                text_appear.setText("");
                testPleaseWait.setVisibility(View.VISIBLE);
                testPleaseWait.setVisibility(View.VISIBLE);
                btnCountTimer.setText("准备");
                btnCountTimer.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                timer.setProgress( mProgressStatus);
                timer.setVisibility(View.INVISIBLE);
                testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready12Activity.this, Option12Activity.class);//打开答题界面
                intent.putExtra("send2",accept1);
                status=0;
                //startActivityForResult的主要作用就是它可以回传数据
                startActivityForResult(intent, 0);
            }
        }
    };

    /**
     * 复写onActivityResult方法
     * 当ReadyActivity页面关闭时，接收Option12Activiy页面传递过来的数据。
     * str即为回传的值 每答一道题目 在Option12Activity里accept1+1 后通过send3回传 此处接收
     * data为Option12Activity中回传的Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在Option12Activity中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras();
                accept1 = b.getInt("send3");
                if(accept1>=1){
                    //如果是第二个题，则显示下一个气味
                    testPleaseWait.setVisibility(View.INVISIBLE);
                    testPleaseWait.setVisibility(View.INVISIBLE);
                    btnCountTimer.setText("下一个气味");
                    btnCountTimer.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    TextView testRemindText = (TextView) findViewById(R.id.test_remind_text);
                    testRemindText.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
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
                        Intent intent = new Intent(Ready12Activity.this, TabActivity.class);
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
