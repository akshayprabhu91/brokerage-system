/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brokeragesystem_fall2016;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author PrabhuA6510
 */
public class OnlineSystem {

    private OnlineAccount theLoginAccount;

    public OnlineSystem() {
        theLoginAccount = null;
    }

    public void showMainPage() {
        Scanner scr = new Scanner(System.in);
        String selection = "";

        while (!selection.equalsIgnoreCase("x")) {
            //menu
            System.out.println();
            System.out.println("Welcome to UHCL Bank");
            System.out.println("Please make your selection");
            System.out.println("1. Create a new online ID");
            System.out.println("2. Login to your online account");
            System.out.println("3. Password retrieval");
            System.out.println("x. Finish the Simulation");

            //get the selection from the user
            selection = scr.nextLine();
            System.out.println();

            if (selection.equals("1")) {
                register();
            } else if (selection.equals("2")) {
                login();
            } else if (selection.equals("3")) {
                passwordRetrieval();
            }
        }
    }

    public void register() {
        //declare local variables

        double balance = 0;
        String accountNum = "";

        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        //prompt input
        System.out.println("Enter your SSN");
        String ssn = scr.nextLine();

        System.out.println("Enter your full Name");
        String fullName = scr.nextLine();

        System.out.println("Enter the new Login ID");
        String loginID = scr.nextLine();

        System.out.println("Enter the password");
        String password = scr.nextLine();

        System.out.println("Do you wish to deposit money now? (Y/N)");
        String input = scr.nextLine();

        boolean flag = false;

        while (!flag) {
            if ("N".equalsIgnoreCase(input)) {
                balance = 0;
                flag = true;
            } else if ("Y".equalsIgnoreCase(input)) {
                System.out.println("Please enter the amount");
                balance = scr.nextDouble();
                scr.nextLine();
                flag = true;
            } else {
                System.out.println("**** INVALID INPUT. PLEASE TRY AGAIN ****");
            }
        }

        System.out.println();
        System.out.println("#### Answer the Security questions in case you forget the password ####");
        System.out.println("What is your dream job?");
        String answerOne = scr.nextLine();
        System.out.println("What was the name of your first pet?");
        String answerTwo = scr.nextLine();
        System.out.println("What was the first book you ever read?");
        String answerThree = scr.nextLine();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            //making sure ssn is not used or id is not used
            rs = stat.executeQuery("select * from onlineaccount where LoginId = '" + loginID + "' or ssn = '" + ssn + "'");
            if (rs.next()) {
                System.out.println("**** ACCOUNT ALREADY EXISTS. ACCOUNT CREATION FAILED. ****");
            } else {
                rs = stat.executeQuery("select * from nextaccountnumber");
                int nextNum = 0;
                while (rs.next()) {
                    accountNum = "" + rs.getInt(1);
                    nextNum = rs.getInt(1) + 1;
                }

                //update the next Account Number
                db.insertUpdateDelete("update nextaccountnumber set NextID = '" + nextNum + "'");
                //insert record into onlineaccount table
                db.insertUpdateDelete("insert into onlineaccount values('" + accountNum + "','" + loginID + "', '" + fullName + "', '" + ssn + "', '" + password + "','" + balance + "')");
                db.insertUpdateDelete("insert into password_retrieval values('" + accountNum + "', 'What is your dream job?', '" + answerOne + "', 'What was the name of your first pet?', '" + answerTwo + "', 'What was the first book you ever read?', '" + answerThree + "')");
                db.insertUpdateDelete("insert into failedloginattempts values ('" + loginID + "', 0)");
                System.out.println("Account Creation Successful!");
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("**** ACCOUNT CREATION FAILED ****");
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

    public void login() {
        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        //prompt login
        System.out.println("Please enter your LoginID");
        String loginID = scr.nextLine();
        System.out.println("Please enter your Password");
        String password = scr.nextLine();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from onlineaccount where LoginID = '" + loginID + "'");
            int loginCount = db.getLoginCount(loginID);

            if (rs.next()) {
                if (loginCount < 2) {
                    //check the password
                    if (password.equals(rs.getString(5))) {
                        //if login is successful, making the logincount as 0
                        db.insertUpdateDelete("update failedloginattempts set LoginCount = 0 where LoginID = '" + loginID + "'");
                        //login successful
                        theLoginAccount = new OnlineAccount(rs.getString(4), loginID, password, rs.getDouble(6));
                        theLoginAccount.welcome(rs.getString(3));
                    } else {
                        System.out.println("**** PASSWORD IS INCORRECT ****");
                        loginCount = db.getLoginCount(loginID);
                        db.insertUpdateDelete("update failedloginattempts set LoginCount = '" + (++loginCount) + "' where LoginID = '" + loginID + "'");
                    }
                } else {
                    System.out.println();
                    System.out.println("**** Your account is Locked. You will be forwarded to the Password Retrieval page. ****");
                    //forwarding to the Password Retrieval page
                    passwordRetrieval();
                }
            } else {
                System.out.println("**** LOGIN ID NOT FOUND ****");
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

    public void passwordRetrieval() {
        int temp = 0;
        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        ArrayList<String> que = new ArrayList<String>();
        que.add("What is your dream job?");
        que.add("What was the name of your first pet?");
        que.add("What was the first book you ever read?");

        System.out.println("Please enter the Login ID");
        String loginID = scr.nextLine();

        //Check whether the loginID is present in the onlineaccount table
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from onlineaccount where LoginID = '" + loginID + "'");
            if (rs.next()) {
                String password = rs.getString(5);
                String accountNum = rs.getString(1);

                rs = stat.executeQuery("select * from password_retrieval where AccountNum = '" + accountNum + "'");
                Random ran = new Random();

                //generate a random number between 0 and 2
                temp = ran.nextInt(3);

                //get a random question
                String question = que.get(temp);
                //prompt the question
                System.out.println(question);
                String answer = scr.nextLine();

                if (rs.next()) {
                    if (rs.getString(2).equalsIgnoreCase(question)) {
                        if (rs.getString(3).equalsIgnoreCase(answer)) {
                            System.out.println("#### Your Password is: " + password);
                            //Unlocking the account after displaying the password so the user can now login
                            db.insertUpdateDelete("update failedloginattempts set LoginCount = 0 where LoginID = '" + loginID + "'");
                        } else {
                            System.out.println("**** Wrong Answer. Password-Retrieval failed****");
                        }
                    } else if (rs.getString(4).equalsIgnoreCase(question)) {
                        if (rs.getString(5).equalsIgnoreCase(answer)) {
                            System.out.println("#### Your Password is: " + password);
                            //Unlocking the account after displaying the password so the user can now login
                            db.insertUpdateDelete("update failedloginattempts set LoginCount = 0 where LoginID = '" + loginID + "'");
                        } else {
                            System.out.println("**** Wrong Answer. Password-Retrieval failed****");
                        }
                    } else if (rs.getString(6).equalsIgnoreCase(question)) {
                        if (rs.getString(7).equalsIgnoreCase(answer)) {
                            System.out.println("#### Your Password is: " + password);
                            //Unlocking the account after displaying the password so the user can now login
                            db.insertUpdateDelete("update failedloginattempts set LoginCount = 0 where LoginID = '" + loginID + "'");
                        } else {
                            System.out.println("**** Wrong Answer. Password-Retrieval failed****");
                        }
                    }
                }
            } else {
                System.out.println("**** LoginID does NOT exist ****");
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
}