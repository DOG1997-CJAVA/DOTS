package com.example.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.common.EventMsg;
import com.example.myapplication.db.Constants;
import com.example.myapplication.service.SocketService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Objects;

import butterknife.ButterKnife;

//import com.yariksoffice.lingver.Lingver;


public class MainActivity extends BaseActivity {
    private ServiceConnection sc;
    private static boolean reload = false;
    private long firstTime = 0;
    public SocketService socketService;
    public ImageView icon10, icon11;
    public static final String TAG = "RightFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindSocketService();// 通过binder拿到service 开启服务
        ButterKnife.bind(this);
        //重启语言设置
/*        if(!reload) {
            SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
            int language_index = retryCount.getInt("language_set", 0);
            String language;
            Log.d(TAG, Integer.toString(language_index));
            if (language_index == 0) {
                language = "zh_simple";
            } else {
                language = "en";
            }
            Log.d(TAG, language);
            switchLanguage(language);
        }*/
        //store = PreferenceLocaleStore(this, Locale(LANGUAGE_ENGLISH))
        //Lingver.init(this, Locale(ENGLISH));
        //Intent intent = getIntent();
        //connect_sur_ero = intent.getBooleanExtra("connect_statue",false);
        /*register EventBus*/
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        icon10 = findViewById(R.id.icon_connect_success);
        icon10.setVisibility(View.GONE);//默认链接失败，等待更新
        icon11 = findViewById(R.id.icon_connect_error);
        icon11.setVisibility(View.VISIBLE);
        Button btn100 = findViewById(R.id.btn100);
        Button btn101 = findViewById(R.id.btn101);
        Button btn102 = findViewById(R.id.btn102);
        btn100.setOnClickListener(v -> {
            //跳转到SimulationTesterActivity 模拟测试页面
            Intent intent1 = new Intent(MainActivity.this, SimulationTesterActivity.class);
            startActivity(intent1);
        });
        //跳转到SimulationTesterActivity 测试选择页面
        btn101.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, TabActivity.class);
            startActivity(intent2);
        });
        //管理员界面
        btn102.setOnClickListener(v -> {
            Intent intent3 = new Intent(MainActivity.this, ManagementTabActivity.class);
            startActivity(intent3);
        });
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

    //表示无论事件是在哪个线程发布出来的，该事件订阅方法onEvent都会在UI线程中执行
    //订阅事件  Android中只能在UI线程中更新UI
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConnectStatue(EventMsg msg) {
        if (msg.getTag().equals(Constants.CONNET_SUCCESS)) {
            /*接收到这个消息说明连接成功*/
            icon10.setVisibility(View.VISIBLE);
            icon11.setVisibility(View.GONE);
        }
        if (msg.getTag().equals(Constants.CONNET_FAIL)) {
            icon11.setVisibility(View.VISIBLE);
            icon10.setVisibility(View.GONE);
        }
    }

/*    private void switchLanguage(String language) {
        //设置应用语言类型
        reload = true;
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh_simple")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        //更新语言后，destroy当前页面，重新绘制
        finish();
        Intent it = new Intent(MainActivity.this, MainActivity.class);
        //清空任务栈确保当前打开activit为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }*/

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, getString(R.string.toast_app_quit_ifo), Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            socketService.sendOrder("414");//退出app时需要 关闭气泵 排风扇 以及所有电磁阀 等待断电
            System.exit(0);
        }
    }


    @Override
    protected void onDestroy() {
        socketService.sendOrder("414");//退出app时需要 关闭气泵 排风扇 以及所有电磁阀 等待断电
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