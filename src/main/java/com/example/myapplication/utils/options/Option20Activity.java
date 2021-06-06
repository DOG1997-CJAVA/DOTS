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

public class Option20Activity extends Activity {

    private int count;
    //private Animation myAlphaAnimation;
    //private ImageView myImageView;
    private TextView textView;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private String ms_time_str = formatter.format(new Date());/*
            记录各个通道打开的系统时间精确到毫秒 加countdown计时器，计算剩余时间 计算打开嗅闻时间
            储存到数据库 TABLE 4
            */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option12);
        MyOpenHelper dbHelper = new MyOpenHelper(Option20Activity.this);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);
        ImageView image4 = (ImageView) findViewById(R.id.image4);
        TextView text1 = (TextView) findViewById(R.id.text1);
        TextView text2 = (TextView) findViewById(R.id.text2);
        TextView text3 = (TextView) findViewById(R.id.text3);
        TextView text4 = (TextView) findViewById(R.id.text4);
        int accept2 = getIntent().getIntExtra("send2", 0);
        int index1, index2, index3, index4;
        textView = (TextView) findViewById(R.id.textview);
        textView.setText("第" + (accept2 + 1) + "题");
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1);
        Cursor cursor = sqliteDatabase.rawQuery(sql, null); //Cursor 是每行的集合 定位到
        try {
            cursor.moveToFirst(); //使用 moveToFirst() 定位第一行 读取数据库里存贮的choose表格数据，显示在界面上
            text1.setText(cursor.getString(cursor.getColumnIndex("target")));
            text2.setText(cursor.getString(cursor.getColumnIndex("error01")));
            text3.setText(cursor.getString(cursor.getColumnIndex("error02")));
            text4.setText(cursor.getString(cursor.getColumnIndex("error03")));
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
                //读取数据库存储的图片
                image1.setImageDrawable(getDrawable().get(index1 - 1));
                image2.setImageDrawable(getDrawable().get(index2 - 1));
                image3.setImageDrawable(getDrawable().get(index3 - 1));
                image4.setImageDrawable(getDrawable().get(index4 - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();//关闭游标 释放资源
        String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        Cursor cursor1 = sqliteDatabase.rawQuery(sql1, null);
        cursor1.moveToFirst();
        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
        String name = cursor1.getString(cursor1.getColumnIndex("name"));
        String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        String age = cursor1.getString(cursor1.getColumnIndex("age"));
        cursor1.close();
        image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("target", text1.getText().toString());
                cv.put("error01", text2.getText().toString());
                cv.put("error02", text3.getText().toString());
                cv.put("error03", text4.getText().toString());
                cv.put("answer", text1.getText().toString());
                cv.put("answerTime", sdf.format(date));
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (accept2 == 19) {
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where target=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    ContentValues cv1 = new ContentValues();
                    if (count >= 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else if (count >= 15 && count < 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    }
                    cursor.close();
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, "谢谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
/*                LinearLayout page1=(LinearLayout) findViewById(R.id.page1);
                RelativeLayout page2=(RelativeLayout) findViewById(R.id.page2);
                page1.setAlpha((float) 0.0);
                page2.setVisibility(View.VISIBLE);
                page2.setOnClickListener(null);
                myImageView=(ImageView)findViewById(R.id.img_loading);
                myImageView.setImageResource(R.drawable.loading);
                myAlphaAnimation=new RotateAnimation(0f, 360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);//设置图片动画属性，各参数说明可参照api
                myAlphaAnimation.setRepeatCount(1);
                myAlphaAnimation.setDuration(200);
                myAlphaAnimation.setInterpolator(new LinearInterpolator());
                myImageView.setAnimation(myAlphaAnimation);
                myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // TODO Auto-generated method stub
                        Intent intent = getIntent();
                        intent.putExtra("send3",accept2+1);
                        setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                        finish();//此处一定要调用finish()方法
                    }
                });*/
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
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
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("target", text1.getText().toString());
                cv.put("error01", text2.getText().toString());
                cv.put("error02", text3.getText().toString());
                cv.put("error03", text4.getText().toString());
                cv.put("answer", text2.getText().toString());
                cv.put("answerTime", sdf.format(date));
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (accept2 == 19) {
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where target=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    ContentValues cv1 = new ContentValues();
                    if (count >= 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else if (count >= 15 && count < 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    }
                    cursor.close();
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
/*                    LinearLayout page1=(LinearLayout) findViewById(R.id.page1);
                    RelativeLayout page2=(RelativeLayout) findViewById(R.id.page2);
                    page1.setAlpha((float) 0.0);
                    page2.setVisibility(View.VISIBLE);
                    page2.setOnClickListener(null);
                    myImageView=(ImageView)findViewById(R.id.img_loading);
                    myImageView.setImageResource(R.drawable.loading);
                    myAlphaAnimation=new RotateAnimation(0f, 360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);//设置图片动画属性，各参数说明可参照api
                    myAlphaAnimation.setRepeatCount(1);
                    myAlphaAnimation.setDuration(200);
                    myAlphaAnimation.setInterpolator(new LinearInterpolator());
                    myImageView.setAnimation(myAlphaAnimation);
                    myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation arg0) {
                            // TODO Auto-generated method stub
                        }
                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                            // TODO Auto-generated method stub
                        }
                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            // TODO Auto-generated method stub
                            Intent intent = getIntent();
                            intent.putExtra("send3",accept2+1);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });*/
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
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
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("target", text1.getText().toString());
                cv.put("error01", text2.getText().toString());
                cv.put("error02", text3.getText().toString());
                cv.put("error03", text4.getText().toString());
                cv.put("answer", text3.getText().toString());
                cv.put("answerTime", sdf.format(date));
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (accept2 == 19) {
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where target=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    ContentValues cv1 = new ContentValues();
                    if (count >= 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else if (count >= 15 && count < 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    }
                    cursor.close();
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss：SSS");
                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                cv.put("name", name);
                cv.put("sex", sex);
                cv.put("age", age);
                cv.put("target", text1.getText().toString());
                cv.put("error01", text2.getText().toString());
                cv.put("error02", text3.getText().toString());
                cv.put("error03", text4.getText().toString());
                cv.put("answer", text4.getText().toString());
                cv.put("answerTime", sdf.format(date));
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (accept2 == 19) {
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where target=answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    ContentValues cv1 = new ContentValues();
                    if (count >= 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else if (count >= 15 && count < 18) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                    }
                    cursor.close();
                    AlertDialog dialog = new AlertDialog.Builder(Option20Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option20Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option20Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                //关闭数据库
                sqliteDatabase.close();
            }
        }));
    }

    @Override
    public void onBackPressed() {
// 这里处理逻辑代码，该方法仅适用于2.0或更新版的sdk
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
