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

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;

public class Option2Activity extends Activity {

    private int count;
    //声明Animation类的对象
    private Animation myAlphaAnimation;
    private ImageView myImageView;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option12);//改变此处指定它的 答题界面 样式
        MyOpenHelper dbHelper = new MyOpenHelper(Option2Activity.this);
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
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();
            text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
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
        sqliteDatabase.close();
        image1.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (accept2 + 1 == 2) {
                    //弹出对话框
                    AlertDialog dialog = new AlertDialog.Builder(Option2Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("模拟测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option2Activity.this, "模拟测试完成", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option2Activity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
            }
        }));

        image2.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (accept2 + 1 == 2) {
                    //弹出对话框
                    AlertDialog dialog = new AlertDialog.Builder(Option2Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("模拟测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option2Activity.this, "模拟测试完成", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option2Activity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
            }
        }));
        image3.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (accept2 + 1 == 2) {
                    //弹出对话框
                    AlertDialog dialog = new AlertDialog.Builder(Option2Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("模拟测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option2Activity.this, "模拟测试完成", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option2Activity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
            }
        }));
        image4.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (accept2 + 1 == 2) {
                    //弹出对话框
                    AlertDialog dialog = new AlertDialog.Builder(Option2Activity.this)
                            .setIcon(R.mipmap.talk)//设置标题的图片
                            .setTitle("提示")//设置对话框的标题
                            .setMessage("模拟测试完成，感谢您的使用！！！")//设置对话框的内容
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Option2Activity.this, "模拟测试完成", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Option2Activity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    Intent intent = getIntent();
                    intent.putExtra("send3", accept2 + 1);
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
// 这里处理逻辑代码，该方法仅适用于2.0或更新版的sdk
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.talk)//设置标题的图片
                .setTitle("警告！")//设置对话框的标题
                .setMessage("是否要退出当前测试？退出后将不会保留任何作答结果！")//设置对话框的内容
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
                        Intent intent = new Intent(Option2Activity.this, MainActivity.class);
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
