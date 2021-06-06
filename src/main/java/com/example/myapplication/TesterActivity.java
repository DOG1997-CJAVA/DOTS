package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;

import java.util.Random;

public class TesterActivity extends AppCompatActivity {
    //String medicalHistory;
    String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
//        List<String> list = new ArrayList<String>();
//        list.add("无");
//        list.add("嗅觉减退");
//        list.add("后天嗅觉丧失");//后天
//        list.add("先天嗅觉缺失");//先天
//        list.add("嗅觉过敏");
//        list.add("幻嗅");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        Spinner sp = (Spinner) findViewById(R.id.spinner1);
//        sp.setAdapter(adapter);
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            // parent： 为控件Spinner view：显示文字的TextView position：下拉选项的位置从0开始
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                TextView tvResult = (TextView) findViewById(R.id.tvResult);
//                //获取Spinner控件的适配器
//                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
//                tvResult.setText(adapter.getItem(position));
//                TextView tv = (TextView)view;
//                tv.setTextSize(40);
//                //获取选中值
//                Spinner spinner = (Spinner) parent;
//                medicalHistory = (String) spinner.getItemAtPosition(position);
//
//            }
//            //没有选中时的处理
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        Button btn=(Button)findViewById(R.id.tester_btn);
        EditText eTN1=(EditText)findViewById(R.id.editTextNum1);
        int random = new Random().nextInt(8999)+1000;
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
            public void onClick(View v) {//trim 去掉前后多余的东西 多余的回车换行键
                if(eT1.getText().toString().trim().isEmpty()||eTN1.getText().toString().trim().isEmpty()||eTN2.getText().toString().trim().isEmpty()){
                    Toast.makeText(TesterActivity.this,"请输入完整的个人信息",Toast.LENGTH_SHORT).show();
                }else{
                    // 创建SQLiteOpenHelper子类对象
                    ////注意，一定要传入最新的数据库版本号
                    MyOpenHelper dbHelper1 = new MyOpenHelper(TesterActivity.this);
                    // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
                    SQLiteDatabase sqliteDatabase1 = dbHelper1.getWritableDatabase();
                    // 创建ContentValues对象
                    ContentValues values1 = new ContentValues();
                    String sq1 = "delete from "+Constants.TABLE_NAME+" where result="+"'默认'";
                    sqliteDatabase1.execSQL(sq1);
                    // 向该对象中插入键值对
                    values1.put("ID", eTN1.getText().toString());
                    values1.put("name", eT1.getText().toString());
                    values1.put("age",eTN2.getText().toString());
                   // values1.put("medicalHistory",medicalHistory);
                    values1.put("gender",gender);
                    values1.put("result","默认");
                    // 调用insert()方法将数据插入到数据库当中
                    sqliteDatabase1.insert(Constants.TABLE_NAME, null, values1);
                    // sqliteDatabase.execSQL("insert into user (id,name) values (1,'carson')");
                    //关闭数据库
                    sqliteDatabase1.close();
                    Intent intent=getIntent();
                    String value=intent.getStringExtra("channel");
                    if("12通道".equals(value)){
                        Intent intent1=new Intent(TesterActivity.this, Ready12Activity.class);
                        startActivity(intent1);
                        finish();
                    }else if("20通道".equals(value)){
                        Intent intent1=new Intent(TesterActivity.this,Ready20Activity.class);
                        startActivity(intent1);
                        finish();
                    }else if("40通道".equals(value)){
                        Intent intent1=new Intent(TesterActivity.this,Ready40Activity.class);
                        startActivity(intent1);
                        finish(); }
                }
            }
        });
    }
}

