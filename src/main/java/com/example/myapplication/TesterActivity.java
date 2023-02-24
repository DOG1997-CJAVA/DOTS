package com.example.myapplication;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.example.myapplication.databinding.ActivityTesterBinding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.idtest.Ready12Activity;
import com.example.myapplication.idtest.Ready20Activity;
import com.example.myapplication.idtest.Ready40Activity;
import com.example.myapplication.idtest.ReadyCustomActivity;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.service.SocketService;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//正式测试 受试者信息输入 需要储存到数据库
public class TesterActivity extends BaseActivity {
    String gender;
    boolean gender_is_check = false; //修复性别不选也可进行测试的bug
    private ServiceConnection sc;
    public SocketService socketService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindSocketService();
        com.example.myapplication.databinding.ActivityTesterBinding activityTesterBinding = ActivityTesterBinding.inflate(getLayoutInflater());
        setContentView(activityTesterBinding.getRoot());
        Button btn = activityTesterBinding.testerBtn;
        Button btn_educate = activityTesterBinding.btnEducateList;
        EditText eTN1 = activityTesterBinding.editTextNum1;
        EditText eT1 = activityTesterBinding.editTextName;
        EditText eTN2 = activityTesterBinding.editTextNum2;
        EditText eTN3 = activityTesterBinding.editTextEducate;
        eTN3.setSaveEnabled(false);
        RadioGroup radioGroup = activityTesterBinding.radioGroup;
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            gender = radioButton.getText().toString();
            gender_is_check = true;
        });



        btn_educate.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(this, MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(R.string.educate_level, null);
            DialogListExtKt.listItems(dialog, R.array.educate_level, null, null, true, (materialDialog, index, text) -> {
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                switch (index) {
                    case 0:
                        eTN3.setText("6");
                        break;
                    case 1:
                        eTN3.setText("9");
                        break;
                    case 2:
                        eTN3.setText("12");
                        break;
                    case 3:
                        eTN3.setText("16");
                        break;
                    case 4:
                        eTN3.setText("19");
                        break;
                    case 5:
                        eTN3.setText("23");
                        break;
                }
                return null;
            });
            dialog.positiveButton(R.string.confirm1, null, materialDialog -> {
                Toast.makeText(this, getText(R.string.confirm1), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
                Toast.makeText(this, getText(R.string.cencle1), Toast.LENGTH_SHORT).show();
                return null;
            });
            dialog.show();
        });

        btn.setOnClickListener(v -> {   //trim 去掉前后多余的东西 多余的回车换行键
            Timer my_open_timer = new Timer();
            SharedPreferences retryCount = this.getSharedPreferences("retryCount", MODE_PRIVATE);
            int external_status = retryCount.getInt("external_status",0);
            if( sc != null && socketService != null && external_status == 0){ //判断开启外部供气还是气泵供气
                socketService.sendOrder("404");
            }else if(sc != null && socketService != null && external_status == 1){
                socketService.sendOrder("402");
            }
            if (eT1.getText().toString().trim().isEmpty() || eTN1.getText().toString().trim().isEmpty()
                    || eTN2.getText().toString().trim().isEmpty() || !gender_is_check || eTN3.getText().toString().trim().isEmpty()) {
                Toast.makeText(TesterActivity.this, getString(R.string.info_incomplete_remind), Toast.LENGTH_SHORT).show();
            } else {
                // 创建SQLiteOpenHelper子类对象
                ////注意，一定要传入最新的数据库版本号
                MyOpenHelper dbHelper1 = new MyOpenHelper(TesterActivity.this);
                // 调用getWritableDatabase()方法创建或打开一个可以读的数据库
                SQLiteDatabase sqliteDatabase1 = dbHelper1.getWritableDatabase();
                ContentValues values1 = new ContentValues();// 创建ContentValues对象
                String record_temp = getString(R.string.quit_record_notcomplit);
                String sq1 = "delete from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                sqliteDatabase1.execSQL(sq1);
                values1.put("ID", eTN1.getText().toString());
                values1.put("name", eT1.getText().toString());
                values1.put("age", eTN2.getText().toString());
                values1.put("educate", eTN3.getText().toString());
                // values1.put("medicalHistory",medicalHistory);
                values1.put("gender", gender);
                values1.put("result", "默认");
                sqliteDatabase1.insert(Constants.TABLE_NAME, null, values1);
                sqliteDatabase1.close();//关闭数据库
                Intent intent = getIntent();
                String value = intent.getStringExtra("channel");
                if ("12通道".equals(value)) {
                    Intent intent1 = new Intent(TesterActivity.this, Ready12Activity.class);
                    intent1.putExtra("educate", eTN3.getText().toString());
                    startActivity(intent1);
                    finish();
                } else if ("20通道".equals(value)) {
                    Intent intent1 = new Intent(TesterActivity.this, Ready20Activity.class);
                    intent1.putExtra("educate", eTN3.getText().toString());
                    startActivity(intent1);
                    finish();
                } else if ("custom".equals(value)) {
                    Intent intent1 = new Intent(TesterActivity.this, ReadyCustomActivity.class);
                    intent1.putExtra("educate", eTN3.getText().toString());
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }


    private void bindSocketService() {
        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);//开启socket服务；自动开启
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }
}
