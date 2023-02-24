package com.example.myapplication.mocktestid;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.example.myapplication.MainActivity;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityReadyBinding;
import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option2Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Ready2Activity extends BaseActivity {
    final int TIMER_MSG = 0x001;
    private int mProgressStatus = 0;
    int accept1 = 0;
    int number_count = 0;
    int status = 0;             //设置重闻的次数
    int TIME = 3;               //定义默认时间长度
    static int odor_release_delay = 2000;
    static int random_fix_mode = 1;
    static int random_retry_backup;
    static boolean random_retry_status = false;
    private ServiceConnection sc;
    public SocketService socketService;
    private ActivityReadyBinding bindingRea12;
    private final String[] status_40 =  {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "306", "307","308"};// 20 21 为外部气体通道 1-19和22、23为识别通道
    private final List<Integer> indices = new ArrayList<>(12);

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
        bindingRea12.loadingIcon.setVisibility(View.INVISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        odor_release_delay = retryCount.getInt("odor_release_delay", 1);
        TIME = retryCount.getInt("odor_release_time", 3);//默认3s
        bindingRea12.progressBar.setMax(TIME);
        int time_sum_order = TIME + odor_release_delay;//电磁阀打开总时间 = 提前打开时间 + 气味释放时间
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        if (random_fix_mode == 0) {
            for (int c = 0; c < 20; c++) {
                indices.add(c);
            }
        }

        Timer my_open_timer = new Timer();
        if (sc != null && socketService != null) {//开启识别测试延时
            socketService.sendOrder("427");
        }
        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {
                    socketService.sendOrder("427");
                }
            }
        }, 200);
        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {
                    socketService.sendOrder("427");
                }
            }
        }, 200);


        bindingRea12.btnPrepareToStart.setOnClickListener(v -> {
            bindingRea12.btnPrepareToStart.setClickable(false);//避免多次点击
            if (sc != null && socketService != null) {
                MyCountTimer myCountTimer = new MyCountTimer(bindingRea12.btnPrepareToStart, "");
                myCountTimer.start();
                new Thread(() -> {
                    try {
                        if (random_fix_mode == 1) {//固定模式
                            Thread.sleep(3000 - odor_release_delay*1000L);
                            socketService.sendOrder(status_40[accept1]);
                            Timer my_delay_timer = new Timer();
                            my_delay_timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    socketService.sendOrder(status_40[accept1]);
                                }
                            }, 200);
                            Thread.sleep(odor_release_delay*1000L); //总计休眠3秒 完成倒计时动画
                            handler.sendEmptyMessage(TIMER_MSG);//发送消息，启动进度条
                        }
                        if (random_fix_mode == 0) {//随机模式
                            Thread.sleep(3000 - odor_release_delay*1000L);
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
                            Thread.sleep(odor_release_delay*1000L);
                            handler.sendEmptyMessage(TIMER_MSG);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                Toast.makeText(Ready2Activity.this, "网络异常！", Toast.LENGTH_SHORT).show();
            }
            bindingRea12.btnPrepareToStart.setClickable(true);
        });
    }

    public static void getRetryData(Context context) {//通过上下文拿到MangementTabActivity定义的retryCount
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
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

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
            if (TIME - mProgressStatus > 0) {
                mProgressStatus++;
                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                bindingRea12.textAppear.setText(getString(R.string.sniff_ifo));
                bindingRea12.textAppear.setEnabled(false);
                bindingRea12.progressBar.setProgress(mProgressStatus);
                handler.sendEmptyMessageDelayed(TIMER_MSG, 1000);
            } else{
                bindingRea12.textAppear.setText("");
                bindingRea12.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea12.loadingIcon.setVisibility(View.VISIBLE);
                bindingRea12.btnPrepareToStart.setText(getString(R.string.prepare_to_start));
                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                bindingRea12.progressBar.setProgress(mProgressStatus);
                bindingRea12.progressBar.setVisibility(View.INVISIBLE);
                bindingRea12.testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready2Activity.this, Option2Activity.class);//打开答题界面
                intent.putExtra("send2", accept1);
                intent.putExtra("number_count", number_count);
                status = 0;
                accept1++;//自增，取生成的随机数组indices里下一个索引（随机模式），或取固定数组status_40的下一个索引
                startActivityForResult(intent, 0);//startActivityForResult的主要作用就是它可以回传数据
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //resultCode为回传的标记，我在Option12Activity中回传的是RESULT_OK
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            number_count = b.getInt("send3");
            if (number_count >= 1) {
                bindingRea12.textPleaseWait.setVisibility(View.INVISIBLE);
                bindingRea12.loadingIcon.setVisibility(View.INVISIBLE);
                bindingRea12.btnPrepareToStart.setText(getString(R.string.prepare_to_start_next));
                bindingRea12.btnPrepareToStart.setVisibility(View.VISIBLE);
                bindingRea12.progressBar.setVisibility(View.VISIBLE);
                bindingRea12.testRemindText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new DroidDialog.Builder(this)
                .cancelable(true, false)
                .icon(R.drawable.ic_baseline_warning_24)
                .title(getString(R.string.remind))
                .content(getString(R.string.quit_mock_test_remind))
                .cancelable(true, true)
                .positiveButton(getString(R.string.confirm1), dialog -> {
                    MyOpenHelper moh = new MyOpenHelper(Ready2Activity.this);
                    SQLiteDatabase sd = moh.getReadableDatabase();
                    String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                    Cursor cursor1 = sd.rawQuery(sql1, null);
                    cursor1.moveToFirst();
                    String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                    cursor1.close();
                    String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                    sd.execSQL(sql);
                    sd.close();
                    Intent intent = new Intent(Ready2Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .negativeButton(getString(R.string.cencle1), Dialog::dismiss)
                .color(ContextCompat.getColor(Ready2Activity.this, R.color.orange), ContextCompat.getColor(Ready2Activity.this, R.color.white),
                        ContextCompat.getColor(Ready2Activity.this, R.color.black))
                .animation(AnimUtils.AnimFadeInOut)
                .show();
    }
}


