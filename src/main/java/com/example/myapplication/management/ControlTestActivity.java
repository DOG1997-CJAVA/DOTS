package com.example.myapplication.management;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityControltestBinding;
import com.example.myapplication.service.SocketService;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class ControlTestActivity extends BaseActivity implements View.OnClickListener {
    private final String[] status = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216",
            "301", "302", "303", "304", "305", "306", "307", "308"};
    private static final int CONNECTED_RESPONSE = 0;
    private static final int RESPONSE_TIMEOUT = 1;
    private static final int SEND_RESPONSE = 2;
    private static final int RECEIVER_RESPONSE = 3;
    private ActivityControltestBinding bindingcontrol;
    Socket socket = null;

    private ServiceConnection sc;
    private int channel = 1;
    public SocketService socketService;
    public List<ToggleButton> bts = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingcontrol = ActivityControltestBinding.inflate(getLayoutInflater());
        setContentView(bindingcontrol.getRoot());
        bindSocketService();
        ButterKnife.bind(this);
        Resources res = getResources();
        bindingcontrol.chronometer.setFormat("??????????????????:s%");
        for (int i = 1; i < 41; i++) {//??????????????????40???????????? ??????????????????????????????
            int id = res.getIdentifier("btn" + i, "id", getPackageName());
            ToggleButton btn = findViewById(id);
            int finalI = i;
            btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {//???
                    btnSetState(false);
                    btn.setClickable(true);
                    btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bt_shape,null));
                    socketService.sendOrder(status[finalI - 1]);
                    bindingcontrol.chronometer.setBase(SystemClock.elapsedRealtime());//???????????????????????????????????????????????????0??????
                    bindingcontrol.chronometer.start();
                } else {//???
                    bindingcontrol.chronometer.stop();//????????????????????????????????????
                    btnSetState(true);
                    btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                    socketService.sendOrder("410");//???????????????????????? ??????????????????
                }
            });
            bts.add(btn);
        }

        bindingcontrol.pumb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.pumb1.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("403");
            } else {
                bindingcontrol.pumb1.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("401");
            }
        });

        //?????????2 ???????????????????????? ???????????????????????????????????????
        bindingcontrol.externalAirSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences retryCount1 = getSharedPreferences("retryCount", MODE_PRIVATE);
            SharedPreferences.Editor edit = retryCount1.edit();
            if (isChecked) {
                bindingcontrol.externalAirSwitch.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("402");
                edit.putInt("external_status",1);
                toastMsg("open external air index == 1");
            } else {
                bindingcontrol.externalAirSwitch.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("404");
                edit.putInt("external_status",0);
                toastMsg("close external air index == 0");
            }
            edit.apply();
        });

        //??????/?????????????????? ???????????? ????????????????????????????????????
        bindingcontrol.btnValueConst.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnValueConst.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("405");
            } else {
                bindingcontrol.btnValueConst.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("406");
            }
        });

        bindingcontrol.btnConnect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnConnect.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
            } else {
                bindingcontrol.btnConnect.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            }
        });

        bindingcontrol.btnFan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnFan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("407");
            } else {
                bindingcontrol.btnFan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("408");
            }
        });

        bindingcontrol.btnClean.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnClean.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("411");//???????????? ????????????
            } else {
                bindingcontrol.btnClean.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("412");//???????????? ????????????
            }
        });

        bindingcontrol.btnAutoScan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                cdt.start();
                toastMsg("40?????? ???????????????????????????");
            } else {
                //?????????????????? ??????????????????
                bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                cdt.cancel();
                socketService.sendOrder("410");//???????????? ???????????????????????????
                channel = 1;
                btnSetState(true);//?????????????????????????????????????????????????????????
                for (int i = 0; i < 40; i++) {
                    bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                }
                toastMsg("???????????????????????????????????????");
            }
        });

        bindingcontrol.btn300Cycle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btn300Cycle.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                cdt_300_cycle.start();
                toastMsg("?????????300??????????????????");
            } else {
                //?????????????????? ??????????????????

                bindingcontrol.btn300Cycle.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                cdt_300_cycle.cancel();
                socketService.sendOrder("410");//???????????? ???????????????????????????
                channel = 1;
                btnSetState(true);//?????????????????????????????????????????????????????????
                for (int i = 0; i < 40; i++) {
                    bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                }
                toastMsg("?????????????????????????????????");
            }
        });

        bindingcontrol.btnExternalAir20.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnExternalAir20.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("420");//????????????????????????20
            } else {
                bindingcontrol.btnExternalAir20.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("421");//??????20
            }
        });

        bindingcontrol.btnExternalAir21.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bindingcontrol.btnExternalAir21.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
                socketService.sendOrder("422");//????????????????????????21
            } else {
                bindingcontrol.btnExternalAir21.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
                socketService.sendOrder("422");//??????21
            }
        });
    }

    private CountDownTimer cdt = new CountDownTimer(20000, 500) {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTick(long millisUntilFinished) {
            btnSetState(false);
            ToggleButton my_btn = bts.get(channel - 1);//btn1??????
            my_btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
            socketService.sendOrder(status[channel - 1]);//??????0?????? ??????????????????
            System.out.println(channel - 1);
            channel++;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onFinish() {
            socketService.sendOrder("410");
            btnSetState(true);//???????????????????????????????????????????????????????????????
            for (int i = 0; i < 40; i++) {//????????????????????????40?????????
                bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            }
            bindingcontrol.btnAutoScan.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            bindingcontrol.btnAutoScan.setChecked(false);
            channel = 1;
        }
    };

    private CountDownTimer cdt_300_cycle = new CountDownTimer(5000*40*300, 5000) {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTick(long millisUntilFinished) {
            btnSetState(false);
            ToggleButton my_btn = bts.get(channel - 1);//btn1??????
            my_btn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape,null));
            socketService.sendOrder(status[channel - 1]);//??????0?????? ??????????????????
            System.out.println(channel - 1);
            if (channel != 40){
                channel++;
            }else {
                btnSetState(true);
                channel = 1;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onFinish() {
            socketService.sendOrder("410");
            btnSetState(true);//???????????????????????????????????????????????????????????????
            for (int i = 0; i < 40; i++) {//????????????????????????40?????????
                bts.get(i).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            }
            bindingcontrol.btn300Cycle.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bt_shape1,null));
            bindingcontrol.btn300Cycle.setChecked(false);
            channel = 1;
        }
    };

    private void btnSetState(boolean state) {
        for (int j = 0; j < 40; j++) {
            ToggleButton my_btn = bts.get(j);
            my_btn.setClickable(state);
        }
    }

    private void bindSocketService() {
        /*??????binder??????service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
                Timer my_delay_timer = new Timer();
                socketService.sendOrder("414");
                my_delay_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("414");
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

    /*??????Toast???????????????????????????  ???????????????????????????????????????toast*/
    private void toastMsg(final String msg) {
        handler.post(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
            socketService.sendOrder("410");
        }
        if (cdt_300_cycle != null) {
            cdt_300_cycle.cancel();
            cdt_300_cycle = null;
            socketService.sendOrder("410");
        }
        super.onDestroy();                  //?????????????????? ???????????????
        Timer my_delay_timer = new Timer();
        socketService.sendOrder("413");  //??????????????????????????? ??????????????????????????? ??????????????????????????? ????????????????????????
        my_delay_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                socketService.sendOrder("413");
            }
        }, 200);
        SharedPreferences retryCount1 = getSharedPreferences("retryCount", MODE_PRIVATE);
        int external_status = retryCount1.getInt("external_status",0);
        if(external_status == 0){
            socketService.sendOrder("404");
            my_delay_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    socketService.sendOrder("404");
                }
            }, 200);
        }else if(external_status == 1){
            socketService.sendOrder("402");
            my_delay_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    socketService.sendOrder("402");
                }
            }, 200);
        }
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        toastMsg("control_test_quit");
    }

    //??????????????? Looper.myLooper() ??????????????????
    private final Handler handler = new Handler(Looper.myLooper()) {
        // ???????????????UI???????????????????????????????????? ???????????????????????????
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED_RESPONSE:
                    bindingcontrol.btnConnect.setTextColor(Color.parseColor("#216F02"));
                    break;
                case RESPONSE_TIMEOUT:
                    Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                case RECEIVER_RESPONSE:
                case SEND_RESPONSE:
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            toastMsg("101");
        }
    }

    private class ConnectThread extends Thread {
        private final String ip;
        private final int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                PrintStream out = new PrintStream(socket.getOutputStream());
                runOnUiThread(() -> {
                    bindingcontrol.btnConnect.setText("??????");
                    Toast.makeText(ControlTestActivity.this, "????????????", Toast.LENGTH_LONG).show();
                });
                //new HeartBeatThread().start();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ControlTestActivity.this, "????????????", Toast.LENGTH_LONG).show());
            }
        }
    }

}
