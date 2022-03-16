package com.exam.entity;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/19
 */
public class Question {


    private String question;

    private String answer;

    private Integer score;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
