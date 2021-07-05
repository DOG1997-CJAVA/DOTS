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
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class Option12Activity extends Activity {

    private int count;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option12);
        //创建SQLiteOpenHelper子类对象
        //注意，一定要传入最新的数据库版本号
        MyOpenHelper dbHelper = new MyOpenHelper(Option12Activity.this);
        //调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);
        ImageView image4 = (ImageView) findViewById(R.id.image4);
        TextView text1 = (TextView) findViewById(R.id.text1);
        TextView text2 = (TextView) findViewById(R.id.text2);
        TextView text3 = (TextView) findViewById(R.id.text3);
        TextView text4 = (TextView) findViewById(R.id.text4);
        //接收ReadyActivity页面传来的数据
        String odorStartTime = getIntent().getStringExtra("odorStartTime");
        String odorEndTime = getIntent().getStringExtra("odorEndTime");
        String retryCount = getIntent().getStringExtra("retryCount");
        int accept2 = getIntent().getIntExtra("send2", 0);
        int number_count2 = getIntent().getIntExtra("number_count", 0);
        int index1, index2, index3, index4;
        String correct = " ";
        textView = (TextView) findViewById(R.id.textview);
        textView.setText("第" + (number_count2 + 1) + "题");
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1);
        //执行sql语句  Cursor 是每行的集合 使用 moveToFirst() 定位第一行
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();//若打乱题目顺序，可以新建一列储存目标，前四列维持随机顺序
            //读取数据库存储的图片名称
            text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
            correct = cursor.getString(cursor.getColumnIndex("correct"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取图片的数量
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
        //查询数据库用户没测试完成的信息
        String sql1 = "select *   from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        Cursor cursor1 = sqliteDatabase.rawQuery(sql1, null);
        cursor1.moveToFirst();
        //获取表中列名为“ID”的字段
        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
        String name = cursor1.getString(cursor1.getColumnIndex("name"));
        String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        String age = cursor1.getString(cursor1.getColumnIndex("age"));
        //关闭查询源
        cursor1.close();
        //设置图片1点击事件
        String finalCorrect = correct;
        image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                //获取当前日期
                Date date = new Date();
                //日期标准化
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // TODO Auto-generated method stub
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text1.getText().toString());//点击第一张图片，就是answer
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                //判断答题数量是否为12
                if (number_count2 == 11) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where answer=correct_answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "12");
                    if (count >= 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 7 && count < 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    //弹出对话框
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option12Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);//accept2+1
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        image2.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text2.getText().toString());//点击第一张图片，就是answer
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 11) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where answer=correct_answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "12");
                    if (count >= 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 7 && count < 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option12Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        image3.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text3.getText().toString());//点击第一张图片，就是answer
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 11) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where answer=correct_answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "12");
                    if (count >= 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 7 && count < 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option12Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
 /*                   LinearLayout page1 = (LinearLayout) findViewById(R.id.page1);
                    RelativeLayout page2 = (RelativeLayout) findViewById(R.id.page2);
                    page1.setAlpha((float) 0.0);
                    page2.setVisibility(View.VISIBLE);
                    page2.setOnClickListener(null);  //只需如此设置，即可达到效果
                    myImageView = (ImageView) findViewById(R.id.img_loading);//取到imageview控件
                    myImageView.setImageResource(R.drawable.loading);//设置要显示的图片
                    myAlphaAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置图片动画属性，各参数说明可参照api
                    myAlphaAnimation.setRepeatCount(1);//设置旋转重复次数，即转几圈
                    myAlphaAnimation.setDuration(200);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
                    myAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
                    myImageView.setAnimation(myAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
                    myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {    //设置动画监听事件
                        @Override
                        public void onAnimationStart(Animation arg0) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                            // TODO Auto-generated method stub
                        }

                        //图片旋转结束后触发事件，这里启动新的activity
                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            // TODO Auto-generated method stub
                            Intent intent = getIntent();
                            intent.putExtra("send3", accept2 + 1);
                            setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                            finish();//此处一定要调用finish()方法
                        }
                    });*/
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
                sqliteDatabase.close();
            }
        }));
        image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", text1.getText().toString());
                cv.put("option2", text2.getText().toString());
                cv.put("option3", text3.getText().toString());
                cv.put("option4", text4.getText().toString());
                cv.put("answer", text4.getText().toString());//点击第一张图片，就是answer
                cv.put("responTime", sdf.format(date));
                cv.put("odorStartTime", odorStartTime);
                cv.put("odorEndTime", odorEndTime);
                cv.put("retryCount", retryCount);
                sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv);
                if (number_count2 == 11) {
                    ContentValues cv1 = new ContentValues();
                    String sql = "select count(*) from " + Constants.TABLE_NAME4 + " where answer=correct_answer and ID=" + id;
                    Cursor cursor = sqliteDatabase.rawQuery(sql, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cv1.put("answercount",count + "/" + "12");
                    if (count >= 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能正常");
                    } else if (count >= 7 && count < 10) {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能障碍");
                    } else {
                        String sql2 = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='12项气味测试' where ID=" + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result","嗅觉功能丧失");
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("测试完成")//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, "感谢您的使用", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option12Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
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
                        MyOpenHelper moh = new MyOpenHelper(Option12Activity.this);
                        SQLiteDatabase sd = moh.getReadableDatabase();
                        String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                        Cursor cursor1 = sd.rawQuery(sql1, null);
                        cursor1.moveToFirst();
                        String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                        cursor1.close();
                        String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                        sd.execSQL(sql);
                        sd.close();
                        Intent intent = new Intent(Option12Activity.this, MainActivity.class);
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
