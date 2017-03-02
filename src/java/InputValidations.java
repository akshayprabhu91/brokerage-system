/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brokeragesystem_fall2016;

import java.util.Scanner;

/**
 *
 * @author SONY
 */
public class InputValidations {

    public String getShare(String buyOrSell) {
        DatabaseFunctions db = new DatabaseFunctions();
        Scanner scr = new Scanner(System.in);

        boolean flag = false;
        String share = "";

        //check if the user enters a valid share
        while (!flag) {
            System.out.println("Which shares will you like to " + buyOrSell + "? Please enter the stock symbol: ");
            share = scr.nextLine();

            //check if it is a valid share
            if (!db.ifRecordsExist("select Symbol from stocks where Symbol = '" + share + "'")) {
                System.out.println("**** Please enter a valid share ****");
            } else {
                flag = true;
            }
        }
        return share;
    }

    public int getNumOfShares(String buyOrSell) {
        Scanner scr = new Scanner(System.in);

        int num = 0;
        while (num <= 0) {
            System.out.println("How many shares do you wish to " + buyOrSell + "? (Enter number greater than 0)");
            num = scr.nextInt();
            scr.nextLine();
        }
        return num;
    }

    public double getPrice(int num, String share, String buyOrSell) {
        Scanner scr = new Scanner(System.in);

        double price = 0;
        while (price <= 0) {
            System.out.println("At what price are you willing to " + buyOrSell + " " + num + " shares of " + share + "? (Enter value greater than 0)");
            price = scr.nextDouble();
            scr.nextLine();
        }
        return price;
    }

    public double depositMoney(String id, double balance) {
        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        double deposit = 0;
        String input = "";

        System.out.println();
        System.out.println("**** You do not have sufficient funds in your account ****");
        System.out.println("Deposit money now? (Y/N)");

        input = scr.nextLine();

        if (input.equalsIgnoreCase("Y")) {

            while (deposit <= 0) {
                System.out.println("Please enter the amount (greater than 0)");
                deposit = scr.nextDouble();
                scr.nextLine();
            }

            double newBalance = (balance + deposit);
            db.insertUpdateDelete("update onlineaccount set balance = '" + newBalance + "' where LoginID = '" + id + "'");
            System.out.println("#### Amount successfully deposited into your account. New Balance: " + newBalance);
            System.out.println();
            return newBalance;
        } else {
            System.out.println("**** Trade cannot be completed due to lack of funds ****");
            return balance;
        }
    }
}
