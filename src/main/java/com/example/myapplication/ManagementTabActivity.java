package com.example.myapplication;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.list.DialogSingleChoiceExtKt;

public class ManagementTabActivity extends Activity {
    private Button btn00,btn01,btn02,btn_retry,rdBtn01,rdBtn02,rdBtn03,rdBtn04;
    private RadioGroup rdDroup;
    private TextView retryNumberShow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //新增SharedPreferences
        SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
        setContentView(R.layout.activity_managementtab);
        btn00 = (Button) findViewById(R.id.btn00);
        btn01 = (Button) findViewById(R.id.btn01);
        btn02 = (Button) findViewById(R.id.btn02);
        btn_retry = (Button) findViewById(R.id.btn_retry_set);
/*        rdBtn01 = (RadioButton) findViewById(R.id.btnRetry1);
        rdBtn02 = (RadioButton) findViewById(R.id.btnRetry2);
        rdBtn03 = (RadioButton) findViewById(R.id.btnRetry3);
        rdBtn04 = (RadioButton) findViewById(R.id.btnRetry4);
        rdDroup = (RadioGroup)  findViewById(R.id.radioGroupRetry);*/
        retryNumberShow = (TextView) findViewById(R.id.retryNumberShow);
        retryNumberShow.setText(retryCount.getInt("retry_time",1)+"");
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
                            retryNumberShow.setText(index+1+"");
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

/*        rdDroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getCheckedRadioButtonId() == rdBtn01.getId()){
                    SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount.edit();
                    edit.putInt("retry_time",1);
                    edit.apply();
                    retryNumberShow.setText("1");
                }
                if(group.getCheckedRadioButtonId() == rdBtn02.getId()){
                    SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount.edit();
                    edit.putInt("retry_time",2);
                    edit.apply();
                    retryNumberShow.setText("2");
                }
                if(group.getCheckedRadioButtonId() == rdBtn03.getId()){
                    SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount.edit();
                    edit.putInt("retry_time",3);
                    edit.apply();
                    retryNumberShow.setText("3");
                }
                if(group.getCheckedRadioButtonId() == rdBtn04.getId()){
                    SharedPreferences retryCount = getSharedPreferences("retryCount", MODE_PRIVATE);
                    SharedPreferences.Editor edit = retryCount.edit();
                    edit.putInt("retry_time",4);
                    edit.apply();
                    retryNumberShow.setText("4");
                }
            }
        });*/
    }
}