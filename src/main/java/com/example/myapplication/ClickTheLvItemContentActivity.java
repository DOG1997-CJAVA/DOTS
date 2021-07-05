package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.files.DialogFolderChooserExtKt;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.ManagementInfo;
import com.example.myapplication.db.MyOpenHelper;
import com.liyu.sqlitetoexcel.SQLiteToExcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;

public class ClickTheLvItemContentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 127;
    private MyOpenHelper moh;
    private SQLiteDatabase sd;
    private List<String> dataList;
    private ClickItemContentAdapter adapter;
    private TextView tv_title;
    private String ID;
    private Button excel,delete_all;

    //以实现输出excel到具体路径下 依据日期添加筛选测试结果 SQlite导出为excel文件
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_item_content);
        if (ContextCompat.checkSelfPermission(ClickTheLvItemContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClickTheLvItemContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);//申请WRITE_EXTERNAL_STORAGE权限
        }
        delete_all = (Button) findViewById(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击删除按钮之后，给出dialog提示
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(ClickTheLvItemContentActivity.this, bottomSheet);
                dialog.title(R.string.remind4, null);
                dialog.message(R.string.delete_all_data, null, null);
                dialog.positiveButton(R.string.remind6, null, materialDialog -> {
                    for(int i = dataList.size() -1 ; i >= 0; i--){
                        String idNumber = (dataList.get(i) + "").substring(3,7);
                        System.out.println(i);
                        System.out.println(idNumber);
                        Map map = moh.deleteFromDdById(idNumber);
                        if (map.get("result1") != null && map.get("result2") != null) {
                            Toast.makeText(ClickTheLvItemContentActivity.this, "已成功删除数据", Toast.LENGTH_SHORT).show();
                        }
                        dataList.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                    return null;
                });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ClickTheLvItemContentActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return null;
                });
                dialog.show();
            }
        });
        excel = (Button) findViewById(R.id.sql_to_excel_click);
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ClickTheLvItemContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ClickTheLvItemContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);//申请WRITE_EXTERNAL_STORAGE权限
                }
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                MaterialDialog dialog_folder = new MaterialDialog(ClickTheLvItemContentActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
                dialog_folder.title(R.string.save_excel_folder, null);
                DialogFolderChooserExtKt.folderChooser(dialog_folder, getExternalStorageDirectory(), null,
                        true, R.string.files_default_empty_text, true, null,
                        (materialDialog, file) -> {
                            try {
                                new SQLiteToExcel
                                        .Builder(ClickTheLvItemContentActivity.this)
                                        .setDataBase(sd.getPath())
                                        .setTables(Constants.TABLE_NAME4)
                                        .setOutputPath(file.getAbsolutePath())
                                        .setOutputFileName(sdf.format(date) + "测试结果.xls")
                                        .start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(ClickTheLvItemContentActivity.this, "Selected file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            return null;
                        });
                dialog_folder.positiveButton(R.string.confirm1, null, materialDialog -> {
                    Toast.makeText(ClickTheLvItemContentActivity.this, "已导出到指定目录", Toast.LENGTH_SHORT).show();
                    return null;
                });
                dialog_folder.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(ClickTheLvItemContentActivity.this, "已取消数据导出", Toast.LENGTH_SHORT).show();
                    return null;
                });
                dialog_folder.show();
            }
        });
        //创建或打开数据库
        moh = new MyOpenHelper(this);
        sd = moh.getReadableDatabase();
        //删除result为'默认'的字段，否则结果详情页面会出错
        String sql = "delete from " + Constants.TABLE_NAME + " where result=" + "'默认'";
        sd.execSQL(sql);
        //初始化数据
        dataList = new ArrayList<>();
        //扫描数据库,将数据库信息放入resultList
        Cursor cursor = sd.rawQuery("select ID,name,result from " + Constants.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String ID = cursor.getString(cursor.getColumnIndex("ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String result = cursor.getString(cursor.getColumnIndex("result"));
            dataList.add("ID:" + ID + " " + "姓名:" + name + " " + "结果:" + result);
        }
        cursor.close();
        sd.close();
        //获取lv 并设置适配器
        ListView listView = (ListView) findViewById(R.id.lv_clickItemContent);
        listView.setEmptyView(findViewById(R.id.isEmpty));
        //创建适配器，传递数据集合，以及条目中被点击控件的的点击监听
        adapter = new ClickItemContentAdapter(ClickTheLvItemContentActivity.this, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ClickTheLvItemContentActivity.this, position + "号位置的条目被点击", Toast.LENGTH_SHORT).show();
                if (view != null) {
                    String idNumber = (dataList.get(position) + "").substring(3, 7);
                    //Toast.makeText(view.getContext(), idNumber, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ClickTheLvItemContentActivity.this, ResultDetailActivity.class);
                    intent.putExtra("ID", idNumber);
                    startActivity(intent);
                }
            }
        });
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ClickTheLvItemContentActivity.this, "已授予权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClickTheLvItemContentActivity.this, "未授予权限，无法导出", Toast.LENGTH_SHORT).show();// Permission Denied
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_del:   //lv条目中 iv_del
                final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转
                //点击删除按钮之后，给出dialog提示
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(this, bottomSheet);
                dialog.title(R.string.remind4, null);
                dialog.message(R.string.remind5, null, null);
                dialog.positiveButton(R.string.remind6, null, materialDialog -> {
                    //Toast.makeText(this, getText(R.string.remind6), Toast.LENGTH_SHORT).show();
                    String idNumber = (dataList.get(position) + "").substring(3, 7);
                    Map map = moh.deleteFromDdById(idNumber);
                    if (map.get("result1") != null && map.get("result2") != null) {
                        Toast.makeText(ClickTheLvItemContentActivity.this, "已成功删除数据", Toast.LENGTH_SHORT).show();
                    }
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    return null;
                });
                dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                    Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return null;
                });
                dialog.show();
                break;
        }
    }
}
