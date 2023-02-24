package com.example.myapplication;

import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.bitmaploader.MyBitmapLoder;
import com.example.myapplication.bitmaploader.MyBitmapRatio;
import com.example.myapplication.service.SocketService;


public class ChannelSelect_Fragment extends Fragment {
    int[] btn = {R.id.btn_12, R.id.btn_20, R.id.btn_40};
    private static final String TAG = "ChannelSelete";
    Button btn_12, btn_20, btn_40, btn_cus;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channelselect_fragment, container, false);
        int[] color = getResources().getIntArray(R.array.bgcolor);
        for (int i = 0; i < 3; i++) {
            Button bt = (Button) view.findViewById(btn[i]);
            bt.setBackgroundColor(color[i]);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences("retryCount", Context.MODE_PRIVATE);
        int load_status = prefs.getInt("cache", 1);
        if (load_status == 1) {
            MyBitmapLoder instance = MyBitmapLoder.getInstance();
            instance.init();
            MyBitmapRatio myBitmapRatio = new MyBitmapRatio();
            Resources r = this.getResources();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 50; i++) {
                        Bitmap bitmap = myBitmapRatio.ratioBitmap(r, drawable_number[i], 400, 270);
                        instance.putBitmapToCache(String.valueOf(i), bitmap);
                        Log.d(TAG, "正在保存图片到内存：" + i);
                    }
                }
            }).start();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("cache", 2);//记录缓存 硬盘储存状态 避免重复缓存
            edit.apply();
        }

        //12通道监听
        btn_12 = getActivity().findViewById(R.id.btn_12);
        btn_12.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TesterActivity.class);
            intent.putExtra("channel", "12通道");
            startActivity(intent);
        });
        //20通道监听
        btn_20 = getActivity().findViewById(R.id.btn_20);
        btn_20.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TesterActivity.class);
            intent.putExtra("channel", "20通道");
            startActivity(intent);
        });
        //40通道监听
        btn_40 = getActivity().findViewById(R.id.btn_40);
        //btn_40.setVisibility(View.GONE);
        btn_40.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),TesterActivity.class);
            intent.putExtra("channel","40通道");
            startActivity(intent);
        });

        btn_cus = getActivity().findViewById(R.id.btn_cus);
        //btn_40.setVisibility(View.GONE);
        btn_cus.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(),TesterActivity.class);
            intent.putExtra("channel","custom");
            startActivity(intent);
        });
    }

}
