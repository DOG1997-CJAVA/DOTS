package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedBackActivity  extends Activity {
    //使用注解绑定当前button
    @BindView(R.id.feedback_submit)
    Button feedback_submit;
    @BindView(R.id.feedback_content)
     EditText content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

    }
    @OnClick(R.id.feedback_submit)
    public void onViewClicked() {
        if (content.getText().toString()!=null&&!"".equals(content.getText().toString())) {
            Toast.makeText(FeedBackActivity.this, "提交成功,谢谢您宝贵的意见！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FeedBackActivity.this, ResultUI_Fragment.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(FeedBackActivity.this, "请输入您的意见！", Toast.LENGTH_SHORT).show();
        }
    }
}
