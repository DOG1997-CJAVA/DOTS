package com.example.myapplication.management;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogMultiChoiceExtKt;
import com.afollestad.materialdialogs.list.DialogSingleChoiceExtKt;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.ConnectActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.idtest.Ready12Activity;
import com.example.myapplication.idtest.Ready20Activity;
import com.example.myapplication.mocktestid.Ready2Activity;
import com.example.myapplication.idtest.Ready40Activity;
import com.example.myapplication.ResultManagementActivity;
import com.example.myapplication.databinding.ActivityManagementtabBinding;
import com.example.myapplication.language.FunApplication;
import com.example.myapplication.service.SocketService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.myapplication.language.LocaleManager.LANGUAGE_CHINESE;
import static com.example.myapplication.language.LocaleManager.LANGUAGE_ENGLISH;

public class ManagementTabActivity extends BaseActivity {
    private int oddr_delay_temp = 0, odor_release_temp = 0;
    private ServiceConnection sc;
    public SocketService socketService;
    private ActivityManagementtabBinding bindingmantab;
    private List<Integer> id_odor_index = new ArrayList<>(20);
    private List<String> id_odor_use_show = new ArrayList<>(20);
    public static final String TAG = "RightFragment";
    private final String[] odor_name_20_EN = {"Orange", "Jasmine", "Chocolate", "Lemon", "Banana", "Milk", "Peach", "Coffee", "Mint",
            "Soap", "Garlic", "Menthol", "Cut Grass", "Baby powder", "Coconut", "Lilac", "Leather", "Pineapple", "Vanilla",
            "Smoke",};
    private final String[] odor_name_20_CN = {"橙子", "茉莉花", "巧克力", "柠檬", "香蕉", "牛奶", "桃子", "咖啡",
            "薄荷", "肥皂", "大蒜", "薄荷脑", "绿草地", "婴儿爽身粉", "椰子", "紫丁香", "皮革", "菠萝",
            "香草", "烟"};
    private String[] odor_name_20 = {};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindSocketService();
        SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
        bindingmantab = ActivityManagementtabBinding.inflate(getLayoutInflater());
        setContentView(bindingmantab.getRoot());
        int language_index = retryCount.getInt("language_set", 1);
        if (language_index == 1) {
            odor_name_20 = odor_name_20_EN;
        } else {
            odor_name_20 = odor_name_20_CN;
        }
        if (retryCount.getInt("btn_random_mode", 0) == 0) {
            bindingmantab.modeToSet.setText(getString(R.string.random_pattern));
        } else {
            bindingmantab.modeToSet.setText(getString(R.string.fix_pattern));
        }
        if (retryCount.getInt("retry_time", 1) <= 5) {
            bindingmantab.retryNumberShow.setText(retryCount.getInt("retry_time", 5) + "");
        } else {
            bindingmantab.retryNumberShow.setText(getString(R.string.no_retry_times));
        }
        int id_odor_count = retryCount.getInt("id_odor_count", 20);
        for (int i = 0; i < id_odor_count; i++) {
            id_odor_index.add(i, retryCount.getInt(i + "id_test_odor", i));
            Log.e(TAG, id_odor_index +"show");
            id_odor_use_show.add(i, odor_name_20[id_odor_index.get(i)]);
        }

        bindingmantab.textIdOdorShow.setText(id_odor_use_show.toString());
        bindingmantab.releaseTimeNumber.setText(retryCount.getInt("odor_release_time", 3)+ "s");
        bindingmantab.delayTimeRemind.setText(retryCount.getInt("odor_release_delay", 1) + "s");
        Ready2Activity.getRetryData(this);
        Ready12Activity.getRetryData(this);
        Ready20Activity.getRetryData(this);
        Ready40Activity.getRetryData(this);
        bindingmantab.btn00.setOnClickListener(v -> {
            Intent intent = new Intent(ManagementTabActivity.this, ConnectActivity.class);
            startActivity(intent);
        });

        bindingmantab.btnManageActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ManagementTabActivity.this, ControlTestActivity.class);
            startActivity(intent);
        });

        bindingmantab.btnTestResultManage.setOnClickListener(v -> {
            Intent intent = new Intent(ManagementTabActivity.this, ResultManagementActivity.class);
            startActivity(intent);
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

        bindingmantab.btnCouIdOdorSelete.setOnClickListener(v -> {
            int[] initialSelection = new int[id_odor_count];
            for (int i = 0; i < id_odor_count; i++) {
                initialSelection[i] = id_odor_index.get(i);
            }
            AtomicInteger indices_length = new AtomicInteger(4);
            MaterialDialog dialog = new MaterialDialog(this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.id_test_odor_cus, null);
            DialogMultiChoiceExtKt.listItemsMultiChoice(dialog, R.array.id_custom_odor, null, null, initialSelection,
                    true, false, (materialDialog, indices, text) -> {
                        Toast.makeText(this, "Selected item  " + text + " at indices " + indices.length, Toast.LENGTH_SHORT).show();
                        SharedPreferences retryCount14 = getSharedPreferences("retryCount", MODE_PRIVATE);
                        SharedPreferences.Editor edit = retryCount14.edit();
                        id_odor_use_show.clear();
                        for (int i = 0; i < indices.length; i++) {
                            edit.putInt(i + "id_test_odor", indices[i]);
                            id_odor_use_show.add(i, odor_name_20[indices[i]]);
                        }
                        Log.e(TAG, id_odor_use_show +"3333");
                        bindingmantab.textIdOdorShow.setText(id_odor_use_show.toString());
                        edit.putInt("id_odor_count", indices.length);
                        edit.apply();
                        indices_length.set(indices.length);
                        return null;
                    });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                Toast.makeText(this, getText(R.string.confirm1), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });//识别测试嗅素选择

        bindingmantab.btnRetrySet.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.retry1, null);
            DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.retry_times, null, null, 0,
                    true, (materialDialog, index, text) -> {
                        Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + text, Toast.LENGTH_SHORT).show();
                        SharedPreferences retryCount1 = getSharedPreferences("retryCount", MODE_PRIVATE);
                        SharedPreferences.Editor edit = retryCount1.edit();
                        if(index <= 4) {
                            edit.putInt("retry_time", index + 1);
                        }else{
                            edit.putInt("retry_time", 0);
                        }
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
            dialog.setCancelable(false);
            dialog.show();
        });

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
                if (oddr_delay_temp > 3) {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.management_advance_timing_max_remind), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + oddr_delay_temp + "s", Toast.LENGTH_SHORT).show();
                    SharedPreferences retryCount13 = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount13.edit();
                    edit.putInt("odor_release_delay", oddr_delay_temp);
                    bindingmantab.delayTimeRemind.setText(oddr_delay_temp + "s");
                    float time_sum = ((oddr_delay_temp * 1000.0f) + (retryCount13.getInt("odor_release_time", 3)) * 1000.0f);//打开总时长
                    int time_sum_order = (int) time_sum;
                    Timer my_delay_timer = new Timer();
                    socketService.sendOrder(time_sum_order + "415");
                    my_delay_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            socketService.sendOrder(time_sum_order + "415");
                            Log.d(TAG, (time_sum_order + "415").length() + "");
                            Log.d(TAG, time_sum_order + "415" + "");
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
                if (odor_release_temp > 10) {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.management_release_timing_max_remind), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagementTabActivity.this, getString(R.string.confirm_setting) + odor_release_temp + "s", Toast.LENGTH_SHORT).show();
                    SharedPreferences retryCount13 = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount13.edit();
                    edit.putInt("odor_release_time", odor_release_temp);
                    bindingmantab.releaseTimeNumber.setText(odor_release_temp + "s");
                    float time_sum = ((odor_release_temp * 1000.0f) + (retryCount13.getInt("odor_release_delay", 1)) * 1000.0f);//打开总时长
                    int time_sum_order = (int) time_sum;
                    Timer my_delay_timer = new Timer();
                    socketService.sendOrder(time_sum_order + "415");
                    my_delay_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.d(TAG, (time_sum_order + "415").length() + "");
                            Log.d(TAG, time_sum_order + "415" + "");
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
        if (language.equals(LANGUAGE_CHINESE)) {
            index = 0;
        } else {
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
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }
}