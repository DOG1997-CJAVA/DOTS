package com.example.myapplication.utils.options;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.example.myapplication.MainActivity;
import com.example.myapplication.OnDoubleClickListener;
import com.example.myapplication.R;
import com.example.myapplication.TestFinishActivity;
import com.example.myapplication.bitmaploader.MyBitmapLoder;
import com.example.myapplication.databinding.ActivityOption12Binding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.service.SocketService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OptionCustomActivity extends BaseActivity {
    private ActivityOption12Binding bindingOpt20;
    private static final String TAG = "OptionCoustomActivity";
    public static final String sql2_CN_NO = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='自定义气味测试' where ID=";
    public static final String sql2_EN_NO = "update " + Constants.TABLE_NAME + " set result='NORMOSMIA' , test_channel='Custom smell identification tests' where ID=";
    public static final String sql2_CN_MI = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='自定义气味测试' where ID=";
    public static final String sql2_EN_MI = "update " + Constants.TABLE_NAME + " set result='MICROSMIA' , test_channel='Custom smell identification tests' where ID=";
    public static final String sql2_CN_AS = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='自定义气味测试' where ID=";
    public static final String sql2_EN_AS = "update " + Constants.TABLE_NAME + " set result='ANOSMIA' , test_channel='Custom smell identification tests' where ID=";
    public static String sql_base = sql2_CN_NO;
    static int retry_20= 1;
    int TIME = 3;
    private int mProgressStatus = 0;
    private ServiceConnection sc;
    public SocketService socketService;
    private final String[] status_40 =  {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "306", "307","308"};// 20 21 为外部气体通道 1-19和22、23为识别通道
    final int[] retryCount_savce = {0};
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mProgressStatus++;
                    bindingOpt20.retryProgressBar.setProgress(mProgressStatus);
                    bindingOpt20.butRetryClicked.setVisibility(View.GONE);
                    bindingOpt20.retryProgressBar.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mProgressStatus = 0;
                    bindingOpt20.butRetryClicked.setEnabled(true);
                    bindingOpt20.retryProgressBar.setProgress(mProgressStatus);
                    bindingOpt20.retryProgressBar.setVisibility(View.GONE);
                    if (retryCount_savce[0] < retry_20) {
                        bindingOpt20.butRetryClicked.setVisibility(View.VISIBLE);
                    } else {
                        bindingOpt20.retryProgressBar.setVisibility(View.GONE);
                        bindingOpt20.butRetryClicked.setVisibility(View.GONE);
                        Toast.makeText(OptionCustomActivity.this, getString(R.string.btn_retry_complete), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingOpt20 = ActivityOption12Binding.inflate(getLayoutInflater());
        setContentView(bindingOpt20.getRoot());
        bindSocketService();
        MyOpenHelper dbHelper = new MyOpenHelper(OptionCustomActivity.this);
        MyBitmapLoder instance = MyBitmapLoder.getInstance();
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        SharedPreferences option2_shareprefe = getSharedPreferences("retryCount", Context.MODE_PRIVATE);
        int language_index = option2_shareprefe.getInt("language_set", 0);
        retry_20 = option2_shareprefe.getInt("retry_time", 1);
        TIME = option2_shareprefe.getInt("odor_release_time", 3);
        int id_odor_count = option2_shareprefe.getInt("id_odor_count", 20);

        if (retry_20 == 0) {//若不设置重闻则不显示重闻按键
            bindingOpt20.butRetryClicked.setVisibility(View.GONE);
            bindingOpt20.butRetryClicked.setEnabled(false);
        }
        bindingOpt20.retryProgressBar.setVisibility(View.GONE);
        String odorStartTime = getIntent().getStringExtra("odorStartTime");
        String odorEndTime = getIntent().getStringExtra("odorEndTime");
        long firstTime = getIntent().getLongExtra("firstTime",0);
        int number_count2 = getIntent().getIntExtra("number_count", 0);
        int accept2 = getIntent().getIntExtra("send2", 0);
        if (language_index == 1) {
            accept2 = accept2 + 20;
        }

        String correct = " ";
        String tempSrt = getString(R.string.ansewer_ui_count) + (number_count2 + 1);
        bindingOpt20.textview.setText(tempSrt);
        bindingOpt20.retryProgressBar.setMax(TIME);
        bindingOpt20.retryProgressBar.setClickable(false);
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1);
        String educate = getIntent().getStringExtra("educate");
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();
            bindingOpt20.text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            bindingOpt20.text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            bindingOpt20.text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            bindingOpt20.text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
            correct = cursor.getString(cursor.getColumnIndex("correct"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int count = getDrawable().size();
        if (count != 0) {
            int index1, index2, index3, index4;
            try {
                index1 = cursor.getInt(cursor.getColumnIndex("index01"));
                index2 = cursor.getInt(cursor.getColumnIndex("index02"));
                index3 = cursor.getInt(cursor.getColumnIndex("index03"));
                index4 = cursor.getInt(cursor.getColumnIndex("index04"));
                bindingOpt20.image1.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index1-1)));
                bindingOpt20.image2.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index2-1)));;
                bindingOpt20.image3.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index3-1)));;
                bindingOpt20.image4.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index4-1)));;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        String sql1 = "select *  from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        Cursor cursor1 = sqliteDatabase.rawQuery(sql1, null);
        if (!cursor1.moveToFirst()) {
            Log.e(TAG, "moveToPosition return fails, maybe table not created!!!");
        }
        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
        String name = cursor1.getString(cursor1.getColumnIndex("name"));
        String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        String age = cursor1.getString(cursor1.getColumnIndex("age"));
        cursor1.close();
        String finalCorrect = correct;
        int finalAccept = accept2 - 20;
        bindingOpt20.image1.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
            ContentValues cv = new ContentValues();
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text1.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("ID", id);
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text1.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.text1.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
            ContentValues cv = new ContentValues();
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text1.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("ID", id);
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text1.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.image2.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
            ContentValues cv = new ContentValues();
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text2.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("ID", id);
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text2.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.text2.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
            ContentValues cv = new ContentValues();
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text2.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("ID", id);
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text2.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.image3.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
            ContentValues cv = new ContentValues();
            cv.put("ID", id);
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text2.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text3.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.text3.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
            ContentValues cv = new ContentValues();
            cv.put("ID", id);
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text2.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text3.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.image4.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
            ContentValues cv = new ContentValues();
            cv.put("ID", id);
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text4.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text4.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.text4.setOnTouchListener(new OnDoubleClickListener(() -> {
            // TODO Auto-generated method stub
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
            ContentValues cv = new ContentValues();
            cv.put("ID", id);
            if((number_count2+1) == 1){
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("educate",educate);
            }
            if((bindingOpt20.text4.getText().toString().trim()).equals(finalCorrect.trim())){
                cv.put("answercount","1");
            }else {
                cv.put("answercount","0");
            }
            cv.put("correct_answer", finalCorrect);
            cv.put("option1", bindingOpt20.text1.getText().toString());
            cv.put("option2", bindingOpt20.text2.getText().toString());
            cv.put("option3", bindingOpt20.text3.getText().toString());
            cv.put("option4", bindingOpt20.text4.getText().toString());
            cv.put("answer", bindingOpt20.text4.getText().toString());
            long secondTime = System.currentTimeMillis();
            double responDurationTime = (secondTime - firstTime)/1000.00;
            cv.put("responDurationTime", responDurationTime);
            cv.put("responTime", sdf.format(date));
            cv.put("odorStartTime", odorStartTime);
            cv.put("odorEndTime", odorEndTime);
            cv.put("retryCount", retryCount_savce[0]);
            sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
            if (number_count2 == id_odor_count - 1) {
                ContentValues cv1 = new ContentValues();
                String sql22 = "select count(*) from " + Constants.TABLE_NAME4 + " where answercount = 1 and ID=" + id;
                Cursor cursor2 = sqliteDatabase.rawQuery(sql22, null);
                cursor2.moveToFirst();
                int count1 = cursor2.getInt(0);
                cv1.put("answercount", count1 + "/" + id_odor_count);
                if (count1 >= id_odor_count - 1) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_NO;
                    }else {
                        sql_base = sql2_EN_NO;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_normal_ifo));
                } else if (count1 >= id_odor_count - 2) {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_MI;
                    }else {
                        sql_base = sql2_EN_MI;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_mid_ifo));
                } else {
                    if(option2_shareprefe.getInt("language_set",0) == 0){
                        sql_base = sql2_CN_AS;
                    }else {
                        sql_base = sql2_EN_AS;
                    }
                    String sql2 = sql_base + id;
                    sqliteDatabase.execSQL(sql2);
                    cv1.put("result",getString(R.string.result_as_ifo));
                }
                cursor2.close();
                cv1.put("ID", id);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                cv1.put("result"," ");
                cv1.put("answercount"," ");
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                Intent intent = new Intent(OptionCustomActivity.this, TestFinishActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("send3", number_count2 + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
            sqliteDatabase.close();
        }));
        bindingOpt20.butRetryClicked.setOnClickListener(v -> {
            bindingOpt20.butRetryClicked.setEnabled(false);
            bindingOpt20.retryProgressBar.setVisibility(View.VISIBLE);
            if (retryCount_savce[0] < retry_20) {
                Log.d(TAG, retryCount_savce[0] + "");
                retryCount_savce[0]++;
                try {
                    socketService.sendOrder(status_40[finalAccept]);
                } catch (Exception e){
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {
                        while (mProgressStatus < TIME) {
                            try {
                                Thread.sleep(1000);
                                mHandler.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }.start();
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(bindingOpt20.getRoot());
            Toast.makeText(OptionCustomActivity.this,"横屏模式",Toast.LENGTH_SHORT).show();
        } else if (this.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(bindingOpt20.getRoot());
            Toast.makeText(OptionCustomActivity.this,"竖屏模式",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        new DroidDialog.Builder(this)
                .cancelable(true, false)
                .icon(R.drawable.ic_baseline_warning_24)
                .title(getString(R.string.remind))
                .content(getString(R.string.quit_remind))
                .cancelable(true, true)
                .positiveButton(getString(R.string.confirm1), dialog -> {
                    MyOpenHelper moh = new MyOpenHelper(OptionCustomActivity.this);
                    SQLiteDatabase sd = moh.getReadableDatabase();
                    String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                    Cursor cursor1 = sd.rawQuery(sql1, null);
                    cursor1.moveToFirst();
                    String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                    cursor1.close();
                    String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                    sd.execSQL(sql);
                    sd.close();
                    Intent intent = new Intent(OptionCustomActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .negativeButton(getString(R.string.cencle1), Dialog::dismiss)
                .animation(AnimUtils.AnimFadeInOut)
                .color(ContextCompat.getColor(OptionCustomActivity.this, R.color.orange), ContextCompat.getColor(OptionCustomActivity.this, R.color.white),
                        ContextCompat.getColor(OptionCustomActivity.this, R.color.black))
                .show();
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

    private ArrayList<Drawable> getDrawable() {
        MyOpenHelper moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        ArrayList<Drawable> drawables = new ArrayList<>();
        Cursor c = sd.query("picture", null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            while (c.moveToNext()) {
                byte[] answerA = c.getBlob(c.getColumnIndexOrThrow(MyOpenHelper.PictureColumns.PICTURE));
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(answerA, 0, answerA.length, null);
                Drawable drawable1 = new BitmapDrawable(getResources(), bitmap1);
                drawables.add(drawable1);
            }
        }
        assert c != null;
        c.close();
        return drawables;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sc != null) {
            unbindService(sc);
        }
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

}