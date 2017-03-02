/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author SONY
 */
@Named(value = "passwordRetrieval")
@ManagedBean
@SessionScoped
public class passwordRetrieval implements Serializable {

    private String loginID;
    private String question;
    private String answer;
    private String password;

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String fetchPassword() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return ("Internal Error");
        }

        DatabaseFunctions db = new DatabaseFunctions();
        if (db.ifRecordsExist("select * from onlineaccount where LoginID = '" + loginID + "'")) {
            String accountNum = db.getAccountNumber(loginID);

            final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
            Connection conn = null;
            Statement stat = null;
            ResultSet rs = null;

            try {
                conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
                stat = conn.createStatement();
                rs = stat.executeQuery("select * from password_retrieval where AccountNum = '" + accountNum + "'");
                rs.next();
                if (rs.getString(2).equalsIgnoreCase(question)) {
                    if (rs.getString(3).equalsIgnoreCase(answer)) {
                        this.setPassword(db.getPassword(loginID));
                    }
                }
                if (rs.getString(4).equalsIgnoreCase(question)) {
                    if (rs.getString(5).equalsIgnoreCase(answer)) {
                        this.setPassword(db.getPassword(loginID));
                    }
                }
                if (rs.getString(6).equalsIgnoreCase(question)) {
                    if (rs.getString(7).equalsIgnoreCase(answer)) {
                        this.setPassword(db.getPassword(loginID));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    stat.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "retrieval.xhtml";
    }

    public String showPassword() {
        if (password != null) {
            return "Your Password is " + password;
        } else {
            return "";
        }
    }

    public String getRandomQuestion() {
        ArrayList<String> que = new ArrayList<String>();
        que.add("What is your dream job?");
        que.add("What was the name of your first pet?");
        que.add("What was the first book you ever read?");

        Random ran = new Random();
        int temp = ran.nextInt(3);

        this.setQuestion(que.get(temp));
        return que.get(temp);
    }

    public String invalidateSession() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }
}
