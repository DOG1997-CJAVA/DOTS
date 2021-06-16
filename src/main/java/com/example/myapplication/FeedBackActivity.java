package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.myapplication.mail.MyEmailHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedBackActivity extends Activity {
    private static final String TAG = FeedBackActivity.class.getSimpleName();
    private String EMAIL_FROM;//发件人
    private String EMAIL_TO;//收件人
    private String EMAIL_TITLE;//邮件标题
    private String EMAIL_CONTEXT;//邮件内容
    private String[] kind_of_feedback;//邮件主题
    private Spinner et_email_title;
    private EditText et_email_context;
    private MyEmailHelper helper = new MyEmailHelper();
    private Button feedback;
    private EditText content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedback = (Button) findViewById(R.id.feedback_submit);
        content = (EditText) findViewById(R.id.feedback_content);
        MaterialDialog dialog = new MaterialDialog(this, MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(R.string.remind, null);
        dialog.message(R.string.need_useful_wifi, null, null);
        dialog.positiveButton(R.string.remind3, null, materialDialog -> {
            //Toast.makeText(this, getText(R.string.remind3), Toast.LENGTH_SHORT).show();
            return null;
        });
        dialog.negativeButton(R.string.cencle1, null, materialDialog -> {
            Toast.makeText(this, getText(R.string.cencle1), Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(FeedBackActivity.this, TabActivity.class);//取消则返回
            startActivity(intent2);
            dialog.dismiss();
            finish();
            return null;
        });
        dialog.show();
        initView();
        initData();
        kind_of_feedback = getResources().getStringArray(R.array.feedback_type);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getText().toString() != null && !"".equals(content.getText().toString())) {
                    initData();
                    sendMail(EMAIL_FROM, EMAIL_TO, EMAIL_TITLE, EMAIL_CONTEXT);
                    Toast.makeText(FeedBackActivity.this, "提交反馈邮件中", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FeedBackActivity.this, TabActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(FeedBackActivity.this, "请输入您的意见！当前为空白", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initView() {
        et_email_title = findViewById(R.id.feedback_type_spinner);
        et_email_context = findViewById(R.id.feedback_content);
    }

    private void initData() {
        EMAIL_FROM = "olfactory2021@163.com";//不用用户输入，默认邮箱
        EMAIL_TO = "olfactory20212@163.com";
        //用户只需选择反馈主题 以及反馈内容
        et_email_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EMAIL_TITLE = "用户反馈主题为" + kind_of_feedback[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                EMAIL_TITLE = "用户反馈主题为默认";
            }
        });
        EMAIL_CONTEXT = et_email_context.getText().toString();
        Log.v("Az", "EMAIL_FROM-->" + EMAIL_FROM + "EMAIL_TO-->" + EMAIL_TO
                + "EMAIL_TITLE-->" + EMAIL_TITLE + "EMAIL_CONTEXT-->" + EMAIL_CONTEXT);
    }

    public void sendMail(String from, String to, String title, String context) {
//          附件
//        List<String> files = new ArrayList<String>();
//        files.add("/mnt/sdcard/test.txt");
        //主要接收人的电子邮箱列表
        List<String> toEmail = new ArrayList<String>();
        toEmail.add(to);
        List<String> ccEmail = new ArrayList<String>();
        //抄送人的电子邮箱列表 抄送给自己 防止被检测为垃圾邮件
        ccEmail.add(from);
        helper.setParams(toEmail, ccEmail, title, context, null);
        Log.v(TAG, "toEmail:" + toEmail + " ccEmail:" + ccEmail + " EMAIL_TITLE_APP:" + title + " appEmailContext:" + context);
        helper.setJieEmailInfterface(new MyEmailHelper.EmailInfterface() {
            @Override
            public void startSend() {
                Toast.makeText(FeedBackActivity.this, "邮件发送中~", Toast.LENGTH_LONG).show();
            }

            @Override
            public void SendStatus(MyEmailHelper.SendStatus sendStatus) {
                switch (sendStatus) {
                    case SENDOK:
                        Toast.makeText(FeedBackActivity.this, "发送反馈邮件成功！感谢您的宝贵意见！", Toast.LENGTH_LONG).show();
                        break;
                    case SENDFAIL:
                        Toast.makeText(FeedBackActivity.this, "发送反馈邮件失败~", Toast.LENGTH_LONG).show();
                        break;
                    case SENDING:
                        Toast.makeText(FeedBackActivity.this, "反馈邮件正在发送中，请稍后重试~", Toast.LENGTH_LONG).show();
                        break;
                    case BADCONTEXT:
                        Toast.makeText(FeedBackActivity.this, "反馈邮件内容或标题被识别为垃圾邮件，请修改后重试~", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });
        helper.sendEmail();
    }
}
