package com.example.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class TabActivity extends BaseActivity implements View.OnClickListener {
    //声明三个Tab的布局文件
    private LinearLayout needToKnow;
    private LinearLayout channelSelect;
    private LinearLayout resultUI;

    //声明三个Tab的ImageButton
    private ImageButton needToKnowImg;
    private ImageButton channelSelectImg;
    private ImageButton resultUIImg;

    //声明三个Tab分别对应的Fragment
    private Fragment needToKnowFrg;
    private Fragment channelSelectFrg;
    private Fragment resultUIFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab);
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(1);//默认选中第一个Tab
    }

    private void initEvents() {
        needToKnow.setOnClickListener(this);
        channelSelect.setOnClickListener(this);
        resultUI.setOnClickListener(this);
    }

    private void initViews() {
        needToKnow = (LinearLayout) findViewById(R.id.need_to_know);
        channelSelect = (LinearLayout) findViewById(R.id.channel_select);
        resultUI = (LinearLayout) findViewById(R.id.result_ui);

        needToKnowImg = (ImageButton) findViewById(R.id.needtoknowimg);
        channelSelectImg = (ImageButton) findViewById(R.id.channelselectimg);
        resultUIImg = (ImageButton) findViewById(R.id.resultuiimg);
    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将三个ImageButton置为灰色
        resetImgs();
        int id = v.getId();
        if (id == R.id.need_to_know) {
            selectTab(0);
        } else if (id == R.id.channel_select) {
            selectTab(1);
        } else if (id == R.id.result_ui) {
            selectTab(2);
        }
    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //设置微信的ImageButton为绿色
                needToKnowImg.setImageResource(R.drawable.knowpress);
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (needToKnowFrg == null) {
                    needToKnowFrg = new NeedToKnow_Fragment();
                    transaction.add(R.id.id_content, needToKnowFrg);
                } else {
                    transaction.show(needToKnowFrg);
                }
                break;
            case 1:
                channelSelectImg.setImageResource(R.drawable.selectpress);
                if (channelSelectFrg == null) {
                    channelSelectFrg = new ChannelSelect_Fragment();
                    transaction.add(R.id.id_content, channelSelectFrg);
                } else {
                    transaction.show(channelSelectFrg);
                }
                break;
            case 2:
                resultUIImg.setImageResource(R.drawable.resultpress);
                if (resultUIFrg == null) {
                    resultUIFrg = new ResultUI_Fragment();
                    transaction.add(R.id.id_content, resultUIFrg);
                } else {
                    transaction.show(resultUIFrg);
                }
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    //将三个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (needToKnowFrg != null) {
            transaction.hide(needToKnowFrg);
        }
        if (channelSelectFrg != null) {
            transaction.hide(channelSelectFrg);
        }
        if (resultUIFrg != null) {
            transaction.hide(resultUIFrg);
        }
    }

    //将三个ImageButton置为灰色
    private void resetImgs() {
        needToKnowImg.setImageResource(R.drawable.know);
        channelSelectImg.setImageResource(R.drawable.select);
        resultUIImg.setImageResource(R.drawable.result);
    }
}