package com.example.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.common.EventMsg;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.management.ManagementTabActivity;
import com.example.myapplication.service.SocketService;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends BaseActivity {
    private ServiceConnection sc;
    private static boolean reload = false;
    private long firstTime = 0;
    public SocketService socketService;
    public ImageView icon_con_success, icon_con_error;
    public static final String TAG = "RightFragment";
    int TIME = 3;               //定义默认时间长度
    static int odor_release_delay = 1;
    private ActivityMainBinding bindingMain;
    private static final String SHOWCASE_ID = "sequence example";
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (! XXPermissions.isGranted(MainActivity.this, Permission.MANAGE_EXTERNAL_STORAGE)){
            XXPermissions.startPermissionActivity(MainActivity.this, Permission.MANAGE_EXTERNAL_STORAGE);
        }
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingMain.getRoot());
        bindSocketService();
        MyOpenHelper dbHelper1 = new MyOpenHelper(MainActivity.this);
        SQLiteDatabase sqliteDatabase1 = dbHelper1.getWritableDatabase();
        sqliteDatabase1.close();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        odor_release_delay = retryCount.getInt("odor_release_delay", 1);
        TIME = retryCount.getInt("odor_release_time", 3);
        int time_sum_order = TIME  + odor_release_delay;
        float time_sum = time_sum_order * 1000.0f;
        time_sum_order = (int)time_sum;

        icon_con_success = bindingMain.iconConnectSuccess;
        icon_con_success.setVisibility(View.GONE);
        icon_con_error = bindingMain.iconConnectError;
        icon_con_error.setVisibility(View.VISIBLE);
        Button btn_mock_test = bindingMain.btnMockTest;
        Button btn_start_test = bindingMain.btnStartTest;
        Button btn_back_mange = bindingMain.btnBackMange;


        if (sc != null && socketService != null) {
            socketService.sendOrder("413");
        }

        Timer my_open_timer = new Timer();
        btn_mock_test.setOnClickListener(v -> {
            int external_status = retryCount.getInt("external_status",0);
            if( sc != null && socketService != null && external_status == 0){
                socketService.sendOrder("404");
                my_open_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("404");
                    }
                }, 100);
            }else if(sc != null && socketService != null && external_status == 1){
                socketService.sendOrder("402");
                my_open_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        socketService.sendOrder("402");
                    }
                }, 100);
            }
            Intent intent1 = new Intent(MainActivity.this, SimulationTesterActivity.class);
            startActivity(intent1);
        });
        int finalTime_sum_order = time_sum_order;

        btn_start_test.setOnClickListener(v -> {
            my_open_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sc != null && socketService != null) {
                    socketService.sendOrder(finalTime_sum_order + "415");
                    Log.d(TAG, finalTime_sum_order + "415" + "");
                }
            }
        }, 200);

            Intent intent2 = new Intent(MainActivity.this, TabActivity.class);
            startActivity(intent2);
        });

        btn_back_mange.setOnClickListener(v -> {
            Intent intent3 = new Intent(MainActivity.this, ManagementTabActivity.class);
            startActivity(intent3);
        });
        bindingMain.btnNav.setOnClickListener(V -> restartShowSequence());
        SharedPreferences retryCount14 = getSharedPreferences("retryCount", MODE_PRIVATE);
        SharedPreferences.Editor edit = retryCount14.edit();
        edit.putInt("cache", 1);
        edit.apply();
    }

    public void restartShowSequence(){
        MaterialShowcaseView.resetSingleUse(this, SHOWCASE_ID);
        presentShowcaseSequence();
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setSkipText(getString(R.string.ShowcaseSequence_6))
                        .setTarget(bindingMain.btnMockTest)
                        .setDismissText(getString(R.string.remind3))
                        .setContentText(getString(R.string.ShowcaseSequence_1))
                        .withRectangleShape(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setSkipText(getString(R.string.ShowcaseSequence_6))
                        .setTarget(bindingMain.btnStartTest)
                        .setDismissText(getString(R.string.remind3))
                        .setContentText(getString(R.string.ShowcaseSequence_2))
                        .withRectangleShape(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(bindingMain.btnBackMange)
                        .setSkipText(getString(R.string.ShowcaseSequence_6))
                        .setDismissText(getString(R.string.remind3))
                        .setContentText(getString(R.string.ShowcaseSequence_5))
                        .withRectangleShape(true)
                        .build()
        );
        sequence.start();
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
        bindService(intent, sc, BIND_AUTO_CREATE);//开启socket服务；自动开启
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConnectStatue(EventMsg msg) {
        if (msg.getTag().equals(Constants.CONNET_SUCCESS)) {
            icon_con_success.setVisibility(View.VISIBLE);
            icon_con_error.setVisibility(View.GONE);
        }
        if (msg.getTag().equals(Constants.CONNET_FAIL)) {
            icon_con_error.setVisibility(View.VISIBLE);
            icon_con_success.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, getString(R.string.toast_app_quit_ifo), Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            socketService.sendOrder("414");
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        socketService.sendOrder("414");
        super.onDestroy();
        //关闭socket服务
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}