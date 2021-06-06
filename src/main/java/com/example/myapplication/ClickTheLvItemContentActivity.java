package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.Constants;
import com.example.myapplication.db.ManagementInfo;
import com.example.myapplication.db.MyOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClickTheLvItemContentActivity extends AppCompatActivity implements View.OnClickListener {
    private MyOpenHelper moh;
    private SQLiteDatabase sd;
    private List<String> dataList;
    private ClickItemContentAdapter adapter;
    private TextView  tv_title;
    private String ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_item_content);
        //创建或打开数据库
        moh=new MyOpenHelper(this);
        sd= moh.getReadableDatabase();
        //删除result为'默认'的字段，否则结果详情页面会出错
        String sql = "delete from "+Constants.TABLE_NAME+" where result="+"'默认'";
        sd.execSQL(sql);
        //初始化数据
        dataList = new ArrayList<>();
        //扫描数据库,将数据库信息放入resultList
        Cursor cursor = sd.rawQuery("select ID,name,result from "+ Constants.TABLE_NAME ,null);
        while (cursor.moveToNext()){
            String ID = cursor.getString(cursor.getColumnIndex("ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String result = cursor.getString(cursor.getColumnIndex("result"));
            dataList.add("ID:"+ID+" "+"姓名:"+name+" "+"结果:"+result);
        }
        cursor.close();
        sd.close();
        //获取lv 并设置适配器
        ListView listView = (ListView) findViewById(R.id.lv_clickItemContent);
        //创建适配器，传递数据集合，以及条目中被点击控件的的点击监听
        adapter = new ClickItemContentAdapter(ClickTheLvItemContentActivity.this, dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ClickTheLvItemContentActivity.this, position + "号位置的条目被点击", Toast.LENGTH_SHORT).show();
                if (view != null) {
                    String idNumber = (dataList.get(position)+"").substring(3,7);
                    //Toast.makeText(view.getContext(), idNumber, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ClickTheLvItemContentActivity.this,ResultDetailActivity.class);
                    intent.putExtra("ID",idNumber);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_del:   //lv条目中 iv_del
                final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转

                //点击删除按钮之后，给出dialog提示
                AlertDialog.Builder builder = new AlertDialog.Builder(ClickTheLvItemContentActivity.this);
                builder.setTitle( "确认删除?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {//调用新加的delect类进行删除操作
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idNumber = (dataList.get(position)+"").substring(3,7);
                        Map map = moh.deleteFromDdById(idNumber);//
                        if(map.get("result1")!=null&&map.get("result2")!=null){
                            Toast.makeText(ClickTheLvItemContentActivity.this,"已成功删除数据",Toast.LENGTH_SHORT).show();
                        }
                        dataList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                break;
        }
    }
}
