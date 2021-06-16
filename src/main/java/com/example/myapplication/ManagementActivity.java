package com.example.myapplication;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.ManagementInfo;
import com.example.myapplication.db.MyOpenHelper;

import java.util.ArrayList;

public class ManagementActivity extends Activity {
    private MyOpenHelper moh;
    private SQLiteDatabase sd;
    private ArrayList<ManagementInfo> resultList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        //创建或打开数据库
        moh = new MyOpenHelper(this);
        sd = moh.getReadableDatabase();
        resultList = new ArrayList<>();
        //扫描数据库,将数据库信息放入resultList
        Cursor cursor = sd.rawQuery("select * from " + Constants.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String ID = cursor.getString(cursor.getColumnIndex("ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String age = cursor.getString(cursor.getColumnIndex("age"));
            // String medicalHistory = cursor.getString(cursor.getColumnIndex("medicalHistory"));
            String gender = cursor.getString(cursor.getColumnIndex("gender"));
            String result = cursor.getString(cursor.getColumnIndex("result"));
            ManagementInfo ri = new ManagementInfo(ID, name, age, gender, result);    //ResultInfo存一个条目的数据
            resultList.add(ri);//把数据库的每一行加入数组中
        }
        //获取ListView,并通过Adapter把resultList的信息显示到ListView
        //为ListView设置一个适配器,getCount()返回数据个数;getView()为每一行设置一个条目
        lv = (ListView) findViewById(R.id.result_lv);
        lv.setEmptyView(findViewById(R.id.isEmpty2));
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return resultList.size();
            }

            //ListView的每一个条目都是一个view对象
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                //对ListView的优化，convertView为空时，创建一个新视图；convertView不为空时，代表它是滚出
                //屏幕，放入Recycler中的视图,若需要用到其他layout，则用inflate(),同一视图，用findViewBy()
                if (convertView == null) {
                    view = View.inflate(getBaseContext(), R.layout.activity_management, null);
                } else {
                    view = convertView;
                }
                //从resultList中取出一行数据，position相当于数组下标,可以实现逐行取数据
                ManagementInfo ri = resultList.get(position);
                TextView ID = (TextView) view.findViewById(R.id.ID);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView age = (TextView) view.findViewById(R.id.age);
                TextView gender = (TextView) view.findViewById(R.id.gender);
                TextView result = (TextView) view.findViewById(R.id.result);
                ID.setText(ri.getID());
                name.setText(ri.getName());
                age.setText(ri.getAge());
                gender.setText(ri.getGender());
                result.setText(ri.getResult());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog dialog = new MaterialDialog(ManagementActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
                        dialog.title(R.string.remind, null);
                        dialog.message(R.string.remind2, null, null);
                        dialog.positiveButton(R.string.remind3, null, materialDialog -> {
                            dialog.dismiss();
                            return null;
                        });
                        dialog.show();
                        //Toast.makeText(ManagementActivity.this, "请前往后台管理页面查看详细信息", Toast.LENGTH_SHORT).show();
                    }
                });
                return view;
            }


            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }
        });
    }
}
