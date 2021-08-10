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
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.databinding.ActivityReadyBinding;
import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.MyCountTimer;
import com.example.myapplication.utils.options.Option40Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class Ready40Activity extends BaseActivity {
    final int TIMER_MSG = 0x001;
    private int mProgressStatus = 0;
    int accept1 = 0;
    int number_count = 0;
    int status;
    int TIME = 6;
    static int retry_40 = 1;
    static int odor_release_delay = 2000;
    static int random_fix_mode = 1;
    private ServiceConnection sc;
    public SocketService socketService;
    static int random_retry_backup;
    static boolean random_retry_status = false;
    private HashMap answerInfo = new HashMap();
    private ActivityReadyBinding bindingRea40;
    private final String[] status_40 = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"};
    private List<Integer> indices = new ArrayList<Integer>(40);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingRea40 = ActivityReadyBinding.inflate(getLayoutInflater());
        setContentView(bindingRea40.getRoot());
        bindSocketService();
        ButterKnife.bind(this);
        bindingRea40.progressBar.setVisibility(View.VISIBLE);
        bindingRea40.testRemindText.setVisibility(View.VISIBLE);
        bindingRea40.textPleaseWait.setVisibility(View.INVISIBLE);
        bindingRea40.btnPrepareToStart.setVisibility(View.VISIBLE);
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry_40 = retryCount.getInt("retry_time", 1);
        odor_release_delay = 3000 - retryCount.getInt("odor_release_delay", 2000);
        random_fix_mode = retryCount.getInt("btn_random_mode", 1);
        TIME = retryCount.getInt("odor_release_time", 6);
        bindingRea40.progressBar.setMax(TIME);
        if (random_fix_mode == 0) {
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
        bindingRea40.btnPrepareToStart.setOnClickListener(v -> {
            if (sc != null && socketService != null) {
                MyCountTimer myCountTimer = new MyCountTimer(bindingRea40.btnPrepareToStart, "");
                myCountTimer.start();
                new Thread(() -> {
                    try {
                        if (random_fix_mode == 1) {
                            Thread.sleep(odor_release_delay);
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
                        if (random_fix_mode == 0) {
                            Thread.sleep(odor_release_delay);
                            if (!random_retry_status) {//随机模式下 若设置重闻次数 进入重闻 需记录测试题目
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
                Toast.makeText(Ready40Activity.this, "网络异常！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getRetryData(Context context) {
        SharedPreferences retryCount = context.getSharedPreferences("retryCount", MODE_PRIVATE);
        retry_40 = retryCount.getInt("retry_time", 1);
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
                Timer my_delay_timer = new Timer();
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("413");
                    }
                }, 100);
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
            TextView text_appear = (TextView) findViewById(R.id.text_appear);
            if (TIME - mProgressStatus > 0) {
                mProgressStatus++;
                if (mProgressStatus == 1) {
                    answerInfo.put("odorStartTime", sdf.format(date));
                }
                bindingRea40.btnPrepareToStart.setVisibility(View.INVISIBLE);
                text_appear.setText(getString(R.string.sniff_ifo));
                text_appear.setEnabled(false);
                bindingRea40.progressBar.setProgress(mProgressStatus);
                handler.sendEmptyMessageDelayed(TIMER_MSG, 500);
            } else if (retry_40 != 6 && status < retry_40) {
                answerInfo.put("odorEndTime", sdf.format(date));
                answerInfo.put("retryCount", status + "");
                AlertDialog dialog = new AlertDialog.Builder(Ready40Activity.this)
                        .setIcon(R.mipmap.wenhao)
                        .setTitle(" ")
                        .setMessage(getString(R.string.sniff_dialog_ifo))
                        .setNegativeButton(getString(R.string.sniff_nagetive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status++;
                                if (random_fix_mode == 0) {
                                    random_retry_status = true;
                                }
                                answerInfo.put("retryCount", status + "");
                                text_appear.setText("");
                                bindingRea40.btnPrepareToStart.setVisibility(View.VISIBLE);
                                bindingRea40.btnPrepareToStart.setText(getString(R.string.btn_sniff_retry));
                                mProgressStatus = 0;
                                bindingRea40.progressBar.setProgress(mProgressStatus);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.sniff_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                text_appear.setText("");
                                bindingRea40.textPleaseWait.setVisibility(View.VISIBLE);
                                bindingRea40.btnPrepareToStart.setVisibility(View.INVISIBLE);
                                mProgressStatus = 0;
                                status = 0;
                                bindingRea40.progressBar.setProgress(mProgressStatus);
                                bindingRea40.progressBar.setVisibility(View.INVISIBLE);
                                bindingRea40.testRemindText.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(Ready40Activity.this, Option40Activity.class);
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
                dialog.setCanceledOnTouchOutside(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GREEN);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);
            } else if (retry_40 == 6 || retry_40 == status) {
                answerInfo.put("odorEndTime", sdf.format(date));
                text_appear.setText("");
                bindingRea40.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea40.textPleaseWait.setVisibility(View.VISIBLE);
                bindingRea40.btnPrepareToStart.setText(getString(R.string.prepare_to_start));
                bindingRea40.btnPrepareToStart.setVisibility(View.INVISIBLE);
                mProgressStatus = 0;
                bindingRea40.progressBar.setProgress(mProgressStatus);
                bindingRea40.progressBar.setVisibility(View.INVISIBLE);
                bindingRea40.testRemindText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Ready40Activity.this, Option40Activity.class);//打开答题界面
                intent.putExtra("send2", accept1);
                intent.putExtra("number_count", number_count);
                intent.putExtra("odorStartTime", (String) answerInfo.get("odorStartTime"));
                intent.putExtra("odorEndTime", (String) answerInfo.get("odorEndTime"));
                intent.putExtra("retryCount", (String) answerInfo.get("retryCount"));
                intent.putExtra("test_channel", (String) answerInfo.get("test_channel"));
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
                bindingRea40.textPleaseWait.setVisibility(View.INVISIBLE);
                bindingRea40.btnPrepareToStart.setText(getString(R.string.prepare_to_start_next));
                bindingRea40.btnPrepareToStart.setVisibility(View.VISIBLE);
                bindingRea40.progressBar.setVisibility(View.VISIBLE);
                bindingRea40.testRemindText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(Ready40Activity.this)
                .setIcon(R.mipmap.talk)
                .setTitle(getString(R.string.remind))
                .setMessage(getString(R.string.quit_remind))
                .setNegativeButton(getString(R.string.cencle1), (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton(getString(R.string.confirm1), (dialog1, which) -> {
                    Intent intent = new Intent(Ready40Activity.this, TabActivity.class);
                    startActivity(intent);
                    finish();
                    dialog1.dismiss();
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
    }
}
