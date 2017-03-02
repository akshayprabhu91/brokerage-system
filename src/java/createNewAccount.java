/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author SONY
 */
@Named(value = "createNewAccount")
@RequestScoped
@ManagedBean
public class createNewAccount {

    private String accountNum;
    private String ssn;
    private String fullName;
    private String loginID;
    private String pwd;
    private double amount;
    private String questionOne;
    private String questionTwo;
    private String questionThree;
    private String answerOne;
    private String answerTwo;
    private String answerThree;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAnswerOne() {
        return answerOne;
    }

    public void setAnswerOne(String answerOne) {
        this.answerOne = answerOne;
    }

    public String getAnswerTwo() {
        return answerTwo;
    }

    public void setAnswerTwo(String answerTwo) {
        this.answerTwo = answerTwo;
    }

    public String getAnswerThree() {
        return answerThree;
    }

    public void setAnswerThree(String answerThree) {
        this.answerThree = answerThree;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getQuestionOne() {
        return questionOne;
    }

    public void setQuestionOne(String questionOne) {
        this.questionOne = questionOne;
    }

    public String getQuestionTwo() {
        return questionTwo;
    }

    public void setQuestionTwo(String questionTwo) {
        this.questionTwo = questionTwo;
    }

    public String getQuestionThree() {
        return questionThree;
    }

    public void setQuestionThree(String questionThree) {
        this.questionThree = questionThree;
    }

    public String createAccount() {
        //load the Driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return ("internalError");
        }

        DatabaseFunctions db = new DatabaseFunctions();
        if (db.ifRecordsExist("select * from onlineaccount where LoginID = '" + loginID + "' or SSN = " + ssn)) {
            return ("Account already exists. Account creation failed!");
        } else {
            accountNum = "" + db.getCurrentAccountNumber();

            db.insertUpdateDelete("update nextaccountnumber set NextID = " + (db.getCurrentAccountNumber() + 1));
            db.insertUpdateDelete("insert into onlineaccount values('" + accountNum + "','" + loginID + "', '" + fullName + "', '" + ssn + "', '" + pwd + "','" + amount + "')");
            db.insertUpdateDelete("insert into password_retrieval values('" + accountNum + "', '" + questionOne + "', '" + answerOne + "', '" + questionTwo + "', '" + answerTwo + "', '" + questionThree + "', '" + answerThree + "')");
            db.insertUpdateDelete("insert into failedloginattempts values ('" + loginID + "', 0)");

            return ("Account creation Successful!");
        }
    }

    public ArrayList<String> fetchQuestions() {
        ArrayList<String> arr = new ArrayList<String>();
        arr.add("What is your dream job?");
        arr.add("What was the name of your first pet?");
        arr.add("What was the first book you ever read?");

        return arr;
    }
}
