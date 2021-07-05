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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class Ready12Activity extends Activity {
    final int TIME = 6;                 //定义时间长度
    final int TIMER_MSG = 0x001;        //定义消息代码
    private ProgressBar progressBar;          //声明水平进度条
    private int mProgressStatus = 0;    //定义完成进度
    private Button btnCountTimer;
    private TextView testRemindText;
    private TextView testPleaseWait;
    private HashMap answerInfo = new HashMap();//储存与OptionActivity的交互数据
    //接收Option12Activity页面传来的数据
    int accept1 = 0;
    int number_count = 0;
    int status = 0;//设置重闻的次数
    //设置气味的默认重闻次数
    static int retry = 1;
    static int odor_release_delay = 2000;
    static int random_fix_mode = 1;
    private ServiceConnection sc;
    public SocketService socketService;
    //private final String[] status_12 = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112"};
    private final String[] status_40 = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "309", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"};
    private List<Integer> indices = new ArrayList<Integer>(12);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        bindSocketService();
        //ButterKnife专注于Android系统的View注入框架,将Android视图和回调方法绑定到成员变量和方法上；可视化一键生成；可以减少大量的findViewById以及setOnClickListener代码，是注解中相对简单易懂的开源框架 。
        ButterKnife.bind(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);      //获取进度条组件
        progressBar.setVisibility(View.VISIBLE);
        testRemindText = (TextView) findViewById(R.id.test_remind_text);
        testRemindText.setVisibility(View.VISIBLE);
        testPleaseWait = (TextView) findViewById(R.id.text_please_wait);
        testPleaseWait.setVisibility(View.INVISIBLE);
        btnCountTimer = (Button)findViewById(R.id.btnPrepareToStart);
        btnCountTimer.setVisibility(View.VISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry = retryCount.getInt("retry_time",1);
        odor_release_delay = retryCount.getInt("odor_release_delay",2000);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        if (random_fix_mode == 0) {
            for (int c = 0; c < 40; c++) {
                indices.add(c);
            }
        }
        btnCountTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sc!=null&&socketService!=null) {
                    MyCountTimer myCountTimer = new MyCountTimer( btnCountTimer, "");
                    myCountTimer.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (random_fix_mode == 1) {//固定模式
                                    Thread.sleep(odor_release_delay); // 上位机命令传输延时 而下位机打开计时 需要修改单片机程序 否则关闭时间出问题
                                    socketService.sendOrder(status_40[accept1]);
                                    Timer my_delay_timer = new Timer();
                                    my_delay_timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            socketService.sendOrder(status_40[accept1]);
                                        }
                                    }, 200);
                                    Thread.sleep(3000 - odor_release_delay); //总计5秒 休眠3秒
                                    handler.sendEmptyMessage(TIMER_MSG);//发送消息，启动进度条
                                    //固定模式下accept1 不再使用option40activity send3返回的accept2赋值递增 采用自增
                                }
                                if (random_fix_mode == 0) {//随机模式
                                    Thread.sleep(odor_release_delay);
                                    accept1 = getRandomOdor();
                                    socketService.sendOrder(status_40[accept1]);
                                    Timer my_delay_timer = new Timer();
                                    my_delay_timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            socketService.sendOrder(status_40[accept1]);
                                        }
                                    }, 200);
                                    Thread.sleep(3000 - odor_release_delay);
                                    handler.sendEmptyMessage(TIMER_MSG);
                                }
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

    public static void getRetryData(Context context){//通过上下文拿到MangementTabActivity定义的retryCount
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry = retryCount.getInt("retry_time",1);
        odor_release_delay = retryCount.getInt("odor_release_delay",2000);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
    }

    private int getRandomOdor() {
        int arrIndex = (int) ((double) indices.size() * Math.random());
        int randomIndex = indices.get(arrIndex);
        indices.remove(arrIndex);
        return randomIndex;
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
            Date date = new Date();//***
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");//***
            TextView text_appear = (TextView) findViewById(R.id.text_appear);
            if (TIME - mProgressStatus > 0) {
                mProgressStatus++;
                if(mProgressStatus == 1) {//只记录一次
                    answerInfo.put("odorStartTime",sdf.format(date));
                }
                btnCountTimer.setVisibility(View.INVISIBLE);
                text_appear.setText("闻");
                text_appear.setEnabled(false);
                progressBar.setProgress( mProgressStatus);
                handler.sendEmptyMessageDelayed(TIMER_MSG, 500);
            } else if(retry != 6 && status<retry){//retry = 5 不再询问，不设置重闻选项
                answerInfo.put("odorEndTime",sdf.format(date));
                answerInfo.put("retryCount",status+"");//重闻次数
                AlertDialog dialog = new AlertDialog.Builder(Ready12Activity.this)
                        .setIcon(R.mipmap.wenhao)
                        .setTitle(" ")
                        .setMessage("您是否闻到了气味？")
                        .setNegativeButton("否",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status++;//重闻状态+1
                                answerInfo.put("retryCount",status+"");
                                Button NegativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                text_appear.setText("");
                                btnCountTimer.setVisibility(View.VISIBLE);
                                btnCountTimer.setText("点击再闻一次");
                                mProgressStatus=0;//进度条归零
                                progressBar.setProgress(mProgressStatus);
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
                                status=0;
                                progressBar.setProgress(mProgressStatus);
                                progressBar.setVisibility(View.INVISIBLE);
                                testRemindText.setVisibility(View.INVISIBLE);
                                Intent intent=new Intent(Ready12Activity.this, Option12Activity.class);
                                intent.putExtra("send2",accept1);
                                intent.putExtra("number_count", number_count);
                                intent.putExtra("odorStartTime",(String) answerInfo.get("odorStartTime"));
                                intent.putExtra("odorEndTime",(String) answerInfo.get("odorEndTime"));
                                intent.putExtra("retryCount",(String) answerInfo.get("retryCount"));
                                intent.putExtra("test_channel",(String) answerInfo.get("test_channel"));
                                startActivityForResult(intent, 0);
                                accept1++;
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
            }else if(retry == 6 || retry==status){
                text_appear.setText("");
                testPleaseWait.setVisibility(View.VISIBLE);
                testPleaseWait.setVisibility(View.VISIBLE);
                btnCountTimer.setText("准备");
                btnCountTimer.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                progressBar.setProgress( mProgressStatus);
                progressBar.setVisibility(View.INVISIBLE);
                testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready12Activity.this, Option12Activity.class);//打开答题界面
                intent.putExtra("send2",accept1);
                intent.putExtra("number_count", number_count);
                intent.putExtra("odorStartTime",(String) answerInfo.get("odorStartTime"));
                intent.putExtra("odorEndTime",(String) answerInfo.get("odorEndTime"));
                intent.putExtra("retryCount",(String) answerInfo.get("retryCount"));
                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
                status=0;
                accept1++;
                startActivityForResult(intent, 0);//startActivityForResult的主要作用就是它可以回传数据
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
                number_count = b.getInt("send3");
                if(number_count>=1){
                    testPleaseWait.setVisibility(View.INVISIBLE);
                    btnCountTimer.setText("下一个气味");
                    btnCountTimer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
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
