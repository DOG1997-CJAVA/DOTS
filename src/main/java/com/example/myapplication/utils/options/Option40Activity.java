package com.example.myapplication.utils.options;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.OnDoubleClickListener;
import com.example.myapplication.R;
import com.example.myapplication.TabActivity;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Option40Activity extends Activity {

    private int count;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option12);
        MyOpenHelper dbHelper = new MyOpenHelper(Option40Activity.this);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);
        ImageView image4 = (ImageView) findViewById(R.id.image4);
        TextView text1 = (TextView) findViewById(R.id.text1);
        TextView text2 = (TextView) findViewById(R.id.text2);
        TextView text3 = (TextView) findViewById(R.id.text3);
        TextView text4 = (TextView) findViewById(R.id.text4);
        String odorStartTime = getIntent().getStringExtra("odorStartTime");
        String odorEndTime = getIntent().getStringExtra("odorEndTime");
        String retryCount = getIntent().getStringExtra("retryCount");
        int accept2 = getIntent().getIntExtra("send2", 0);//题目计数 答题返回计数 由number_count2代替
        int number_count2 = getIntent().getIntExtra("number_count", 0);
        int index1, index2, index3, index4;
        String correct = " ";
        textView = (TextView) findViewById(R.id.textview);
        textView.setText("第" + (number_count2 + 1) + "题");
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1 );//分离 accept2只负责 取数据 不负责返回计数
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();
            text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
            correct = cursor.getString(cursor.getColumnIndex("correct"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        count = getDrawable().size();
        if (count != 0) {
            try {
                index1 = cursor.getInt(cursor.getColumnIndex("index01"));
                index2 = cursor.getInt(cursor.getColumnIndex("index02"));
                index3 = cursor.getInt(cursor.getColumnIndex("index03"));
                index4 = cursor.getInt(cursor.getColumnIndex("index04"));
                image1.setImageDrawable(getDrawable().get(index1 - 1));
                image2.setImageDrawable(getDrawable().get(index2 - 1));
                image3.setImageDrawable(getDrawable().get(index3 - 1));
                image4.setImageDrawable(getDrawable().get(index4 - 1));
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
        image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2 +1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text1.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 39) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "40");
                    if (count >= 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 33 && count < 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    cv1.put("answer",name + "的测试结果");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    cv1.put("answer"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option40Activity.this)
                            .setIcon(R.mipmap.talk)
                            .setTitle("提示")
                            .setMessage("测试完成，感谢您的使用！！！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option40Activity.this, "谢谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option40Activity.this, TabActivity.class);
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
                //关闭数据库
                sqliteDatabase.close();
            }
        }));
        image2.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2 +1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text2.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 39) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "40");
                    if (count >= 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 33 && count < 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    cv1.put("answer",name + "的测试结果");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    cv1.put("answer"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option40Activity.this)
                            .setIcon(R.mipmap.talk)
                            .setTitle("提示")
                            .setMessage("测试完成，感谢您的使用！！！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option40Activity.this, "谢谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option40Activity.this, TabActivity.class);
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
                //关闭数据库
                sqliteDatabase.close();
            }
        }));
        image3.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2 +1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text3.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 39) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "40");
                    if (count >= 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 33 && count < 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    cv1.put("answer",name + "的测试结果");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    cv1.put("answer"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option40Activity.this)
                            .setIcon(R.mipmap.talk)
                            .setTitle("提示")
                            .setMessage("测试完成，感谢您的使用！！！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option40Activity.this, "谢谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option40Activity.this, TabActivity.class);
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
                //关闭数据库
                sqliteDatabase.close();
            }
        }));
        image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                if((number_count2 +1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text4.getText().toString());
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 39) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where correct_answer=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "40");
                    if (count >= 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 33 && count < 36) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='40项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    cv1.put("answer",name + "的测试结果");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");
                    cv1.put("answercount"," ");
                    cv1.put("answer"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option40Activity.this)
                            .setIcon(R.mipmap.talk)
                            .setTitle("提示")
                            .setMessage("测试完成，感谢您的使用！！！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option40Activity.this, "谢谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option40Activity.this, TabActivity.class);
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

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.talk)//设置标题的图片
                .setTitle("提示")//设置对话框的标题
                .setMessage("是否退出当前页面？退出将不会保留任何作答结果")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyOpenHelper moh = new MyOpenHelper(Option40Activity.this);
                        SQLiteDatabase sd = moh.getReadableDatabase();
                        String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                        Cursor cursor1 = sd.rawQuery(sql1, null);
                        cursor1.moveToFirst();
                        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                        cursor1.close();
                        String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                        sd.execSQL(sql);
                        sd.close();
                        Intent intent = new Intent(Option40Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(40);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
        return;
    }

    private ArrayList<Drawable> getDrawable() {
        MyOpenHelper moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
        //查询数据库
        Cursor c = sd.query("picture", null, null, null, null, null, null);
        //遍历数据
        if (c != null && c.getCount() != 0) {
            while (c.moveToNext()) {
                //获取数据
                byte[] answerA = c.getBlob(c.getColumnIndexOrThrow(MyOpenHelper.PictureColumns.PICTURE));
                //将获取的数据转换成drawable
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(answerA, 0, answerA.length, null);
                BitmapDrawable bitmapDrawable1 = new BitmapDrawable(bitmap1);
                Drawable drawable1 = bitmapDrawable1;
                drawables.add(drawable1);
            }
        }
        return drawables;
    }
}
