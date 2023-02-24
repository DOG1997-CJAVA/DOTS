package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.liyu.sqlitetoexcel.SQLiteToExcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResultManagementActivity extends BaseActivity implements View.OnClickListener {
    private MyOpenHelper moh;
    private List<String> dataList,idList;
    private ClickItemContentAdapter adapter;
    private static final String TAG = "ResultManagement";
    private String ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_manage_content);
        Button delete_all = (Button) findViewById(R.id.delete_all);
        Button excel = (Button) findViewById(R.id.sql_to_excel_click);
        dataList = new ArrayList<>();
        idList =  new ArrayList<>();
        excel.setOnClickListener(v -> {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            try {
                new SQLiteToExcel
                        .Builder(ResultManagementActivity.this)
                        .setDataBase(moh.getDBPath())
                        .setTables(Constants.TABLE_NAME4)
                        .setOutputPath(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)))
                        .setOutputFileName(sdf.format(date) + getString(R.string.toast_export_filename_ifo))
                        .start(new SQLiteToExcel.ExportListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(ResultManagementActivity.this, "开始导出", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCompleted(String filePath) {
                                Toast.makeText(ResultManagementActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ResultManagementActivity.this, "导出出错"+e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "导出出错"+e.toString());
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //创建或打开数据库
        moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        //删除result为'默认'的字段，否则结果详情页面会出错
        String sql = "delete from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        sd.execSQL(sql);
        //扫描数据库,将数据库信息放入resultList
        Cursor cursor = sd.rawQuery("select ID,name,result from " + Constants.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            ID = cursor.getString(cursor.getColumnIndex("ID"));
            idList.add(ID);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String result = cursor.getString(cursor.getColumnIndex("result"));
            dataList.add(getString(R.string.id_3_7) + ID + " " + getString(R.string.name) + name + " " + getString(R.string.test_status_ifo) + "null");
        }
        cursor.close();
        sd.close();
        ListView listView = (ListView) findViewById(R.id.lv_clickItemContent);
        listView.setEmptyView(findViewById(R.id.isEmpty));
        adapter = new ClickItemContentAdapter(ResultManagementActivity.this, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (view != null) {
                String idNumber = idList.get(position);
                Intent intent = new Intent(ResultManagementActivity.this, ResultDetailActivity.class);
                intent.putExtra("ID", idNumber);
                startActivity(intent);
            }
        });
        if(dataList.isEmpty()){
            delete_all.setClickable(false);
        }else {
            delete_all.setOnClickListener(v -> {
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(ResultManagementActivity.this, bottomSheet);
                dialog.title(R.string.remind4, null);
                dialog.message(R.string.delete_all_data, null, null);
                dialog.positiveButton(R.string.remind6, null, materialDialog -> {
                    for (int i = idList.size() - 1; i >= 0; i--) {
                        String idNumber = idList.get(i);
                        System.out.println(i);
                        System.out.println(idNumber);
                        Map map = moh.deleteFromDdById(idNumber);
                        if (map.get("result1") != null && map.get("result2") != null) {
                            Toast.makeText(ResultManagementActivity.this, getString(R.string.toast_delete_data_ifo), Toast.LENGTH_SHORT).show();
                        }
                        dataList.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                    return null;
                });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ResultManagementActivity.this, getString(R.string.cencle1), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return null;
                });
                dialog.show();
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_del) {
            final int position = (int) v.getTag();
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(this, bottomSheet);
            dialog.title(R.string.remind4, null);
            dialog.message(R.string.remind5, null, null);
            dialog.positiveButton(R.string.remind6, null, materialDialog -> {
                String idNumber = idList.get(position);
                Map map = moh.deleteFromDdById(idNumber);
                if (map.get("result1") != null && map.get("result2") != null) {
                    Toast.makeText(ResultManagementActivity.this, getString(R.string.toast_delete_data_ifo), Toast.LENGTH_SHORT).show();
                }
                dataList.remove(position);
                adapter.notifyDataSetChanged();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(this, getString(R.string.cencle1), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return null;
            });
            dialog.show();
        }
    }
}
