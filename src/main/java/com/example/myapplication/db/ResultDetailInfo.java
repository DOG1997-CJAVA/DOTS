package com.example.myapplication.db;

public class ResultDetailInfo {
    //目标选项
    private String target;
    //错误选项1
    private String error01;
    //错误选项2
    private String error02;
    //错误选项3
    private String error03;
    //作答答案
    private String answer;
    //作答答案时间
    private String answer_time;

    public ResultDetailInfo() {
    }

    public ResultDetailInfo(String target, String error01, String error02, String error03, String answer, String answer_time ) {
        this.target = target;
        this.error01 = error01;
        this.error02 = error02;
        this.error03 = error03;
        this.answer = answer;
        this.answer_time = answer_time;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setError01(String error01) {
        this.error01 = error01;
    }

    public void setError02(String error02) {
        this.error02 = error02;
    }

    public void setError03(String error03) {
        this.error03 = error03;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswerTime(String answer_time) {
        this.answer_time = answer_time;
    }

    public String getTarget() {
        return target;
    }

    public String getError01() {
        return error01;
    }

    public String getError02() {
        return error02;
    }

    public String getError03() {
        return error03;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnswerTime() {
        return answer_time;
    }
}
