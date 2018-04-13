package com.expenditrack.expenditrack;

public class User {

    private String username;
    private String passwd;
    private String secQuestion;
    private String secAnswer;

    public User(String username, String passwd, String secQuestion, String secAnswer) {
        this.username = username;
        this.passwd = passwd;
        this.secQuestion = secQuestion;
        this.secAnswer = secAnswer;
    }

    public User(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecQuestion() {
        return secQuestion;
    }

    public void setSecQuestion(String secQuestion) {
        this.secQuestion = secQuestion;
    }

    public String getSecAnswer() {
        return secAnswer;
    }

    public void setSecAnswer(String secAnswer) {
        this.secAnswer = secAnswer;
    }
}
