package com.example.myapplication.db;

public class Constants {
    //用户信息表
    public static final String DATABASE_NAME = "message.db";
    //数据库版本号
    public static final int VERSION_CODE = 17;
    //管理员管理表
    public static final String TABLE_NAME = "managementinfo";
    //图片表
    public static final String TABLE_NAME1 = "picture";
    //图标对应的名称表
    public static final String TABLE_NAME3 = "excel";
    //用户作答表
    public static final String TABLE_NAME4 = "answerresult";

    /*intent tag*/
    public static final String INTENT_IP = "intentIp";//ip
    public static final String INTENT_PORT = "intentPort";//port(端口号)
    /*EventBus  msg*/
    public static final String CONNET_SUCCESS = "connectSucccess";
    public static final String CONNET_FAIL = "connectError";
}
