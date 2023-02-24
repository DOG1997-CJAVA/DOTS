package com.example.myapplication.utils.options;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.example.myapplication.MainActivity;
import com.example.myapplication.OnDoubleClickListener;
import com.example.myapplication.R;
import com.example.myapplication.bitmaploader.MyBitmapLoder;
import com.example.myapplication.databinding.ActivityOption12Binding;
import com.example.myapplication.db.Constants;
import com.example.myapplication.db.MyOpenHelper;
import com.example.myapplication.language.BaseActivity;
import com.example.myapplication.mocktestid.MockTestFinishActivity;
import com.example.myapplication.service.SocketService;

import java.util.ArrayList;

public class Option2Activity extends BaseActivity {

    private ActivityOption12Binding bindingOpt12;
    private static final String TAG = "Option2Activity";
    static int retry_12 = 1;
    int TIME = 3;
    private int mProgressStatus = 0;
    private int number_count2 = 0;
    private ServiceConnection sc;
    public SocketService socketService;
    private final String[] status_40 =  {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "201", "202", "203", "306", "307","308"};// 20 21 为外部气体通道 1-19和22、23为识别通道
    final int[] retryCount_savce = {0};
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mProgressStatus++;
                    bindingOpt12.retryProgressBar.setProgress(mProgressStatus);
                    bindingOpt12.butRetryClicked.setVisibility(View.GONE);
                    bindingOpt12.retryProgressBar.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mProgressStatus = 0;
                    bindingOpt12.butRetryClicked.setEnabled(true);
                    bindingOpt12.retryProgressBar.setProgress(mProgressStatus);
                    bindingOpt12.retryProgressBar.setVisibility(View.GONE);
                    if (retryCount_savce[0] < retry_12) {
                        bindingOpt12.butRetryClicked.setVisibility(View.VISIBLE);
                    } else {
                        bindingOpt12.retryProgressBar.setVisibility(View.GONE);
                        bindingOpt12.butRetryClicked.setVisibility(View.GONE);
                        Toast.makeText(Option2Activity.this, getString(R.string.btn_retry_complete), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingOpt12 = ActivityOption12Binding.inflate(getLayoutInflater());
        setContentView(bindingOpt12.getRoot());
        bindSocketService();
        MyOpenHelper dbHelper = new MyOpenHelper(Option2Activity.this);
        MyBitmapLoder instance = MyBitmapLoder.getInstance();
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        SharedPreferences option2_shareprefe = getSharedPreferences("retryCount", Context.MODE_PRIVATE);
        int language_index = option2_shareprefe.getInt("language_set", 0);
        retry_12 = option2_shareprefe.getInt("retry_time", 1);
        TIME = option2_shareprefe.getInt("odor_release_time", 3);

        if (retry_12 == 0) {
            bindingOpt12.butRetryClicked.setVisibility(View.GONE);
            bindingOpt12.butRetryClicked.setEnabled(false);
        }
        bindingOpt12.retryProgressBar.setVisibility(View.GONE);
        number_count2 = getIntent().getIntExtra("number_count", 0);
        int accept2 = getIntent().getIntExtra("send2", 0);
        if (language_index == 1) {//递增40行取相应语言的描述词
            accept2 = accept2 + 20;
        }

        String tempSrt = getString(R.string.ansewer_ui_count) + (number_count2 + 1) ;
        bindingOpt12.textview.setText(tempSrt);
        bindingOpt12.retryProgressBar.setMax(TIME);
        bindingOpt12.retryProgressBar.setClickable(false);
        String sql = "select * from " + Constants.TABLE_NAME3 + " where rowid=" + (accept2 + 1);
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        try {
            cursor.moveToFirst();
            bindingOpt12.text1.setText(cursor.getString(cursor.getColumnIndex("option1")));
            bindingOpt12.text2.setText(cursor.getString(cursor.getColumnIndex("option2")));
            bindingOpt12.text3.setText(cursor.getString(cursor.getColumnIndex("option3")));
            bindingOpt12.text4.setText(cursor.getString(cursor.getColumnIndex("option4")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取图片的数量
        int count = getDrawable().size();
        if (count != 0) {
            int index1, index2, index3, index4;
            try {
                index1 = cursor.getInt(cursor.getColumnIndex("index01"));
                index2 = cursor.getInt(cursor.getColumnIndex("index02"));
                index3 = cursor.getInt(cursor.getColumnIndex("index03"));
                index4 = cursor.getInt(cursor.getColumnIndex("index04"));
                bindingOpt12.image1.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index1-1)));
                bindingOpt12.image2.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index2-1)));
                bindingOpt12.image3.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index3-1)));
                bindingOpt12.image4.setImageBitmap(instance.getBitmapFromLocal(String.valueOf(index4-1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        int finalAccept = accept2 - 20;
        bindingOpt12.image1.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.text1.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.image2.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.text2.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.image3.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.text3.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.image4.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.text4.setOnTouchListener(new OnDoubleClickListener(this::judge));
        bindingOpt12.butRetryClicked.setOnClickListener(v -> {
            bindingOpt12.butRetryClicked.setEnabled(false);
            bindingOpt12.retryProgressBar.setVisibility(View.VISIBLE);
            if (retryCount_savce[0] < retry_12) {
                Log.d(TAG, retryCount_savce[0] + "");
                retryCount_savce[0]++;
                try {
                    socketService.sendOrder(status_40[finalAccept]);
                } catch (Exception e){
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {
                        while (mProgressStatus < TIME) {
                            try {
                                Thread.sleep(1000);
                                mHandler.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }.start();
            }
        });
    }

    public void judge(){
        if (number_count2 == 3) {
            Intent intent = new Intent(Option2Activity.this, MockTestFinishActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = getIntent();
            intent.putExtra("send3", number_count2 + 1);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onBackPressed() {
        new DroidDialog.Builder(this)
                .cancelable(true, false)
                .icon(R.drawable.ic_baseline_warning_24)
                .title(getString(R.string.remind))
                .content(getString(R.string.quit_mock_test_remind))
                .cancelable(true, true)
                .positiveButton(getString(R.string.confirm1), dialog -> {
                    MyOpenHelper moh = new MyOpenHelper(Option2Activity.this);
                    SQLiteDatabase sd = moh.getReadableDatabase();
                    String sql1 = "select * from " + Constants.TABLE_NAME + " where result=" + "'默认'";
                    Cursor cursor1 = sd.rawQuery(sql1, null);
                    cursor1.moveToFirst();
                    String id = cursor1.getString(cursor1.getColumnIndex("ID"));
                    cursor1.close();
                    String sql = "delete from " + Constants.TABLE_NAME4 + " where ID=" + id;
                    sd.execSQL(sql);
                    sd.close();
                    Intent intent = new Intent(Option2Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .negativeButton(getString(R.string.cencle1), Dialog::dismiss)
                .animation(AnimUtils.AnimFadeInOut)
                .color(ContextCompat.getColor(Option2Activity.this, R.color.orange), ContextCompat.getColor(Option2Activity.this, R.color.white),
                        ContextCompat.getColor(Option2Activity.this, R.color.black))
                .show();
    }

    private void bindSocketService() {
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
        bindService(intent, sc, BIND_AUTO_CREATE);
    }

    private ArrayList<Drawable> getDrawable() {
        MyOpenHelper moh = new MyOpenHelper(this);
        SQLiteDatabase sd = moh.getReadableDatabase();
        ArrayList<Drawable> drawables = new ArrayList<>();
        Cursor c = sd.query("picture", null, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            while (c.moveToNext()) {
                byte[] answerA = c.getBlob(c.getColumnIndexOrThrow(MyOpenHelper.PictureColumns.PICTURE));
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(answerA, 0, answerA.length, null);
                Drawable drawable1 = new BitmapDrawable(getResources(), bitmap1);
                drawables.add(drawable1);
            }
        }
        assert c != null;
        c.close();
        return drawables;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sc != null) {
            unbindService(sc);
        }
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

}
