package com.example.myapplication.db;

public class ResultDetailInfo {
    //目标选项
    private String option1;
    //错误选项1
    private String option2;
    //错误选项2
    private String option3;
    //错误选项3
    private String option4;
    //作答答案
    private String answer;
    //作答答案时间
    private String correct_answer;

    public ResultDetailInfo() {
    }

    public ResultDetailInfo(String option1, String option2, String option3, String option4, String answer, String correct_answer ) {
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.correct_answer = correct_answer;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }
}
