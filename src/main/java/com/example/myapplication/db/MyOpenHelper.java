package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.content.ContentValues.TAG;

public class MyOpenHelper extends SQLiteOpenHelper {

    public static int language_index = 0;
    public static class PictureColumns implements BaseColumns {
        public static final String PICTURE = "picture";
    }

    private Context mContext;
    //构造方法，new时会调用，使用常量预定义部分数据库名称
    public MyOpenHelper(Context context){
        super(context,Constants.DATABASE_NAME,null,Constants.VERSION_CODE);
        this.mContext = context;//上下文
    }
    //数据库创建时，此方法会调用
    //创建数据库的同时创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建时的回调  TABLE_NAME4为主要的
        Log.d(TAG,"创建数据库....");
        //getRetryData(mContext); //获取SharedPreferences 储存的语言设置 导入相应语言的表格
        String sql="create table "+Constants.TABLE_NAME+"(ID char(4),name char(8),age char(3),gender char(1),test_channel char(15),result char(15))";//managementinfo表格
        String sql1 = "create table " + Constants.TABLE_NAME1 + "(" + BaseColumns._ID   //picture表格
                + " integer primary key autoincrement," + PictureColumns.PICTURE //primary key autoincrement 自动累加
                + " blob not null);";
        String sql2 = "create table "+Constants.TABLE_NAME3+"(option1 char(10),option2 char(10),option3 char(10),option4 char(10),index01 char(10),index02 char(10),index03 char(10),index04 char(10),correct char(10))";//excel表格
        String sql3 = "create table "+Constants.TABLE_NAME4+"(ID char(6),name char(18),sex char(1),age char(3),responTime char(25),odorStartTime char(25),odorEndTime char(25),retryCount char(10),option1 char(10),option2 char(10),option3 char(10),option4 char(10),answer char(10),correct_answer char(10),answercount char(15),result char(15))";
        db.execSQL(sql); //execSQL()方法不能执行查询操作。可以执行有更改行为的SQL语句
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        //初始化
        initDataBase(db,mContext);//将图片插入数据库
        importSheet(db,mContext); //将答题表格插入数据库
    }

    //将转换后的图片存入到数据库中
    private void initDataBase (SQLiteDatabase db, Context context) {
        Resources r = context.getResources();
        Drawable[] drawable = {
                r.getDrawable(R.drawable.xiangjiao),r.getDrawable(R.drawable.tanxiang),r.getDrawable(R.drawable.yezi),r.getDrawable(R.drawable.kafei),//4
                r.getDrawable(R.drawable.qiaokeli),r.getDrawable(R.drawable.chengzi),r.getDrawable(R.drawable.putao),r.getDrawable(R.drawable.ningmeng),//8
                r.getDrawable(R.drawable.meigui),r.getDrawable(R.drawable.huasheng),r.getDrawable(R.drawable.caomei),r.getDrawable(R.drawable.boluo),//12
                r.getDrawable(R.drawable.yu),r.getDrawable(R.drawable.huanggua),r.getDrawable(R.drawable.jiang),r.getDrawable(R.drawable.zhimayou),//16
                r.getDrawable(R.drawable.pingguo),r.getDrawable(R.drawable.dasuan),r.getDrawable(R.drawable.pige),r.getDrawable(R.drawable.dingxiang),//20
                r.getDrawable(R.drawable.zidingxiang),r.getDrawable(R.drawable.huangyou),r.getDrawable(R.drawable.bohe),r.getDrawable(R.drawable.taozi),//24
                r.getDrawable(R.drawable.yingtao),r.getDrawable(R.drawable.molihua),r.getDrawable(R.drawable.yingershuangshenfen),r.getDrawable(R.drawable.yan),
                r.getDrawable(R.drawable.songshu),r.getDrawable(R.drawable.feizao),r.getDrawable(R.drawable.lvcaodi),r.getDrawable(R.drawable.bohenao),
                r.getDrawable(R.drawable.mangguo),r.getDrawable(R.drawable.niunai),r.getDrawable(R.drawable.xiangcao),r.getDrawable(R.drawable.cu),
                r.getDrawable(R.drawable.jiangyou),r.getDrawable(R.drawable.yangcong),r.getDrawable(R.drawable.gancao),r.getDrawable(R.drawable.youzi),//40
                r.getDrawable(R.drawable.hetao),r.getDrawable(R.drawable.binggan),r.getDrawable(R.drawable.fengmi),r.getDrawable(R.drawable.rougui),
                r.getDrawable(R.drawable.nailao),r.getDrawable(R.drawable.li),r.getDrawable(R.drawable.xigua),r.getDrawable(R.drawable.kouxiangtang),
                r.getDrawable(R.drawable.youqixishiji),r.getDrawable(R.drawable.tianranqi),
        };
        ContentValues cv = new ContentValues();
        for (int i=0;i<drawable.length;i++){
            cv.put(PictureColumns.PICTURE, getPicture(drawable[i]));
            db.insert(Constants.TABLE_NAME1, null, cv);
        }
    }

    //将drawable转换成可以用来存储的byte[]类型
    private byte[] getPicture(Drawable drawable) {
        if(drawable == null) {
            return null;
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /*
    对图片名称excel表的内容进行操作
     */
    public void importSheet(SQLiteDatabase db,Context context) {
        try {
/*            SharedPreferences retryCount = context.getSharedPreferences("retryCount",Context.MODE_PRIVATE);
            language_index = retryCount.getInt("language_set",0);
            Log.d(TAG,Integer.toString(language_index));*/
            InputStream is = context.getResources().getAssets().open("choose.xls");
            Workbook book = Workbook.getWorkbook(is);
            Log.d(TAG,"导入excel表格数据....");
            Sheet sheet = book.getSheet(0);//导入相应语言的excel表格
            for (int j = 1; j < sheet.getRows(); ++j) {
                initDataInfo(db,sheet.getCell(0, j).getContents(), sheet.getCell(1, j).getContents(), sheet.getCell(2, j).getContents(), sheet.getCell(3, j).getContents(),
                        sheet.getCell(4, j).getContents(),sheet.getCell(5, j).getContents(),sheet.getCell(6, j).getContents(),sheet.getCell(7, j).getContents(),sheet.getCell(8, j).getContents());
            }
            book.close();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    /*
    * helper中新建删除类，补充删除功能，供管理员界面调用
    * 参数：唯一标码ID
    * 功能：删除TABLE_NAME  TABLE_NAME4中储存选定的数据
    * 返回值：集合map 返回删除结果
    * 日期：2021/6/4 lan
    * */
    public Map<String,Integer> deleteFromDdById (String ID){
        Map<String, Integer> map = new HashMap<String, Integer>();
        SQLiteDatabase db = getWritableDatabase();
        int result1 =  db.delete(Constants.TABLE_NAME,"ID like ?",new String[]{ID});
        int result2 = db.delete(Constants.TABLE_NAME4,"ID like ?",new String[]{ID});
        map.put("result1",result1);
        map.put("result2",result2);
        return map;
    }
    /*
     * helper中新建全部删除类，补充删除功能，供管理员界面调用
     * 参数：null
     * 功能：删除TABLE_NAME  TABLE_NAME4中储存的所有数据
     * 返回值：null
     * 日期：2021/6/23 lan
     * */
    public void deleteAllTable(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + Constants.TABLE_NAME + Constants.TABLE_NAME4);
    };
    /*
    将图片名称excel表内容导入数据库
     */
    public void initDataInfo(SQLiteDatabase db,String option1, String option2,String option3,String option4,String index01,String index02,String index03,String index04,String correct) {
        ContentValues cv = new ContentValues();
        cv.put("option1", option1);
        cv.put("option2", option2);
        cv.put("option3", option3);
        cv.put("option4", option4);
        cv.put("index01", index01);
        cv.put("index02", index02);
        cv.put("index03", index03);
        cv.put("index04", index04);
        cv.put("correct", correct);
        db.insert(Constants.TABLE_NAME3, null, cv);
    }

    public static void getRetryData(Context context) {//通过上下文拿到MangementTabActivity定义的retryCount
        SharedPreferences retryCount = context.getSharedPreferences("retryCount",Context.MODE_PRIVATE);
        language_index = retryCount.getInt("language_set",0);
        Log.d(TAG,Integer.toString(language_index));
    }

    //数据库升级时，此方法会调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //升级时的回调
        Log.d(TAG,"升级数据库....");
        String sql = " DROP TABLE IF EXISTS " + Constants.TABLE_NAME;
        db.execSQL(sql);
        String sql1 = " DROP TABLE IF EXISTS " + Constants.TABLE_NAME1;
        db.execSQL(sql1);
        String sql2 = " DROP TABLE IF EXISTS " + Constants.TABLE_NAME3;
        db.execSQL(sql2);
        String sql3 = " DROP TABLE IF EXISTS " + Constants.TABLE_NAME4;
        db.execSQL(sql3);
        onCreate(db);
    }
}
