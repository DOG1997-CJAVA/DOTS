package com.example.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogSingleChoiceExtKt;
import com.example.myapplication.databinding.ActivityManagementtabBinding;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.FunApplication;
import com.example.myapplication.service.SocketService;
import com.example.myapplication.utils.options.Option12Activity;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.myapplication.language.LocaleManager.LANGUAGE_CHINESE;
import static com.example.myapplication.language.LocaleManager.LANGUAGE_ENGLISH;

public class ManagementTabActivity extends BaseActivity {
    private int oddr_delay_temp = 0, odor_release_temp = 0;
    private ServiceConnection sc;
    public SocketService socketService;
    private ActivityManagementtabBinding bindingmantab;
    public static final String TAG = "RightFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindSocketService();
        SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
        bindingmantab = ActivityManagementtabBinding.inflate(getLayoutInflater());
        setContentView(bindingmantab.getRoot());
/*        MyOpenHelper dbHelper = new MyOpenHelper(ManagementTabActivity.this);
        //调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();*/
        if (retryCount.getInt("btn_random_mode", 1) == 0) {
            bindingmantab.modeToSet.setText(getString(R.string.random_pattern));
        } else {
            bindingmantab.modeToSet.setText(getString(R.string.fix_pattern));
        }
        if (retryCount.getInt("retry_time", 1) <= 4) {
            bindingmantab.retryNumberShow.setText(retryCount.getInt("retry_time", 1) + "");
        } else {
            bindingmantab.retryNumberShow.setText(getString(R.string.no_retry_times));
        }
        bindingmantab.releaseTimeNumber.setText(retryCount.getInt("odor_release_time", 3) * 0.5 + "s");
        bindingmantab.delayTimeRemind.setText(retryCount.getInt("odor_release_delay", 3000) + "ms");
        Ready2Activity.getRetryData(this);
        Ready12Activity.getRetryData(this);
        Ready20Activity.getRetryData(this);
        Ready40Activity.getRetryData(this);
        bindingmantab.btn00.setOnClickListener(v -> {
            Intent intent0 = new Intent(ManagementTabActivity.this, ConnectActivity.class);
            startActivity(intent0);
        });
        bindingmantab.btn01.setOnClickListener(v -> {
            Intent intent1 = new Intent(ManagementTabActivity.this, ControlTestActivity.class);
            startActivity(intent1);
        });
        bindingmantab.btn02.setOnClickListener(v -> {
            Intent intent2 = new Intent(ManagementTabActivity.this, ClickTheLvItemContentActivity.class);
            startActivity(intent2);
        });

        bindingmantab.randomFixMode.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.test_mode, null);
            DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.random_fix_mode, null, null, 0,
                    true, (materialDialog, index, text) -> {
                        Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + text, Toast.LENGTH_SHORT).show();
                        SharedPreferences retryCount14 = getSharedPreferences("retryCount", MODE_PRIVATE);
                        SharedPreferences.Editor edit = retryCount14.edit();
                        edit.putInt("btn_random_mode", index);//0:随机模式 1：固定模式
                        edit.apply();
                        if (index == 0) {
                            bindingmantab.modeToSet.setText(getString(R.string.random_pattern));
                        } else {
                            bindingmantab.modeToSet.setText(getString(R.string.fix_pattern));
                        }
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                dialog.dismiss();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(ManagementTabActivity.this, getString(R.string.cancle_setting), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });

        bindingmantab.btnLanguageSelect.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.lang_select, null);
            DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.language, null, null, 0,
                    true, (materialDialog, index, text) -> {
                        // Toast.makeText(ManagementTabActivity.this, text, Toast.LENGTH_SHORT).show();
                        if (index == 0) {
                            setNewLocale(LANGUAGE_CHINESE, true);
                        } else {
                            setNewLocale(LANGUAGE_ENGLISH, true);
                        }
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                dialog.dismiss();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(ManagementTabActivity.this, getString(R.string.cancle_setting), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();

        });

        //需要同步发送指令，修改单片机程序 避免app修改延时或打开时间后，下位机依旧打开固定时间，关闭时间出错
        //单片机 打开总时间 = 接收到指令，提前打开时间 + 固定嗅闻时间
        bindingmantab.odorReleaseDelay.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.remind4, null);
            dialog.message(R.string.release_delay_set, null, null);
            DialogInputExtKt.input(dialog, getString(R.string.management_advance_timing_input_remind), null, null, null,
                    InputType.TYPE_CLASS_NUMBER,
                    null, true, false, (materialDialog, text) -> {
                        Toast.makeText(ManagementTabActivity.this, "Input  " + text, Toast.LENGTH_SHORT).show();
                        oddr_delay_temp = Integer.parseInt(text.toString());
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                if (oddr_delay_temp > 3000) {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.management_advance_timing_max_remind), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + oddr_delay_temp + "ms", Toast.LENGTH_SHORT).show();
                    SharedPreferences retryCount13 = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount13.edit();
                    edit.putInt("odor_release_delay", oddr_delay_temp);
                    bindingmantab.delayTimeRemind.setText(oddr_delay_temp + "ms");
                    float time_sum = (( oddr_delay_temp / 1000.0f) + (retryCount13.getInt("odor_release_time", 6)) / 2.0f) * 1000;
                    int time_sum_order = (int) time_sum;
                    Timer my_delay_timer = new Timer();
                    socketService.sendOrder(time_sum_order + "415");
                    my_delay_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            socketService.sendOrder(time_sum_order + "415");
                            Log.d(TAG, (time_sum_order + "415").length() + "");
                        }
                    }, 100);
                    edit.apply();
                }
                dialog.dismiss();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(ManagementTabActivity.this, getString(R.string.cancle_setting), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });

        bindingmantab.releaseTime.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.remind4, null);
            dialog.message(R.string.release_time_set, null, null);
            DialogInputExtKt.input(dialog, getString(R.string.management_release_timing_ifo), null, null, null,
                    InputType.TYPE_CLASS_NUMBER,
                    null, true, false, (materialDialog, text) -> {
                        Toast.makeText(ManagementTabActivity.this, "Input  " + text, Toast.LENGTH_SHORT).show();
                        odor_release_temp = Integer.parseInt(text.toString());
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                SharedPreferences retryCount12 = getSharedPreferences("retryCount", MODE_PRIVATE);
                SharedPreferences.Editor edit = retryCount12.edit();
                if (odor_release_temp > 10) {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.management_release_timing_max_remind), Toast.LENGTH_SHORT).show();
                } else {
                    edit.putInt("odor_release_time", odor_release_temp);
                    edit.apply();
                    float odor_release_time_float = odor_release_temp * 0.5f;
                    bindingmantab.releaseTimeNumber.setText(odor_release_time_float + "s");
                    float time_sum = (odor_release_temp * 0.5f + ((retryCount12.getInt("odor_release_delay", 6)) / 1000.0f)) * 1000;
                    int time_sum_order = (int) time_sum;
                    Timer my_delay_timer = new Timer();
                    socketService.sendOrder(time_sum_order + "415");
                    my_delay_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            socketService.sendOrder(time_sum_order + "415");//指令在循环里被忽略了 非常见指令
                            Log.d(TAG, (time_sum_order + "415").length() + "");
                        }
                    }, 100);
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + odor_release_time_float + "s", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(ManagementTabActivity.this, getString(R.string.cancle_setting), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });

        bindingmantab.btnRetrySet.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.retry1, null);
            DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.retry_times, null, null, 0,
                    true, (materialDialog, index, text) -> {
                        Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + text, Toast.LENGTH_SHORT).show();
                        SharedPreferences retryCount1 = getSharedPreferences("retryCount", MODE_PRIVATE);
                        SharedPreferences.Editor edit = retryCount1.edit();
                        edit.putInt("retry_time", index + 1);
                        edit.apply();
                        if (index <= 4) {
                            bindingmantab.retryNumberShow.setText(index + 1 + "");
                        } else {
                            bindingmantab.retryNumberShow.setText(getString(R.string.no_retry_times));
                        }
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                dialog.dismiss();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(ManagementTabActivity.this, getString(R.string.cancle_setting), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });
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

    private void setNewLocale(String language, boolean restartProcess) {
        FunApplication.localeManager.setNewLocale(this, language);

        int index;
        if(language.equals(LANGUAGE_CHINESE)){
            index = 0;
        }else {
            index = 1;
        }
        SharedPreferences language_set = getSharedPreferences("retryCount", MODE_PRIVATE);
        SharedPreferences.Editor edit = language_set.edit();
        edit.putInt("language_set", index);
        edit.commit();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
        //Toast.makeText(ManagementTabActivity.this, "已退出管理界面", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }
}