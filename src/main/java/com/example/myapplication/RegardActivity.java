package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;

import com.example.myapplication.language.BaseActivity;

public class RegardActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regard);
        //判断父Activity是否为空，不为空设置导航图标显示
        if(NavUtils.getParentActivityName(RegardActivity.this)!=null){
            //显示向左的图标箭头
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
