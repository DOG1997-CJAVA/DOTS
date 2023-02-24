package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.myapplication.bitmaploader.MyBitmapLoder;
import com.example.myapplication.bitmaploader.MyBitmapRatio;
import com.example.myapplication.databinding.ActivityMockTestChooseBinding;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.mocktestid.Ready2Activity;
import com.example.myapplication.service.SocketService;

/*
 * 加载模拟测试 受试信息录入界面 获取测试者的个人信息 不储存到数据库
 * */
public class SimulationTesterActivity extends BaseActivity {
    private ServiceConnection sc;
    public SocketService socketService;
    int TIME = 3;               //定义默认时间长度
    static int odor_release_delay = 1;
    int[] drawable_number = {
            R.drawable.xiangjiao, R.drawable.tanxiang, R.drawable.yezi, R.drawable.kafei,  //4
            R.drawable.qiaokeli, R.drawable.chengzi, R.drawable.putao, R.drawable.ningmeng,  //8
            R.drawable.meigui, R.drawable.huasheng, R.drawable.caomei, R.drawable.boluo,  //12
            R.drawable.yu, R.drawable.huanggua, R.drawable.jiang, R.drawable.zhimayou,  //16
            R.drawable.pingguo, R.drawable.dasuan, R.drawable.pige, R.drawable.dingxiang,  //20
            R.drawable.zidingxiang, R.drawable.huangyou, R.drawable.bohe, R.drawable.taozi,  //24
            R.drawable.yingtao, R.drawable.molihua, R.drawable.yingershuangshenfen, R.drawable.yan,  // 28
            R.drawable.songshu, R.drawable.feizao, R.drawable.lvcaodi, R.drawable.bohenao,
            R.drawable.mangguo, R.drawable.niunai, R.drawable.xiangcaobingqilin, R.drawable.cu,
            R.drawable.jiangyou, R.drawable.yangcong, R.drawable.gancao, R.drawable.youzi,
            R.drawable.hetao, R.drawable.binggan, R.drawable.fengmi, R.drawable.rougui,
            R.drawable.nailao, R.drawable.li, R.drawable.xigua, R.drawable.kouxiangtang,
            R.drawable.youqixishiji, R.drawable.tianranqi
    };
    private static final String TAG = "SimulationBitmapLoad";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.myapplication.databinding.ActivityMockTestChooseBinding bindingMockTest = ActivityMockTestChooseBinding.inflate(getLayoutInflater());
        setContentView(bindingMockTest.getRoot());
        bindSocketService();
        SharedPreferences prefs = this.getSharedPreferences("retryCount", Context.MODE_PRIVATE);
        int load_status = prefs.getInt("cache", 1);
        if (load_status == 1) {
            MyBitmapLoder instance = MyBitmapLoder.getInstance();
            instance.init();
            MyBitmapRatio myBitmapRatio = new MyBitmapRatio();
            Resources r = this.getResources();
            for (int i = 0; i < 50; i++) {
                Bitmap bitmap = myBitmapRatio.ratioBitmap(r, drawable_number[i], 400, 270);
                instance.putBitmapToCache(String.valueOf(i), bitmap);
                Log.d(TAG, "正在保存图片到内存：" + i);
            }
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("cache", 2);//记录缓存 硬盘储存状态 避免重复缓存
            edit.apply();
        }
        Button btn_mock_test_id = bindingMockTest.btnSimulationTestId;
        SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
        odor_release_delay = retryCount.getInt("odor_release_delay", 1);
        TIME = retryCount.getInt("odor_release_time", 3);//默认3s
        int time_sum_order = TIME + odor_release_delay;//电磁阀打开总时间 = 提前打开时间 + 气味释放时间
        float time_sum = time_sum_order * 1000.0f;
        time_sum_order = (int) time_sum;
        int finalTime_sum_order = time_sum_order;
        btn_mock_test_id.setOnClickListener(v -> {
            if (sc != null && socketService != null) {//向下位机传递 识别延时
                socketService.sendOrder(finalTime_sum_order + "415");
                Log.d(TAG, finalTime_sum_order + "415" + "");
            }
            Intent intent1 = new Intent(SimulationTesterActivity.this, Ready2Activity.class);
            startActivity(intent1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

}

