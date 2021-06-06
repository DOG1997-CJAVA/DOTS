package com.example.myapplication.utils.options;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;

public class OptionOneActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView image1,image2,image3,image4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionone);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        image4 = (ImageView)findViewById(R.id.image4);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//获取OptionOneActivity中LayoutInflater （上下文参数）
        // LayoutInflater factorys = LayoutInflater.from(OptionOneActivity.this);
        //获取View 对象
//        View view = factorys.inflate(R.layout.activity_login, null);
//        //获取 TextView 控件
//        EditText editText = (EditText) view.findViewById(R.id.editTextPhoneNum);
//        String phoneNumber = editText.getText().toString();
//        phoneNumber.substring(phoneNumber.length()-7,phoneNumber.length());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);//设置图标
        builder.setTitle("提示");
        builder.setMessage("确定选择该项吗？");//设置对话框的内容
        switch (v.getId()){
            case R.id.image1:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                        TextView textView1=(TextView)findViewById(R.id.text1);
//                        String option=textView1.getText().toString();

                        // 创建SQLiteOpenHelper子类对象
                        ////注意，一定要传入最新的数据库版本号
                        MyOpenHelper dbHelper1 = new MyOpenHelper(OptionOneActivity.this);
                        // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
                        SQLiteDatabase sqliteDatabase1 = dbHelper1.getWritableDatabase();

                        // 创建ContentValues对象
                        ContentValues values1 = new ContentValues();

                        // 向该对象中插入键值对
                        values1.put("ID", "0737");
                        values1.put("name", "张三");
                        values1.put("age","23");
                        values1.put("medicalHistory","后天嗅觉缺失");
                        values1.put("gender","男");

                        // 调用insert()方法将数据插入到数据库当中
                        sqliteDatabase1.insert(Constants.TABLE_NAME, null, values1);

                        // sqliteDatabase.execSQL("insert into user (id,name) values (1,'carson')");
                        //关闭数据库
                        sqliteDatabase1.close();
                        Toast.makeText(OptionOneActivity.this, "1通道测试完成", Toast.LENGTH_SHORT).show();
//                        //Intent intent = new Intent(OptionOneActivity.this, Channel4Activity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog b1=builder.create();
                b1.show();  //必须show一下才能看到对话框，跟Toast一样的道理
                break;
            case  R.id.image2:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "1通道测试完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog b2=builder.create();
                b2.show();  //必须show一下才能看到对话框，跟Toast一样的道理
                break;
            case  R.id.image3:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "1通道测试完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog b3=builder.create();
                b3.show();  //必须show一下才能看到对话框，跟Toast一样的道理
                break;
            case  R.id.image4:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Toast.makeText(OptionOneActivity.this, "1通道测试完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(OptionOneActivity.this, "",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog b4=builder.create();
                b4.show();  //必须show一下才能看到对话框，跟Toast一样的道理
                break;
            default:
                break;
        }

    }
}
