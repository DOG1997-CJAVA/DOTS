package com.example.myapplication.idtest;

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

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityReadyBinding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option20Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Ready20Activity extends BaseActivity {
    final int TIMER_MSG = 0x001;
    private int mProgressStatus = 0;
    int accept1 = 0;
    int number_count = 0;
    int TIME = 3;
    int status;
    static int retry_20 = 1;
    static int random_fix_mode = 1;
    static int odor_release_delay = 2;
    private ServiceConnection sc;
    public SocketService socketService;
    static int random_retry_backup;
    static boolean random_retry_status = false;
    private ActivityReadyBinding bindingRea20;
    private final HashMap<String, Object> answerInfo = new HashMap<>();
    private final String[] status_20 =  {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "306"};// 20 21 为外部气体通道 1-19和34为识别通道
    private final List<Integer> indices = new ArrayList<>(20);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingRea20 = ActivityReadyBinding.inflate(getLayoutInflater());
        setContentView(bindingRea20.getRoot());
        bindSocketService();
        bindingRea20.progressBar.setVisibility(View.VISIBLE);
        bindingRea20.testRemindText.setVisibility(View.VISIBLE);
        bindingRea20.textPleaseWait.setVisibility(View.INVISIBLE);
        bindingRea20.loadingIcon.setVisibility(View.INVISIBLE);
        bindingRea20.btnPrepareToStart.setVisibility(View.VISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry_20 = retryCount.getInt("retry_time", 1);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        odor_release_delay = retryCount.getInt("odor_release_delay", 1);//更改为以s做单位
        TIME = retryCount.getInt("odor_release_time", 3);
        int time_sum_order = TIME + odor_release_delay;//电磁阀打开总时间 = 提前打开时间 + 气味释放时间
        String educate = getIntent().getStringExtra("educate");
        answerInfo.put("educate", educate);
        bindingRea20.progressBar.setMax(TIME);
        answerInfo.put("release_time",TIME);
        if (random_fix_mode == 0) {
            for (int c = 0; c < 20; c++) {
                indices.add(c);

            }
        }
        Timer my_open_timer = new Timer();

        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {//开启识别测试延时
                    socketService.sendOrder("427");
                }
            }
        }, 200);
        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {//开启识别测试延时
                    socketService.sendOrder("427");
                }
            }
        }, 200);
        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {//开启识别测试延时
                    socketService.sendOrder("427");
                }
            }
        }, 200);

        bindingRea20.btnPrepareToStart.setOnClickListener(v -> {
            bindingRea20.btnPrepareToStart.setClickable(false);//避免多次点击
            if (sc != null && socketService != null) {
                MyCountTimer myCountTimer = new MyCountTimer(bindingRea20.btnPrepareToStart, "");
                myCountTimer.start();
                new Thread(() -> {
                    try {
                        if (random_fix_mode == 1) {
                            Thread.sleep(3000 - odor_release_delay * 1000L);
                            socketService.sendOrder(status_20[accept1]);
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
                            answerInfo.put("odorStartTime", sdf.format(date));
                            Thread.sleep(odor_release_delay * 1000L);
                            handler.sendEmptyMessage(TIMER_MSG);
                        }
                        if (random_fix_mode == 0) {
                            Thread.sleep(3000 - odor_release_delay * 1000L);
                            if (!random_retry_status) {
                                accept1 = getRandomOdor();
                                random_retry_backup = accept1;
                            } else {
                                accept1 = random_retry_backup;
                                random_retry_status = false;
                            }
                            socketService.sendOrder(status_20[accept1]);
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
                            answerInfo.put("odorStartTime", sdf.format(date));
                            Thread.sleep(odor_release_delay * 1000L);
                            handler.sendEmptyMessage(TIMER_MSG);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                Toast.makeText(Ready20Activity.this, "网络异常！", Toast.LENGTH_SHORT).show();
            }
            bindingRea20.btnPrepareToStart.setClickable(true);//避免多次点击
        });
    }

    public static void getRetryData(Context context) {
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry_20 = retryCount.getInt("retry_time", 1);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        odor_release_delay = 3000 - retryCount.getInt("odor_release_delay", 2000);
    }

    private int getRandomOdor() {
        int arrIndex = (int) ((double) indices.size() * Math.random());
        int randomIndex = indices.get(arrIndex);
        indices.remove(arrIndex);
        return randomIndex;
    }

    private void bindSocketService() {
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
                bindingRea20.btnPrepareToStart.setVisibility(View.INVISIBLE);
                bindingRea20.textAppear.setText(getString(R.string.sniff_ifo));
                bindingRea20.textAppear.setEnabled(false);
                bindingRea20.progressBar.setProgress(mProgressStatus);
                handler.sendEmptyMessageDelayed(TIMER_MSG, 1000);
            } else {
                long firstTime = System.currentTimeMillis();
                answerInfo.put("firstTime",firstTime);
                answerInfo.put("odorEndTime", sdf.format(date));
                bindingRea20.textAppear.setText("");
                bindingRea20.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea20.loadingIcon.setVisibility(View.VISIBLE);
                bindingRea20.btnPrepareToStart.setText(getString(R.string.prepare_to_start));
                bindingRea20.btnPrepareToStart.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                bindingRea20.progressBar.setProgress(mProgressStatus);
                bindingRea20.progressBar.setVisibility(View.INVISIBLE);
                bindingRea20.testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready20Activity.this, Option20Activity.class);//打开答题界面
                intent.putExtra("send2", accept1);
                intent.putExtra("number_count", number_count);
                intent.putExtra("odorStartTime", (String) answerInfo.get("odorStartTime"));
                intent.putExtra("odorEndTime", (String) answerInfo.get("odorEndTime"));
                intent.putExtra("retryCount", (String) answerInfo.get("retryCount"));
                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
                intent.putExtra("firstTime", (Long) answerInfo.get("firstTime"));
                intent.putExtra("educate", (String) answerInfo.get("educate"));
                status = 0;
                accept1++;
                startActivityForResult(intent, 0);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            number_count = b.getInt("send3");
            if (number_count >= 1) {
                bindingRea20.textPleaseWait.setVisibility(View.INVISIBLE);
                bindingRea20.loadingIcon.setVisibility(View.INVISIBLE);
                bindingRea20.btnPrepareToStart.setText(getString(R.string.prepare_to_start_next));
                bindingRea20.btnPrepareToStart.setVisibility(View.VISIBLE);
                bindingRea20.progressBar.setVisibility(View.VISIBLE);
                bindingRea20.testRemindText.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onBackPressed() {
        new DroidDialog.Builder(this)
                .cancelable(true, false)
                .icon(R.drawable.ic_baseline_warning_24)
                .title(getString(R.string.remind))
                .content(getString(R.string.quit_remind))
                .cancelable(true, true)
                .positiveButton(getString(R.string.confirm1), dialog -> {
                    MyOpenHelper moh = new MyOpenHelper(Ready20Activity.this);
                    SQLiteDatabase sd = moh.getReadableDatabase();
                    String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                    Cursor cursor1 = sd.rawQuery(sql1, null);
                    cursor1.moveToFirst();
                    String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                    cursor1.close();
                    String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                    try {
                        sd.execSQL(sql);
                    } finally {
                        sd.close();
                    }
                    sd.close();
                    Intent intent = new Intent(Ready20Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .negativeButton(getString(R.string.cencle1), Dialog::dismiss)
                .animation(AnimUtils.AnimFadeInOut)
                .show();
    }
}
