package com.example.myapplication.utils.options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.myapplication.MainActivity;
import com.example.myapplication.OnDoubleClickListener;
import com.example.myapplication.R;
import com.example.myapplication.TabActivity;
import com.example.myapplication.databinding.ActivityOption12Binding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.LocaleManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Option12Activity extends Activity {

    private ActivityOption12Binding bindingOpt12;
    private static final String TAG = "Option12Activity";
    public static final String sql2_CN_NO = "update " + Constants.TABLE_NAME + " set result='嗅觉功能正常' , test_channel='12项气味测试' where ID=";
    public static final String sql2_EN_NO = "update " + Constants.TABLE_NAME + " set result='NORMOSMIA' , test_channel='12 odor tests' where ID=";
    public static final String sql2_CN_MI = "update " + Constants.TABLE_NAME + " set result='嗅觉功能障碍' , test_channel='12项气味测试' where ID=";
    public static final String sql2_EN_MI = "update " + Constants.TABLE_NAME + " set result='MICROSMIA' , test_channel='12 odor tests' where ID=";
    public static final String sql2_CN_AS = "update " + Constants.TABLE_NAME + " set result='嗅觉功能丧失' , test_channel='12项气味测试' where ID=";
    public static final String sql2_EN_AS = "update " + Constants.TABLE_NAME + " set result='ANOSMIA' , test_channel='12 odor tests' where ID=";
    public static String sql_base = sql2_CN_NO;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingOpt12 = ActivityOption12Binding.inflate(getLayoutInflater());
        setContentView(bindingOpt12.getRoot());
        //创建SQLiteOpenHelper子类对象
        //注意，一定要传入最新的数据库版本号
        MyOpenHelper dbHelper = new MyOpenHelper(Option12Activity.this);
        //调用getWritableDatabase()方法创建或打开一个可以读的数据库
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        //接收ReadyActivity页面传来的数据
        SharedPreferences language_set = getSharedPreferences("retryCount", Context.MODE_PRIVATE);
        int language_index = language_set.getInt("language_set", 0);
        Log.d(TAG,"****************************************");
        Log.d(TAG,Integer.toString(language_index));
        Log.d(TAG,"****************************************");

        String odorStartTime = getIntent().getStringExtra("odorStartTime");
        String odorEndTime = getIntent().getStringExtra("odorEndTime");
        String retryCount = getIntent().getStringExtra("retryCount");
        int accept2 = getIntent().getIntExtra("send2", 0);//题目计数 答题返回计数 由number_count2代替 accept2只负责取数据库题目

        if(language_index == 1){//递增40行取相应语言的描述词
            accept2 = accept2 + 40;
        }
        int number_count2 = getIntent().getIntExtra("number_count", 0);
        int index1, index2, index3, index4;
        String correct = " ";
        String tempSrt= "第" + (number_count2 + 1) + "题";
        bindingOpt12.textview.setText(tempSrt);
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1);

        //执行sql语句  Cursor 是每行的集合 使用 moveToFirst() 定位第一行
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();//若打乱题目顺序，可以新建一列储存目标，前四列维持随机顺序
            //读取数据库存储的图片名称
            bindingOpt12.text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            bindingOpt12.text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            bindingOpt12.text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            bindingOpt12.text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
            correct = cursor.getString(cursor.getColumnIndex("correct"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取图片的数量
        int count = getDrawable().size();
        if (count != 0) {
            try {
                index1 = cursor.getInt(cursor.getColumnIndex("index01"));
                index2 = cursor.getInt(cursor.getColumnIndex("index02"));
                index3 = cursor.getInt(cursor.getColumnIndex("index03"));
                index4 = cursor.getInt(cursor.getColumnIndex("index04"));
                bindingOpt12.image1.setImageDrawable(getDrawable().get(index1 - 1));
                bindingOpt12.image2.setImageDrawable(getDrawable().get(index2 - 1));
                bindingOpt12.image3.setImageDrawable(getDrawable().get(index3 - 1));
                bindingOpt12.image4.setImageDrawable(getDrawable().get(index4 - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        //查询数据库用户没测试完成的信息
        String sql1 = "select *  from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        Cursor cursor1 = sqliteDatabase.rawQuery(sql1, null);
        if(!cursor1.moveToFirst()) {
            Log.e(TAG, "moveToPosition return fails, maybe table not created!!!");
        }
            //获取表中列名为“ID”的字段
            String id = cursor1.getString(cursor1.getColumnIndex("ID"));
            String name = cursor1.getString(cursor1.getColumnIndex("name"));
            String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
            String age = cursor1.getString(cursor1.getColumnIndex("age"));
            //关闭查询源
            cursor1.close();
            //设置图片1点击事件
            String finalCorrect = correct;


        bindingOpt12.image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                //获取当前日期
                Date date = new Date();
                //日期标准化
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                // TODO Auto-generated method stub
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt12.text1.getText().toString());
                cv.put("option2", bindingOpt12.text2.getText().toString());
                cv.put("option3", bindingOpt12.text3.getText().toString());
                cv.put("option4", bindingOpt12.text4.getText().toString());
                cv.put("answer", bindingOpt12.text1.getText().toString());//点击第一张图片，就是answer
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
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_NO;
                            dbHelper.importSheet(sqliteDatabase,Option12Activity.this);
                        } else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 7) {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_MI;
                        } else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_AS;
                        } else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
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
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option12Activity.this, TabActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", number_count2 + 1);//number_count2 + 1
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();
                }
                sqliteDatabase.close();
            }
        }));
        bindingOpt12.image2.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt12.text1.getText().toString());
                cv.put("option2", bindingOpt12.text2.getText().toString());
                cv.put("option3", bindingOpt12.text3.getText().toString());
                cv.put("option4", bindingOpt12.text4.getText().toString());
                cv.put("answer", bindingOpt12.text2.getText().toString());
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
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_NO;
                        } else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 7) {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_MI;
                        } else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_AS;
                        } else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
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
        bindingOpt12.image3.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt12.text1.getText().toString());
                cv.put("option2", bindingOpt12.text2.getText().toString());
                cv.put("option3", bindingOpt12.text3.getText().toString());
                cv.put("option4", bindingOpt12.text4.getText().toString());
                cv.put("answer", bindingOpt12.text3.getText().toString());
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
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_NO;
                        } else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 7) {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_MI;
                        } else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_AS;
                        } else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
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
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        //图片旋转结束后触发事件，这里启动新的activity
                        @Override
                        public void onAnimationEnd(Animation arg0) {
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
        bindingOpt12.image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                ContentValues cv = new ContentValues();
                if((number_count2+1) == 1){
                    cv.put("name", name);
                    cv.put("sex", sex);
                    cv.put("age", age);
                }
                cv.put("ID", id);
                cv.put("correct_answer", finalCorrect);
                cv.put("option1", bindingOpt12.text1.getText().toString());
                cv.put("option2", bindingOpt12.text2.getText().toString());
                cv.put("option3", bindingOpt12.text3.getText().toString());
                cv.put("option4", bindingOpt12.text4.getText().toString());
                cv.put("answer", bindingOpt12.text4.getText().toString());
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
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_NO;
                        } else {
                            sql_base = sql2_EN_NO;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_normal_ifo));
                    } else if (count >= 7) {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_MI;
                        } else {
                            sql_base = sql2_EN_MI;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_mid_ifo));
                    } else {
                        if (language_set.getInt("language_set", 0) == 0) {
                            sql_base = sql2_CN_AS;
                        } else {
                            sql_base = sql2_EN_AS;
                        }
                        String sql2 = sql_base + id;
                        sqliteDatabase.execSQL(sql2);
                        cv1.put("result",getString(R.string.result_as_ifo));
                    }
                    cursor.close();
                    cv1.put("ID", id);
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    cv1.put("result"," ");//添加一行空行
                    cv1.put("answercount"," ");
                    sqliteDatabase.insert(Constants.TABLE_NAME4, null, cv1);
                    AlertDialog dialog = new AlertDialog.Builder(Option12Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle(getString(R.string.remind))//设置对话框的标题
                            .setMessage(getString(R.string.finish_quit_remind))//设置对话框的内容
                            .setPositiveButton(getString(R.string.remind3), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option12Activity.this, getString(R.string.finish_quit_remind), Toast.LENGTH_SHORT).show();
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
                BitmapDrawable bitmapDrawable1 = new BitmapDrawable(getResources(),bitmap1);
                Drawable drawable1 = bitmapDrawable1;
                drawables.add(drawable1);
            }
        }
        assert c != null;//Cursor必须要close()回收
        c.close();
        return drawables;
    }


}
