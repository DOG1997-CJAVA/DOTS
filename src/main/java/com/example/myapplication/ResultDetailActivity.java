package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myapplication.databinding.ActivityResultdetailBinding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.db.ResultDetailInfo;
import com.example.myapplication.language.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/*
* 2021/7/20 代码优化 使用ViewBinding 替换所有的findviewbyID 仍使用java
 * */

public class ResultDetailActivity extends BaseActivity {
    private static final String TAG = "tag";
    private List<ResultDetailInfo> resultDetailList;//ResultDetailInfo对象
    private String ID,answer,correct_answer;
    private int correct = 0;
    private int raw_count_int = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.myapplication.databinding.ActivityResultdetailBinding mbinding1 = ActivityResultdetailBinding.inflate(getLayoutInflater());
        setContentView(mbinding1.getRoot());
        Intent intent = getIntent();
        String getID = intent.getStringExtra("ID");
        //创建或打开数据库
        MyOpenHelper moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        resultDetailList = new ArrayList<>();
        ArrayList<String> timeLog = new ArrayList<>();
        Cursor cursor = sd.rawQuery("select * from "+ Constants.TABLE_NAME4+" where ID="+getID ,null);
        while (cursor.moveToNext()){
            String raw_count = raw_count_int +" ";
            ID = cursor.getString(cursor.getColumnIndex("ID"));
            String responTime = cursor.getString(cursor.getColumnIndex("responTime"));
            timeLog.add(responTime);
            String option1 = raw_count + cursor.getString(cursor.getColumnIndex("option1"));//若打乱题目顺序，可以新建一列储存目标
            //String option1 = cursor.getString(cursor.getColumnIndex("option1"));
            String option2 = cursor.getString(cursor.getColumnIndex("option2"));
            String option3 = cursor.getString(cursor.getColumnIndex("option3"));
            String option4 = cursor.getString(cursor.getColumnIndex("option4"));
            String answer = cursor.getString(cursor.getColumnIndex("answer"));
            String correct_answer = cursor.getString(cursor.getColumnIndex("correct_answer"));
            if(TextUtils.isEmpty(correct_answer) || TextUtils.isEmpty(answer)){
                Log.d(TAG," do noting");
            }else{
                ResultDetailInfo rdi = new ResultDetailInfo(option1,option2,option3,option4,answer,correct_answer);
                resultDetailList.add(rdi);
                raw_count_int = raw_count_int + 1;
                if(answer.trim().equals(correct_answer.trim())){
                    correct = correct + 1;
                }
            }
        }
        cursor.close();
        Cursor cursor1 = sd.rawQuery("select * from "+ Constants.TABLE_NAME+" where ID="+getID ,null);
        cursor1.moveToFirst();
        String name = cursor1.getString(cursor1.getColumnIndex("name"));
        String age = cursor1.getString(cursor1.getColumnIndex("age"));
        String sex = cursor1.getString(cursor1.getColumnIndex("gender"));
        //String results = cursor1.getString(cursor1.getColumnIndex("result"));
        String educate = cursor1.getString(cursor1.getColumnIndex("educate"));
        String testchannel = cursor1.getString(cursor1.getColumnIndex("test_channel"));
        cursor1.close();

        String answerStu = (correct + "") + "/" + (resultDetailList.size() + "");//答题正确数目 显示
        Log.d(TAG, "onCreate: "+ answerStu);

        mbinding1.textView11.setVisibility(View.INVISIBLE);//暂时不显示不准确的数据
        mbinding1.ID2.setText(ID);
        mbinding1.name.setText(name);
        mbinding1.sex.setText(sex);
        mbinding1.age.setText(age);
        //mbinding1.result1.setText(results);
        mbinding1.answercount.setText(answerStu);
        mbinding1.channelname.setText(testchannel);
        mbinding1.educate.setText(educate);
        try{
            mbinding1.startTime.setText(timeLog.get(0));
            mbinding1.endTime.setText(timeLog.get(timeLog.size()-3));//最后一个时间
        }catch (Exception e){
            e.printStackTrace();
        }
        //获取ListView,并通过Adapter把resultList的信息显示到ListView
        //为ListView设置一个适配器,getCount()返回数据个数;getView()为每一行设置一个条目
        mbinding1.resultdetailLv.setAdapter(new BaseAdapter() {
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
                ResultDetailInfo ri = resultDetailList.get(position); //从resultList中取出一行数据，position相当于数组下标,可以实现逐行取数据
                TextView listviewOption1 = view.findViewById(R.id.listview_option1);
                listviewOption1.setText(ri.getOption1());
                TextView listviewOption2 = view.findViewById(R.id.listview_option2);
                listviewOption2.setText(ri.getOption2());
                TextView listviewOption3 = view.findViewById(R.id.listview_option3);
                listviewOption3.setText(ri.getOption3());
                TextView listviewOption4 = view.findViewById(R.id.listview_option4);
                listviewOption4.setText(ri.getOption4());
                TextView listviewAnswer = view.findViewById(R.id.listview_answer);
                listviewAnswer.setText(ri.getAnswer());
                TextView listviewCorrectAnswer = view.findViewById(R.id.listview_correct_answer);
                listviewCorrectAnswer.setText(ri.getCorrect_answer());
                listviewCorrectAnswer.setTextColor(getResources().getColor(R.color.color_green));
//                if ( (!TextUtils.isEmpty(ri.getCorrect_answer())) && (!TextUtils.isEmpty(ri.getAnswer())) && (ri.getAnswer().trim()).equals(ri.getCorrect_answer().trim())) {
//                    listviewAnswer.setTextColor(getResources().getColor(R.color.color_green));
//                }
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


