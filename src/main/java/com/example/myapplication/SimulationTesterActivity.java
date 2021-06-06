package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
* 加载模拟测试 受试信息录入界面 获取测试者的个人信息 不储存到数据库
* */
public class SimulationTesterActivity extends AppCompatActivity {
    //String medicalHistory;
    String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        Button btn = (Button)findViewById(R.id.tester_btn);
        EditText eTN1 = (EditText)findViewById(R.id.editTextNum1);
        int random = new Random().nextInt(10000)+100;
        eTN1.setText(String.valueOf(random));
        eTN1.setEnabled(false);
        EditText eT1=(EditText)findViewById(R.id.editTextName);
        EditText eTN2=(EditText)findViewById(R.id.editTextNum2);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
                gender = radioButton.getText().toString();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//由layout输入框加入限制信息
                if(eT1.getText().toString().trim().isEmpty()||eTN1.getText().toString().trim().isEmpty()||eTN2.getText().toString().trim().isEmpty()){
                    Toast.makeText(SimulationTesterActivity.this,"请输入完整的个人信息",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SimulationTesterActivity.this,Ready2Activity.class);
                    getIntent().putExtra("simulation","simulation");
                    startActivity(intent);
                }
            }
        });
    }
}

