package com.example.myapplication;

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
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.databinding.ActivityReadyBinding;
import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option12Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Ready12Activity extends BaseActivity {
    final int TIMER_MSG = 0x001;                //定义消息代码
    private int mProgressStatus = 0;            //定义完成进度
    private HashMap answerInfo = new HashMap();//储存与OptionActivity的交互数据 接收Option12Activity页面传来的数据
    int accept1 = 0;
    int number_count = 0;
    int status = 0;             //设置重闻的次数
    int TIME = 6;               //定义时间长度
    static int retry = 1;
    static int odor_release_delay = 2000;
    static int random_fix_mode = 1;
    static int random_retry_backup;
    static boolean random_retry_status = false;
    private ServiceConnection sc;
    public SocketService socketService;
    private ActivityReadyBinding bindingRea12;

    private final String[] status_40 = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"};
    private List<Integer> indices = new ArrayList<Integer>(12);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingRea12 = ActivityReadyBinding.inflate(getLayoutInflater());
        setContentView(bindingRea12.getRoot());
        bindSocketService();
        bindingRea12.progressBar.setVisibility(View.VISIBLE);//获取进度条组件
        bindingRea12.testRemindText.setVisibility(View.VISIBLE);
        bindingRea12.textPleaseWait.setVisibility(View.INVISIBLE);
        bindingRea12.btnPrepareToStart.setVisibility(View.VISIBLE);
        bindingRea12.progressBar.setMax(TIME);//更新进度条最大值
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        TIME = retryCount.getInt("odor_release_time", 6);//默认 6*0.5s = 3s
        retry = retryCount.getInt("retry_time", 1);
        odor_release_delay = 3000 - retryCount.getInt("odor_release_delay", 2000);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        if (random_fix_mode == 0) {//添加随机数
            for (int c = 0; c < 40; c++) {
                indices.add(c);
            }
        }
        Timer my_open_timer = new Timer();
        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {
                    socketService.sendOrder("413");
                }
            }
        }, 100);
        bindingRea12.btnPrepareToStart.setOnClickListener(v -> {
            if (sc != null && socketService != null) {
                MyCountTimer myCountTimer = new MyCountTimer(bindingRea12.btnPrepareToStart, "");
                myCountTimer.start();
                new Thread(() -> {
                    try {
                        //socketService.sendOrder("413");
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
                        }
                        if (random_fix_mode == 0) {//随机模式
                            Thread.sleep(odor_release_delay);
                            if (!random_retry_status) {//随机模式下 若设置重闻次数 进入重闻 需记录刚测试的题目索引
                                accept1 = getRandomOdor();
                                random_retry_backup = accept1;
                            } else {
                                accept1 = random_retry_backup;
                                random_retry_status = false;
                            }
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
                }).start();
            } else {
                Toast.makeText(Ready12Activity.this, "网络异常！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getRetryData(Context context) {//通过上下文拿到MangementTabActivity定义的retryCount
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry = retryCount.getInt("retry_time", 1);
        odor_release_delay = 3000 - retryCount.getInt("odor_release_delay", 2000);
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
        bindService(intent, sc, BIND_AUTO_CREATE);
        //bindService()方法启动服务。多个客户端可以绑定至同一个服务。如果服务此时还没有加载，bindService()会先加载它。
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);        //关闭socket服务
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());//国外使用需要更改
            if (TIME - mProgressStatus > 0) {
                mProgressStatus++;
                if (mProgressStatus == 1) {//只记录一次
                    answerInfo.put("odorStartTime", sdf.format(date));
                }
                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                bindingRea12.textAppear.setText(getString(R.string.sniff_ifo));
                bindingRea12.textAppear.setEnabled(false);
                bindingRea12.progressBar.setProgress(mProgressStatus);
                handler.sendEmptyMessageDelayed(TIMER_MSG, 500);
            } else if (retry != 6 && status < retry) {//retry = 6 不设置重闻选项
                answerInfo.put("odorEndTime", sdf.format(date));
                answerInfo.put("retryCount", status + "");//重闻次数
                AlertDialog dialog = new AlertDialog.Builder(Ready12Activity.this)
                        .setIcon(R.mipmap.wenhao)
                        .setTitle(" ")
                        .setMessage(getString(R.string.sniff_dialog_ifo))
                        .setNegativeButton(getString(R.string.sniff_nagetive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status++;//重闻状态+1
                                if (random_fix_mode == 0) {
                                    random_retry_status = true;
                                }
                                answerInfo.put("retryCount", status + "");
                                bindingRea12.textAppear.setText("");
                                bindingRea12.btnPrepareToStart.setVisibility(View.VISIBLE);
                                bindingRea12.btnPrepareToStart.setText(getString(R.string.btn_sniff_retry));
                                mProgressStatus = 0;//进度条归零
                                bindingRea12.progressBar.setProgress(mProgressStatus);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.sniff_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bindingRea12.textAppear.setText("");//隐藏 准备 提示
                                bindingRea12.textPleaseWait.setVisibility(View.VISIBLE);
                                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                                mProgressStatus = 0;
                                status = 0;
                                bindingRea12.progressBar.setProgress(mProgressStatus);
                                bindingRea12.progressBar.setVisibility(View.INVISIBLE);
                                bindingRea12.testRemindText.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(Ready12Activity.this, Option12Activity.class);
                                intent.putExtra("send2", accept1);
                                intent.putExtra("number_count", number_count);
                                intent.putExtra("odorStartTime", (String) answerInfo.get("odorStartTime"));
                                intent.putExtra("odorEndTime", (String) answerInfo.get("odorEndTime"));
                                intent.putExtra("retryCount", (String) answerInfo.get("retryCount"));
                                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
                                startActivityForResult(intent, 0);
                                accept1++;
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
                msgTxt.setTextSize(16);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
            } else if (retry == 6 || retry == status) {
                answerInfo.put("odorEndTime", sdf.format(date));//到达上限次数记录结束时间 或 不设置重闻次数记录结束时间
                bindingRea12.textAppear.setText("");
                bindingRea12.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea12.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea12.btnPrepareToStart.setText(getString(R.string.prepare_to_start));
                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                bindingRea12.progressBar.setProgress(mProgressStatus);
                bindingRea12.progressBar.setVisibility(View.INVISIBLE);
                bindingRea12.testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready12Activity.this, Option12Activity.class);//打开答题界面
                intent.putExtra("send2", accept1);
                intent.putExtra("number_count", number_count);
                intent.putExtra("odorStartTime", (String) answerInfo.get("odorStartTime"));
                intent.putExtra("odorEndTime", (String) answerInfo.get("odorEndTime"));
                intent.putExtra("retryCount", (String) answerInfo.get("retryCount"));
                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
                status = 0;
                accept1++;//自增，取生成的随机数组indices里下一个索引（随机模式），或取固定数组status_40的下一个索引
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
        //resultCode为回传的标记，我在Option12Activity中回传的是RESULT_OK
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            number_count = b.getInt("send3");
            //number_count 代替 accept 记录题目序号 无论何种模式 题目计数独立 + 1
            if (number_count >= 1) {
                bindingRea12.textPleaseWait.setVisibility(View.INVISIBLE);
                bindingRea12.btnPrepareToStart.setText(getString(R.string.prepare_to_start_next));
                bindingRea12.btnPrepareToStart.setVisibility(View.VISIBLE);
                bindingRea12.progressBar.setVisibility(View.VISIBLE);
                bindingRea12.testRemindText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(Ready12Activity.this)
                .setIcon(R.mipmap.talk)
                .setTitle(getString(R.string.remind))
                .setMessage(getString(R.string.quit_remind))
                .setNegativeButton(getString(R.string.cencle1), (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton(getString(R.string.confirm1), (dialog1, which) -> {
                    Intent intent = new Intent(Ready12Activity.this, TabActivity.class);
                    startActivity(intent);
                    finish();
                    dialog1.dismiss();
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
    }

}
