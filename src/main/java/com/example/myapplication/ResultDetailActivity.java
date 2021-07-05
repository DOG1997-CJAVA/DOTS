package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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

import static android.content.ContentValues.TAG;


public class ResultDetailActivity extends Activity {
    private static final String TAG = "tag";
    private MyOpenHelper moh;
    private SQLiteDatabase sd;
    private List<ResultDetailInfo> resultDetailList;//ResultDetailInfo对象
    private ArrayList<String> timeLog;
    private ListView lv;
    private String ID,name,sex,age,responTime,results,answerStu,testchannel;
    private int correct = 0;
    private int raw_count_int = 1;
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
            String raw_count = raw_count_int +" ";
            ID = cursor.getString(cursor.getColumnIndex("ID"));
            responTime = cursor.getString(cursor.getColumnIndex("responTime"));
            timeLog.add(responTime);
            String option1 = raw_count + cursor.getString(cursor.getColumnIndex("option1"));//若打乱题目顺序，可以新建一列储存目标
            String option2 = cursor.getString(cursor.getColumnIndex("option2"));
            String option3 = cursor.getString(cursor.getColumnIndex("option3"));
            String option4 = cursor.getString(cursor.getColumnIndex("option4"));
            String answer = cursor.getString(cursor.getColumnIndex("answer"));
            String correct_answer = cursor.getString(cursor.getColumnIndex("correct_answer"));
            if(TextUtils.isEmpty(correct_answer) || TextUtils.isEmpty(answer)){
            }else{
                ResultDetailInfo rdi = new ResultDetailInfo(option1,option2,option3,option4,answer,correct_answer);
                resultDetailList.add(rdi);
                raw_count_int = raw_count_int + 1;
            }

            if(TextUtils.isEmpty(correct_answer) || TextUtils.isEmpty(answer)){
            }else if(answer.equals(correct_answer)){
                    correct = correct + 1;
            }
        }
        cursor.close();
        Cursor cursor1 = sd.rawQuery("select * from "+ Constants.TABLE_NAME+" where ID="+getID ,null);
        cursor1.moveToFirst();
        name = cursor1.getString(cursor1.getColumnIndex("name"));
        age = cursor1.getString(cursor1.getColumnIndex("age"));
        sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        results = cursor1.getString(cursor1.getColumnIndex("result"));
        testchannel = cursor1.getString(cursor1.getColumnIndex("test_channel"));
        cursor1.close();

        answerStu = (correct + "") + "/" + (resultDetailList.size() + "");//答题正确数目 显示
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
        TextView hzAnswerSituation = (TextView) findViewById(R.id.answercount);
        hzAnswerSituation.setText(answerStu);
        TextView test_channel = (TextView) findViewById(R.id.channelname);
        test_channel.setText(testchannel);

        try{
            //显示开始作答时间
            TextView hzStartAnswerTime = (TextView) findViewById(R.id.startTime);
            hzStartAnswerTime.setText(timeLog.get(0));
            //显示结束作答时间
            TextView hzEndAnswerTime = (TextView) findViewById(R.id.endTime);
            //hzEndAnswerTime.setText(timeLog.get(timeLog.size()-1));//最后一个时间
            hzEndAnswerTime.setText(timeLog.get(timeLog.size()-3));//最后一个时间
            //Log.d(TAG,"aaaaaaaaaaaaaaaaaaaaaaaaaa"+timeLog);
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
                TextView option1 = (TextView)view.findViewById(R.id.listview_option1);
                TextView option2 = (TextView)view.findViewById(R.id.listview_option2);
                TextView option3 = (TextView)view.findViewById(R.id.listview_option3);
                TextView option4 = (TextView)view.findViewById(R.id.listview_option4);
                TextView answer = (TextView)view.findViewById(R.id.listview_answer);
                TextView correct_answer = (TextView)view.findViewById(R.id.listview_correct_answer);
                option1.setText(ri.getOption1());
                option2.setText(ri.getOption2());
                option3.setText(ri.getOption3());
                option4.setText(ri.getOption4());
                answer.setText(ri.getAnswer());
                correct_answer.setText(ri.getCorrect_answer());
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


