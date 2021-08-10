package com.example.myapplication.utils.options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.OnDoubleClickListener;
import com.example.myapplication.R;
import com.example.myapplication.TabActivity;
import com.example.myapplication.databinding.ActivityOption12Binding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Option20Activity extends Activity {

    private static final String TAG = "Option20Activity";
    private ActivityOption12Binding bindingOpt20;
    public static final String sql2_CN_NO = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='20项气味测试' where ID=";
    public static final String sql2_EN_NO = "update " + Constants.TABLE_NAME + " set result='NORMOSMIA' , test_channel='20 odor tests' where ID=";
    public static final String sql2_CN_MI = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='20项气味测试' where ID=";
    public static final String sql2_EN_MI = "update " + Constants.TABLE_NAME + " set result='MICROSMIA' , test_channel='20 odor tests' where ID=";
    public static final String sql2_CN_AS = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='20项气味测试' where ID=";
    public static final String sql2_EN_AS = "update " + Constants.TABLE_NAME + " set result='ANOSMIA' , test_channel='20 odor tests' where ID=";
    public static String sql_base = sql2_CN_NO;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingOpt20 = ActivityOption12Binding.inflate(getLayoutInflater());
        setContentView(bindingOpt20.getRoot());

        MyOpenHelper dbHelper = new MyOpenHelper(Option20Activity.this);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        String odorStartTime = getIntent().getStringExtra("odorStartTime");
        String odorEndTime = getIntent().getStringExtra("odorEndTime");
        String retryCount = getIntent().getStringExtra("retryCount");
        int accept2 = getIntent().getIntExtra("send2", 0);
        int number_count2 = getIntent().getIntExtra("number_count", 0);
        int index1, index2, index3, index4;

        SharedPreferences language_set = getSharedPreferences("retryCount",MODE_PRIVATE);
        int language_index = language_set.getInt("language_set", 0);
        if(language_index == 1){
            accept2 = accept2 + 40;
        }
        Log.d(TAG,"****************************************");
        Log.d(TAG,Integer.toString(language_index));
        Log.d(TAG,"****************************************");

        String correct = " ";
        TextView textView = findViewById(R.id.textview);
        String tempSrt= "第" + (number_count2 + 1) + "题";
        textView.setText(tempSrt);
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1 );
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
            try {
                index1 = cursor.getInt(cursor.getColumnIndex("index01"));
                index2 = cursor.getInt(cursor.getColumnIndex("index02"));
                index3 = cursor.getInt(cursor.getColumnIndex("index03"));
                index4 = cursor.getInt(cursor.getColumnIndex("index04"));
                bindingOpt20.image1.setImageDrawable(getDrawable().get(index1 - 1));
                bindingOpt20.image2.setImageDrawable(getDrawable().get(index2 - 1));
                bindingOpt20.image3.setImageDrawable(getDrawable().get(index3 - 1));
                bindingOpt20.image4.setImageDrawable(getDrawable().get(index4 - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        Cursor cursor1 = sqliteDatabase.rawQuery(sql1, null);
        cursor1.moveToFirst();
        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
        String name = cursor1.getString(cursor1.getColumnIndex("name"));
        String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        String age = cursor1.getString(cursor1.getColumnIndex("age"));
        cursor1.close();
        String finalCorrect = correct;
        bindingOpt20.image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt20.text1.getText().toString());
                cv.put("option2", bindingOpt20.text2.getText().toString());
                cv.put("option3", bindingOpt20.text3.getText().toString());
                cv.put("option4", bindingOpt20.text4.getText().toString());
                cv.put("answer", bindingOpt20.text1.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 19) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "20");
                    if (count >= 18) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_NO;
                        }else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 15) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_MI;
                        }else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_AS;
                        }else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        bindingOpt20.image2.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt20.text1.getText().toString());
                cv.put("option2", bindingOpt20.text2.getText().toString());
                cv.put("option3", bindingOpt20.text3.getText().toString());
                cv.put("option4", bindingOpt20.text4.getText().toString());
                cv.put("answer", bindingOpt20.text2.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 19) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "20");
                    if (count >= 18) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_NO;
                        }else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 15) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_MI;
                        }else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_AS;
                        }else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        bindingOpt20.image3.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt20.text1.getText().toString());
                cv.put("option2", bindingOpt20.text2.getText().toString());
                cv.put("option3", bindingOpt20.text3.getText().toString());
                cv.put("option4", bindingOpt20.text4.getText().toString());
                cv.put("answer", bindingOpt20.text3.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 19) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "20");
                    if (count >= 18) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_NO;
                        }else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 15) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_MI;
                        }else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_AS;
                        }else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        bindingOpt20.image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",Locale.CHINA);
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt20.text1.getText().toString());
                cv.put("option2", bindingOpt20.text2.getText().toString());
                cv.put("option3", bindingOpt20.text3.getText().toString());
                cv.put("option4", bindingOpt20.text4.getText().toString());
                cv.put("answer", bindingOpt20.text4.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 19) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "20");
                    if (count >= 18) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_NO;
                        }else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 15) {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_MI;
                        }else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if(language_set.getInt("language_set",0) == 0){
                            sql_base = sql2_CN_AS;
                        }else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
    }

    public void onBackPressed() {
// 这里处理逻辑代码，该方法仅适用于2.0或更新版的sdk
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.talk)//设置标题的图片
                .setTitle(getString(R.string.remind))//设置对话框的标题
                .setMessage(getString(R.string.quit_remind))//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton(getString(R.string.cencle1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.confirm1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyOpenHelper moh = new MyOpenHelper(Option20Activity.this);
                        SQLiteDatabase sd = moh.getReadableDatabase();
                        String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                        Cursor cursor1 = sd.rawQuery(sql1, null);
                        cursor1.moveToFirst();
                        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                        cursor1.close();
                        String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                        sd.execSQL(sql);
                        sd.close();
                        Intent intent = new Intent(Option20Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
    }

    private ArrayList<Drawable> getDrawable() {
        MyOpenHelper moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
        Cursor c = sd.query("picture", null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            while (c.moveToNext()) {
                byte[] answerA = c.getBlob(c.getColumnIndexOrThrow(MyOpenHelper.PictureColumns.PICTURE));
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(answerA, 0, answerA.length, null);
                BitmapDrawable bitmapDrawable1 = new BitmapDrawable(getResources(),bitmap1);
                Drawable drawable1 = bitmapDrawable1;
                drawables.add(drawable1);
            }
        }
        assert c != null;
        c.close();
        return drawables;
    }
}
