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

import androidx.core.content.res.ResourcesCompat;

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

    private final Context mContext;
    public MyOpenHelper(Context context){
        super(context,Constants.DATABASE_NAME,null,Constants.VERSION_CODE);
        this.mContext = context;//上下文
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"创建数据库....");
        String sql="create table "+Constants.TABLE_NAME+"(ID char(4),name char(8),age char(3),gender char(1),test_channel char(15)," +
                "result char(15),educate char(15))";
        String sql1 = "create table " + Constants.TABLE_NAME1 + "(" + BaseColumns._ID
                + " integer primary key autoincrement," + PictureColumns.PICTURE
                + " blob not null);";
        String sql2 = "create table "+Constants.TABLE_NAME3+"(option1 char(10),option2 char(10),option3 char(10),option4 char(10)," +
                "index01 char(10),index02 char(10),index03 char(10),index04 char(10),correct char(10))";
        String sql3 = "create table "+Constants.TABLE_NAME4+"(ID char(6),name char(18),sex char(1),age char(3),educate char(15), " +
                "responTime char(25),odorStartTime char(25),odorEndTime char(25),responDurationTime char(25),retryCount char(10)," +
                "option1 char(10),option2 char(10),option3 char(10),option4 char(10),answer char(10),correct_answer char(10)," +
                "answercount char(15),result char(15))";
        db.execSQL(sql);
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        initDataBase(db,mContext);
        importSheet(db,mContext);
    }

    //将转换后的图片存入到数据库中
    private void initDataBase (SQLiteDatabase db, Context context) {
        Resources r = context.getResources();
        Drawable[] drawable = {
                ResourcesCompat.getDrawable(r,R.drawable.xiangjiao, null),ResourcesCompat.getDrawable(r,R.drawable.tanxiang, null),ResourcesCompat.getDrawable(r,R.drawable.yezi, null),ResourcesCompat.getDrawable(r,R.drawable.kafei,null),//4
                ResourcesCompat.getDrawable(r,R.drawable.qiaokeli, null),ResourcesCompat.getDrawable(r,R.drawable.chengzi, null),ResourcesCompat.getDrawable(r,R.drawable.putao, null),ResourcesCompat.getDrawable(r,R.drawable.ningmeng,null),//8
                ResourcesCompat.getDrawable(r,R.drawable.meigui, null),ResourcesCompat.getDrawable(r,R.drawable.huasheng, null),ResourcesCompat.getDrawable(r,R.drawable.caomei, null),ResourcesCompat.getDrawable(r,R.drawable.boluo,null),//12
                ResourcesCompat.getDrawable(r,R.drawable.yu,null),ResourcesCompat.getDrawable(r,R.drawable.huanggua,null),ResourcesCompat.getDrawable(r,R.drawable.jiang,null),ResourcesCompat.getDrawable(r,R.drawable.zhimayou,null),//16
                ResourcesCompat.getDrawable(r,R.drawable.pingguo,null),ResourcesCompat.getDrawable(r,R.drawable.dasuan,null),ResourcesCompat.getDrawable(r,R.drawable.pige,null),ResourcesCompat.getDrawable(r,R.drawable.dingxiang,null),//20
                ResourcesCompat.getDrawable(r,R.drawable.zidingxiang,null),ResourcesCompat.getDrawable(r,R.drawable.huangyou,null),ResourcesCompat.getDrawable(r,R.drawable.bohe,null),ResourcesCompat.getDrawable(r,R.drawable.taozi,null),//20
                ResourcesCompat.getDrawable(r,R.drawable.yingtao,null),ResourcesCompat.getDrawable(r,R.drawable.molihua,null),ResourcesCompat.getDrawable(r,R.drawable.yingershuangshenfen,null),ResourcesCompat.getDrawable(r,R.drawable.yan,null),// 24
                ResourcesCompat.getDrawable(r,R.drawable.songshu,null),ResourcesCompat.getDrawable(r,R.drawable.feizao,null),ResourcesCompat.getDrawable(r,R.drawable.lvcaodi,null),ResourcesCompat.getDrawable(r,R.drawable.bohenao,null),
                ResourcesCompat.getDrawable(r,R.drawable.mangguo,null),ResourcesCompat.getDrawable(r,R.drawable.niunai,null),ResourcesCompat.getDrawable(r,R.drawable.xiangcaobingqilin,null),ResourcesCompat.getDrawable(r,R.drawable.cu,null),
                ResourcesCompat.getDrawable(r,R.drawable.jiangyou,null),ResourcesCompat.getDrawable(r,R.drawable.yangcong,null),ResourcesCompat.getDrawable(r,R.drawable.gancao,null),ResourcesCompat.getDrawable(r,R.drawable.youzi,null),
                ResourcesCompat.getDrawable(r,R.drawable.hetao,null),ResourcesCompat.getDrawable(r,R.drawable.binggan,null),ResourcesCompat.getDrawable(r,R.drawable.bohe,null),ResourcesCompat.getDrawable(r,R.drawable.rougui,null),
                ResourcesCompat.getDrawable(r,R.drawable.nailao,null),ResourcesCompat.getDrawable(r,R.drawable.li,null),ResourcesCompat.getDrawable(r,R.drawable.xigua,null),ResourcesCompat.getDrawable(r,R.drawable.kouxiangtang,null),
                ResourcesCompat.getDrawable(r,R.drawable.youqixishiji,null),ResourcesCompat.getDrawable(r,R.drawable.tianranqi,null)
        };
        ContentValues cv = new ContentValues();
        for (Drawable value : drawable) {
            cv.put(PictureColumns.PICTURE, getPicture(value));
            db.insert(Constants.TABLE_NAME1, null, cv);
        }
    }

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

    public void importSheet(SQLiteDatabase db,Context context) {
        try {
            InputStream is = context.getResources().getAssets().open("choose2.xls");
            Workbook book = Workbook.getWorkbook(is);
            Log.d(TAG,"导入excel表格数据....");
            Sheet sheet = book.getSheet(0);
            for (int j = 1; j < sheet.getRows(); ++j) {
                initDataInfo(db,sheet.getCell(0, j).getContents(), sheet.getCell(1, j).getContents(), sheet.getCell(2, j).getContents(), sheet.getCell(3, j).getContents(),
                        sheet.getCell(4, j).getContents(),sheet.getCell(5, j).getContents(),sheet.getCell(6, j).getContents(),sheet.getCell(7, j).getContents(),sheet.getCell(8, j).getContents());
            }
            book.close();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }


    public Map<String,Integer> deleteFromDdById (String ID){
        Map<String, Integer> map = new HashMap<>();
        SQLiteDatabase db = getWritableDatabase();
        int result1 =  db.delete(Constants.TABLE_NAME,"ID like ?",new String[]{ID});
        int result2 = db.delete(Constants.TABLE_NAME4,"ID like ?",new String[]{ID});
        map.put("result1",result1);
        map.put("result2",result2);
        return map;
    }

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

    public static void getRetryData(Context context) {
        SharedPreferences retryCount = context.getSharedPreferences("retryCount",Context.MODE_PRIVATE);
        language_index = retryCount.getInt("language_set",0);
        Log.d(TAG,Integer.toString(language_index));
    }

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

    public String getDBPath(){
        return mContext.getDatabasePath(Constants.DATABASE_NAME).getPath();
    }
}
