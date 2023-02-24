package com.example.myapplication.idtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.example.myapplication.utils.options.OptionCustomActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReadyCustomActivity extends BaseActivity {

    private static final String TAG = "IDCustomTest";
    final int TIMER_MSG = 0x001;
    private int mProgressStatus = 0;
    private final HashMap<String, Object> answerInfo = new HashMap<>();
    int accept1 = 0;
    int number_count = 0;
    int status = 0;
    int TIME = 3;
    static int odor_release_delay = 1;
    static int random_fix_mode = 1;
    static int random_retry_backup;
    static boolean random_retry_status = false;
    private ServiceConnection sc;
    public SocketService socketService;
    private ActivityReadyBinding bindingRea12;
    private final String[] status_20 = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "306"};
    private final List<Integer> indices = new ArrayList<>();
    private final List<Integer> id_odor_index = new ArrayList<>(20);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingRea12 = ActivityReadyBinding.inflate(getLayoutInflater());
        setContentView(bindingRea12.getRoot());
        bindSocketService();
        bindingRea12.progressBar.setVisibility(View.VISIBLE);
        bindingRea12.testRemindText.setVisibility(View.VISIBLE);
        bindingRea12.textPleaseWait.setVisibility(View.INVISIBLE);
        bindingRea12.loadingIcon.setVisibility(View.INVISIBLE);
        bindingRea12.btnPrepareToStart.setVisibility(View.VISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        odor_release_delay = retryCount.getInt("odor_release_delay", 1);
        TIME = retryCount.getInt("odor_release_time", 3);
        int time_sum_order = TIME  + odor_release_delay;
        bindingRea12.progressBar.setMax(TIME);
        String educate = getIntent().getStringExtra("educate");
        answerInfo.put("educate", educate);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        answerInfo.put("release_time", TIME);
        int id_odor_count = retryCount.getInt("id_odor_count", 4);
        for (int i = 0; i < id_odor_count; i++) {
            id_odor_index.add(i, retryCount.getInt(i + "id_test_odor", i));
            Log.w(TAG, id_odor_index +"取出所选识别测试嗅素的指令位置");
        }
        String[] id_selete_odor_code = new String[id_odor_count];
        for (int i = 0; i < id_odor_count; i++) {
            id_selete_odor_code[i] = status_20[id_odor_index.get(i)];
            Log.w(TAG, id_selete_odor_code[i] +"取出所选记忆测试嗅素所需的具体的控制指令");
        }
        if (random_fix_mode == 0) {
            for (int c = 0; c < id_odor_count; c++) {
                indices.add(c);
            }
        }
        Timer my_open_timer = new Timer();
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


        my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {
                    socketService.sendOrder("427");
                }
            }
        }, 200);


        bindingRea12.btnPrepareToStart.setOnClickListener(v -> {
            bindingRea12.btnPrepareToStart.setClickable(false);
            if (sc != null && socketService != null) {
                MyCountTimer myCountTimer = new MyCountTimer(bindingRea12.btnPrepareToStart, "");
                myCountTimer.start();
                new Thread(() -> {
                    try {
                        if (random_fix_mode == 1) {
                            Thread.sleep(3000 - odor_release_delay * 1000L);
                            socketService.sendOrder(id_selete_odor_code[accept1]);
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
                            socketService.sendOrder(id_selete_odor_code[accept1]);
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
                            answerInfo.put("odorStartTime", sdf.format(date));
                            Thread.sleep(odor_release_delay* 1000L);
                            handler.sendEmptyMessage(TIMER_MSG);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                Toast.makeText(ReadyCustomActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
            }
            bindingRea12.btnPrepareToStart.setClickable(true);
        });
    }

    public static void getRetryData(Context context) {
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
            } else {
                long firstTime = System.currentTimeMillis();
                answerInfo.put("firstTime", firstTime);
                answerInfo.put("odorEndTime", sdf.format(date));//不再在准备界面设置重闻对话框
                bindingRea12.textAppear.setText("");
                bindingRea12.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea12.loadingIcon.setVisibility(View.VISIBLE);
                bindingRea12.btnPrepareToStart.setText(getString(R.string.prepare_to_start));
                bindingRea12.btnPrepareToStart.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                bindingRea12.progressBar.setProgress(mProgressStatus);
                bindingRea12.progressBar.setVisibility(View.INVISIBLE);
                bindingRea12.testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(ReadyCustomActivity.this, OptionCustomActivity.class);
                intent.putExtra("send2", id_odor_index.get(accept1));
                intent.putExtra("number_count", number_count);
                intent.putExtra("odorStartTime", (String) answerInfo.get("odorStartTime"));
                intent.putExtra("odorEndTime", (String) answerInfo.get("odorEndTime"));
                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
                intent.putExtra("release_time", (Integer) answerInfo.get("release_time"));
                intent.putExtra("firstTime", (Long) answerInfo.get("firstTime"));
                intent.putExtra("educate", (String) answerInfo.get("educate"));
                status = 0;
                accept1++;
                startActivityForResult(intent, 0);//startActivityForResult的主要作用就是它可以回传数据
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
                .content(getString(R.string.quit_remind))
                .cancelable(true, true)
                .positiveButton(getString(R.string.confirm1), dialog -> {
                    MyOpenHelper moh = new MyOpenHelper(ReadyCustomActivity.this);
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
                    Intent intent = new Intent(ReadyCustomActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .negativeButton(getString(R.string.cencle1), Dialog::dismiss)
                .animation(AnimUtils.AnimFadeInOut)
                .color(ContextCompat.getColor(ReadyCustomActivity.this, R.color.orange), ContextCompat.getColor(ReadyCustomActivity.this, R.color.white),
                        ContextCompat.getColor(ReadyCustomActivity.this, R.color.black))
                .show();
    }
}