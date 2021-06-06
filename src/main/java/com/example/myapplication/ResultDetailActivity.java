package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.db.ResultDetailInfo;

import java.util.ArrayList;
import java.util.List;


public class ResultDetailActivity extends Activity {
    private static final String TAG = "tag";
    private MyOpenHelper moh;
    private SQLiteDatabase sd;
    //存放用户作答详细结果的List集合
    private List<ResultDetailInfo> resultDetailList;//n行ResultDetailInfo对象
    //存放时间
    private ArrayList<String> timeLog;
    private ListView lv;
    private String ID,name,sex,age,answerTime,results,answerStu;
    private int correct = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultdetail);
        Intent intent = getIntent();
        String getID = intent.getStringExtra("ID");
        //创建或打开数据库
        moh=new MyOpenHelper(this);
        sd= moh.getReadableDatabase();
        resultDetailList = new ArrayList<>();
        timeLog = new ArrayList<String>();
        //扫描数据库,将数据库信息放入resultList
        Cursor cursor = sd.rawQuery("select * from "+ Constants.TABLE_NAME4+" where ID="+getID ,null);
        while (cursor.moveToNext()){
            ID = cursor.getString(cursor.getColumnIndex("ID"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            sex = cursor.getString(cursor.getColumnIndex("sex"));
            age = cursor.getString(cursor.getColumnIndex("age"));
            answerTime = cursor.getString(cursor.getColumnIndex("answerTime"));
            timeLog.add(answerTime);
            String target = cursor.getString(cursor.getColumnIndex("target"));//若打乱题目顺序，可以新建一列储存目标
            String error01 = cursor.getString(cursor.getColumnIndex("error01"));
            String error02 = cursor.getString(cursor.getColumnIndex("error02"));
            String error03 = cursor.getString(cursor.getColumnIndex("error03"));
            String answer = cursor.getString(cursor.getColumnIndex("answer"));
           //String answer_time = cursor.getString(cursor.getColumnIndex("answerTime"));
            ResultDetailInfo rdi = new ResultDetailInfo(target,error01,error02,error03,answer,answerTime);
            resultDetailList.add(rdi);//把数据库的每一行加入数组中
            if(target.equals(answer)){correct = correct + 1;}
        }
        cursor.close();

        Cursor cursor1 = sd.rawQuery("select result from "+ Constants.TABLE_NAME+" where ID="+getID ,null);
        cursor1.moveToFirst();
        results = cursor1.getString(cursor1.getColumnIndex("result"));
        cursor1.close();

        answerStu = (correct + "") + "/" + (resultDetailList.size() + "");
        Log.d(TAG, "onCreate: "+ answerStu);

        TextView hzID = (TextView) findViewById(R.id.ID2);
        hzID.setText(ID);
        TextView hzName = (TextView) findViewById(R.id.name);
        hzName.setText(name);
        TextView hzSex = (TextView) findViewById(R.id.sex);
        hzSex.setText(sex);
        TextView hzAge = (TextView) findViewById(R.id.age);
        hzAge.setText(age);
        TextView hzResult = (TextView) findViewById(R.id.result1);
        hzResult.setText(results);
        TextView hzAnswerSituation = (TextView) findViewById(R.id.answer1);
        hzAnswerSituation.setText(answerStu);

        try{
            //显示开始作答时间
            TextView hzStartAnswerTime = (TextView) findViewById(R.id.startTime);
            hzStartAnswerTime.setText(timeLog.get(0));
            //显示结束作答时间
            TextView hzEndAnswerTime = (TextView) findViewById(R.id.endTime);
            hzEndAnswerTime.setText(timeLog.get(timeLog.size()-1));//最后一个时间
        }catch (Exception e){
            e.printStackTrace();
        }
        //获取ListView,并通过Adapter把resultList的信息显示到ListView
        //为ListView设置一个适配器,getCount()返回数据个数;getView()为每一行设置一个条目
        lv = (ListView)findViewById(R.id.resultdetail_lv);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return resultDetailList.size();
            }
            //ListView的每一个条目都是一个view对象 记得更改activity_listview的子对象,调用其子对象布局
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                //对ListView的优化，convertView为空时，创建一个新视图；convertView不为空时，代表它是滚出
                //屏幕，放入Recycler中的视图,若需要用到其他layout，则用inflate(),同一视图，用findViewBy()
                if(convertView==null){
                    //将布局填充成View对象
                    view = View.inflate(getBaseContext(),R.layout.activity_listview,null);
                }
                else{
                    view = convertView;
                }
                //从resultList中取出一行数据，position相当于数组下标,可以实现逐行取数据
                ResultDetailInfo ri = resultDetailList.get(position);
                TextView target = (TextView)view.findViewById(R.id.target);
                TextView error01 = (TextView)view.findViewById(R.id.error01);
                TextView error02 = (TextView)view.findViewById(R.id.error02);
                TextView error03 = (TextView)view.findViewById(R.id.error03);
                TextView answer = (TextView)view.findViewById(R.id.answer);
                TextView answer_time = (TextView)view.findViewById(R.id.list_answer_time);
                target.setText(ri.getTarget());
                error01.setText(ri.getError01());
                error02.setText(ri.getError02());
                error03.setText(ri.getError03());
                answer.setText(ri.getAnswer());
                answer_time.setText(ri.getAnswerTime());
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


