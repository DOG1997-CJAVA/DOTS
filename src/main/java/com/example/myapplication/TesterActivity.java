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

public class TesterActivity extends BaseActivity {
    //String medicalHistory;
    String gender;
    boolean gender_is_check = false;//修复性别不选也可进行测试的bug
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
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
                gender_is_check = true;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //trim 去掉前后多余的东西 多余的回车换行键
                if(eT1.getText().toString().trim().isEmpty()||eTN1.getText().toString().trim().isEmpty()
                        ||eTN2.getText().toString().trim().isEmpty()|| !gender_is_check){
                    Toast.makeText(TesterActivity.this,getString(R.string.info_incomplete_remind),Toast.LENGTH_SHORT).show();
                }else{
                    // 创建SQLiteOpenHelper子类对象
                    ////注意，一定要传入最新的数据库版本号
                    MyOpenHelper dbHelper1 = new MyOpenHelper(TesterActivity.this);
                    // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
                    SQLiteDatabase sqliteDatabase1 = dbHelper1.getWritableDatabase();
                    // 创建ContentValues对象
                    ContentValues values1 = new ContentValues();
                    String record_temp = getString(R.string.quit_record_notcomplit);
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
                    Intent intent = getIntent();
                    String value = intent.getStringExtra("channel");
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

