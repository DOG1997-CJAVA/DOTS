package com.example.myapplication;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

/**
 * Created by G2 on 2021/2/8.
 */
public class InstructionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        //判断父Activity是否为空，不为空设置导航图标显示
        if(NavUtils.getParentActivityName(InstructionActivity.this)!=null){
            //显示向左的图标箭头
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
