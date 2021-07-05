package com.example.myapplication.db;



public class ManagementInfo {
    //用户ID号
    private String ID;
    //用户姓名
    private String name;
    //用户年龄
    private String age;
    //用户性别
    private String gender;
    //用户测试类型
    private String test_channel;
    //用户作答结果
    private String result;

    public ManagementInfo(){};
    public ManagementInfo(String ID, String name, String age, String gender,String test_channel,String result) {
        this.ID = ID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.test_channel = test_channel;
        this.result = result;
    }
    public ManagementInfo(String ID, String name, String result) {
        this.ID = ID;
        this.name = name;
        this.result = result;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTest_channel() {
        return test_channel;
    }

    public void getTest_channel(String test_channel) {
        this.test_channel = test_channel;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
