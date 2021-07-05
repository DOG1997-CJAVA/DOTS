package com.example.myapplication;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogSingleChoiceExtKt;

import java.util.ArrayList;

public class ManagementTabActivity extends Activity {
    private Button btn00,btn01,btn02,btn_retry,btn_release_delay,btn_random_mode;
    private TextView retryNumberShow,modeToSet;
    private int oddr_delay_temp = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //新增SharedPreferences
        SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
        setContentView(R.layout.activity_managementtab);
        btn00 = (Button) findViewById(R.id.btn00);
        btn01 = (Button) findViewById(R.id.btn01);
        btn02 = (Button) findViewById(R.id.btn02);
        btn_release_delay = (Button) findViewById(R.id.odor_release_delay);
        btn_random_mode = (Button) findViewById(R.id.random_fix_mode);
        btn_retry = (Button) findViewById(R.id.btn_retry_set);
        retryNumberShow = (TextView) findViewById(R.id.retryNumberShow);
        modeToSet = (TextView) findViewById(R.id.mode_to_set);//创建页面初始化
        if(retryCount.getInt("btn_random_mode",1) == 0){
            modeToSet.setText("随机模式");
        }else {
            modeToSet.setText("固定模式");
        }
        if(retryCount.getInt("retry_time",1) <= 4){
            retryNumberShow.setText(retryCount.getInt("retry_time",1)+"");
        }else {
            retryNumberShow.setText("0次：不可重闻");
        }
        Ready2Activity.getRetryData(this);
        Ready12Activity.getRetryData(this);
        Ready20Activity.getRetryData(this);
        Ready40Activity.getRetryData(this);
        btn00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent0 = new Intent(ManagementTabActivity.this,ConnectActivity.class);
                startActivity(intent0);
            }
        });
        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ManagementTabActivity.this,ControlTestActivity.class);
                startActivity(intent1);
            }
        });
        btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ManagementTabActivity.this,ClickTheLvItemContentActivity.class);
                startActivity(intent2);
            }
        });

        btn_random_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
                dialog.title(R.string.test_mode, null);
                DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.random_fix_mode, null, null, 0,
                        true, (materialDialog, index, text) -> {
                            Toast.makeText(ManagementTabActivity.this, "已设置为 " + text , Toast.LENGTH_SHORT).show();
                            SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                            SharedPreferences.Editor edit = retryCount.edit();
                            edit.putInt("btn_random_mode",index);//0:随机模式 1：固定模式
                            edit.apply();
                            if(index == 0){
                                modeToSet.setText("随机模式");
                            }else {
                                modeToSet.setText("固定模式");
                            }
                            return null;
                        });
                dialog.positiveButton(R.string.confirm1,null,materialDialog -> {
                    dialog.dismiss();
                    return null;
                });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ManagementTabActivity.this, "已取消设置", Toast.LENGTH_SHORT).show();
                    return null;
                });
                dialog.show();

            }
        });

        //需要同步发送指令，修改单片机程序 避免app指令发送延时后，下位机依旧打开固定时间，关闭时间出错
        //单片机 打开总时间 = 接收到指令，提前打开时间 + 固定嗅闻时间
        btn_release_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
                dialog.title(R.string.remind4, null);
                dialog.message(R.string.release_delay_set, null, null);
                DialogInputExtKt.input(dialog, "请输入延时时间/ms", null, null, null,
                        InputType.TYPE_CLASS_NUMBER,
                        null, true, false, (materialDialog, text) -> {
                            Toast.makeText(ManagementTabActivity.this, "Input  " + text, Toast.LENGTH_SHORT).show();
                            oddr_delay_temp = Integer.parseInt(text.toString());
                            return null;
                        });
                dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                    Toast.makeText(ManagementTabActivity.this, getText(R.string.confirm1), Toast.LENGTH_SHORT).show();
                    SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount.edit();
                    edit.putInt("odor_release_delay",oddr_delay_temp);
                    edit.apply();
                    dialog.dismiss();
                    return null;
                });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ManagementTabActivity.this, "已取消设置", Toast.LENGTH_SHORT).show();
                    return null;
                });
                dialog.show();
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog(ManagementTabActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
                dialog.title(R.string.retry1, null);
                DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, R.array.retry_times, null, null, 0,
                        true, (materialDialog, index, text) -> {
                            Toast.makeText(ManagementTabActivity.this, "已设置为 " + text , Toast.LENGTH_SHORT).show();
                            SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                            SharedPreferences.Editor edit = retryCount.edit();
                            edit.putInt("retry_time",index+1);
                            edit.apply();
                            if (index <= 4) {
                                retryNumberShow.setText(index + 1 + "");
                            } else {
                                retryNumberShow.setText("0次：不可重闻");
                            }
                            return null;
                        });
                dialog.positiveButton(R.string.confirm1,null,materialDialog -> {
                    dialog.dismiss();
                    return null;
                        });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ManagementTabActivity.this, "已取消设置", Toast.LENGTH_SHORT).show();
                    return null;
                });
                dialog.show();
            }
        });
    }
}