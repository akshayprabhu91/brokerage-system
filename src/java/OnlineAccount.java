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
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author PrabhuA6510
 */
public class OnlineAccount {

    //attributes
    private String ssn;
    private String id;
    private String psw;
    private double balance;

    public OnlineAccount(String s, String i, String p, double b) {
        this.ssn = s;
        this.id = i;
        this.psw = p;
        this.balance = b;
    }

    //display the welcome message and options
    public void welcome(String name) {
        Scanner scr = new Scanner(System.in);
        String selection = "";

        while (!selection.equalsIgnoreCase("x")) {
            //update stock database
            updateStockDatabase();

            System.out.println();
            System.out.println("Hello " + name + "!");
            System.out.println("Current Account Balance: $" + balance);
            System.out.println("Please select an option: ");
            System.out.println("t: Trade");
            System.out.println("w: Display watch list");
            System.out.println("s: Search");
            System.out.println("d: Display orders");
            System.out.println("x: LogOut");

            selection = scr.next();

            if (selection.equalsIgnoreCase("t")) {
                //start Trading
                trade();
            } else if (selection.equalsIgnoreCase("w")) {
                //display watch list
                displayWatchList();
            } else if (selection.equalsIgnoreCase("s")) {
                //search a particular stock
                search();
            } else if (selection.equalsIgnoreCase("d")) {
                //display orders
                displayOrders();
            } else if (selection.equalsIgnoreCase("x")) {
                ;
            } else {
                System.out.println("**** INVALID INPUT. PLEASE TRY AGAIN. ****");
            }
        }
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void trade() {
        Scanner scr = new Scanner(System.in);
        String selection = "";

        while (!selection.equalsIgnoreCase("x")) {
            System.out.println();
            System.out.println("b: Buy Stocks");
            System.out.println("s: Sell Stocks");
            System.out.println("x: Exit");

            selection = scr.nextLine();

            if (selection.equalsIgnoreCase("b")) {
                buyShares();
            } else if (selection.equalsIgnoreCase("s")) {
                sellShares();
            } else if (selection.equalsIgnoreCase("x")) {
                ;
            } else {
                System.out.println("**** INVALID INPUT. PLEASE TRY AGAIN. ****");
            }
        }
    }

    public void buyShares() {
        DatabaseFunctions db = new DatabaseFunctions();
        InputValidations in = new InputValidations();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        String selection = "";

        Scanner scr = new Scanner(System.in);
        System.out.println();

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            while (!selection.equalsIgnoreCase("x")) {
                int num = 0;
                double price = 0;
                String share = "";
                boolean flagOne = false;

                System.out.println("#### BUY STOCKS ####");
                System.out.println("1: Limit Order");
                System.out.println("2: Market Order");
                System.out.println("x: Exit");

                selection = scr.nextLine();

                if (selection.equalsIgnoreCase("1")) {
                    System.out.println();
                    System.out.println("#### LIMIT ORDER");

                    //check if the user enters a valid share
                    share = in.getShare("buy");

                    rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and MinSellingPrice > 0 order by MinSellingPrice");

                    if (rs.next()) {
                        System.out.println();
                        System.out.println("#### Available shares of " + share + ":");
                        System.out.printf("%20s%20s%20s%20s\n", "Share", "Total Available", "Price per share", "Seller");
                        rs.beforeFirst();
                        //display the available shares for sale
                        while (rs.next()) {
                            System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                        }

                        num = in.getNumOfShares(share);
                        rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and NumberOfShares >= " + num + " and MinSellingPrice > 0 order by MinSellingPrice");

                        if (rs.next()) {
                            System.out.println();
                            System.out.println("#### Available " + num + " shares of " + share + ":");
                            System.out.printf("%20s%20s%20s%20s\n", "Share", "Total Available", "Price per share", "Seller");
                            //display the current share returned
                            rs.beforeFirst();
                            //display the available shares for sale
                            while (rs.next()) {
                                System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                            }

                            System.out.println();
                            System.out.println("#### Your available balance is: " + balance);
                            price = in.getPrice(num, share, "buy");

                            rs.beforeFirst();
                            rs.next();
                            if (balance < rs.getDouble(3)) {
                                this.setBalance(in.depositMoney(id, balance));
                            }

                            rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and NumberOfShares >= " + num + " and MinSellingPrice <= " + price + " and MinSellingPrice > 0 order by MinSellingPrice");

                            if (rs.next()) {
                                //buy the first stock which is found. Since, this is a limit order
                                if (rs.getInt(2) == num) {
                                    //update buyer databases
                                    db.insertUpdateDelete("delete from salesorder where ShareSymbol = '" + share + "' and NumberOfShares = " + num + " and MinSellingPrice = " + rs.getDouble(3));
                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + id + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + num + ", 'BUY')");
                                    db.insertUpdateDelete("update stocks set RecentTradedPrice = " + price + " where Symbol = '" + share + "'");
                                    //update the user balance
                                    db.insertUpdateDelete("update onlineaccount set balance = " + (balance - price) + " where LoginID = '" + id + "'");
                                    //modifying the current object
                                    this.setBalance(balance - price);
                                    //update the count of shares in users account
                                    if (db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                                        db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) + num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                    } else {
                                        db.insertUpdateDelete("insert into myshares values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + num + ")");
                                    }

                                    //update seller databases
                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + rs.getString(4) + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + num + ", 'SELL')");
                                    db.insertUpdateDelete("update onlineaccount set balance = " + (db.getUserBalance(rs.getString(4)) + price) + " where LoginID = '" + rs.getString(4) + "'");
                                } else {
                                    db.insertUpdateDelete("update salesorder set NumberOfShares = " + (rs.getInt(2) - num) + " where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MinSellingPrice = " + rs.getDouble(3));
                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + id + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + num + ", 'BUY')");
                                    db.insertUpdateDelete("update stocks set RecentTradedPrice = " + price + " where Symbol = '" + share + "'");
                                    //update the user balance
                                    db.insertUpdateDelete("update onlineaccount set balance = " + (balance - price) + " where LoginID = '" + id + "'");
                                    //modifying the current object
                                    this.setBalance(balance - price);
                                    //update the count of shares in users account
                                    if (db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                                        db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) + num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                    } else {
                                        db.insertUpdateDelete("insert into myshares values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + num + ")");
                                    }

                                    //update seller databases
                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + rs.getString(4) + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + num + ", 'SELL')");
                                    db.insertUpdateDelete("update onlineaccount set balance = " + (db.getUserBalance(rs.getString(4)) + price) + " where LoginID = '" + rs.getString(4) + "'");
                                }
                                System.out.println("#### You bought " + num + " shares of " + share + " for $" + price + "");
                                System.out.println("#### Trade Completed");
                            } else {
                                //User Quoted a lesser price
                                System.out.println("**** You Quoted a much lesser price. NO Stock available at this price. ****");
                                System.out.println("**** Trade cannot be completed ****");
                                db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + num + "', '" + price + "', '" + id + "')");
                                System.out.println("#### Your request has been added to the pending orders ####");
                                System.out.println();
                            }
                        } else {
                            System.out.println("**** Currently there are NO " + num + " shares available of " + share + " at the same rate ****");
                            System.out.println("**** Trade Cannot be Completed ****");
                            System.out.println("#### Your request will be added to the pending orders ####");
                            price = in.getPrice(num, share, "buy");
                            db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + num + "', '" + price + "', '" + id + "')");
                            System.out.println("#### Your request has been added to the pending orders ####");
                        }
                    } else {
                        System.out.println("**** Currently NO " + share + " share available for sale ****");
                        System.out.println("**** Trade Cannot be Completed ****");
                        System.out.println("#### Your request will be added to the pending orders ####");

                        num = in.getNumOfShares("buy");
                        System.out.println();
                        price = in.getPrice(num, share, "buy");

                        db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + num + "', '" + price + "', '" + id + "')");
                        System.out.println("#### Your request has been added to the pending orders ####");
                    }
                } else if (selection.equalsIgnoreCase("2")) {
                    System.out.println();
                    System.out.println("#### MARKET ORDER");

                    share = in.getShare("buy");
                    num = in.getNumOfShares("buy");
                    price = 0;

                    rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and MinSellingPrice > 0 order by MinSellingPrice");

                    if (rs.next()) {
                        System.out.println("#### Available Shares of " + share + " in the Pending Sales Order");
                        System.out.printf("%20s%20s%20s%20s\n", "ShareSymbol", "NumberOfShares", "MinSellingPrice", "Seller");
                        rs.beforeFirst();
                        while (rs.next()) {
                            System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                        }

                        System.out.println("#### Your account balance is " + balance);

                        flagOne = false;
                        rs.beforeFirst();
                        //buy the shares with Minimum Selling Price first
                        while (rs.next()) {
                            if (num == rs.getInt(2)) {
                                System.out.println("#### Buying " + num + " shares for $" + rs.getDouble(3) + "");
                                //buying all the stocks at minimum price
                                //updating buyer records
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'BUY')");
                                if (!db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                                    db.insertUpdateDelete("insert into myshares values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + num + ")");
                                } else {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) + num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                }
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (balance - rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                this.setBalance((balance - rs.getDouble(3)));

                                //updating seller records
                                db.insertUpdateDelete("delete from salesorder where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MinSellingPrice = " + rs.getDouble(3) + "");
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) + rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                num = 0;
                                break;
                            } else if (num < rs.getInt(2)) {
                                System.out.println("#### Buying " + num + " shares for $" + rs.getDouble(3) + "");
                                //updating buyer records
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'BUY')");
                                if (!db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                                    db.insertUpdateDelete("insert into myshares values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + num + ")");
                                } else {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) + num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                }
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (balance - rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                this.setBalance((balance - rs.getDouble(3)));

                                //updating seller records
                                db.insertUpdateDelete("update salesorder set NumberOfShares = " + (rs.getInt(2) - num) + " where ShareSymbol = '" + share + "' and MinSellingPrice = " + rs.getDouble(3) + " and Seller = '" + rs.getString(4) + "'");
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) + rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                num = 0;
                                break;
                            } else if (num > rs.getInt(2)) {
                                int temp = num;
                                num = (num - rs.getInt(2));
                                //updating buyer records
                                System.out.println("#### Buying " + rs.getInt(2) + " shares for $" + rs.getDouble(3) + "");
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'BUY')");
                                while (!flagOne) {
                                    if (!db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                                        db.insertUpdateDelete("insert into myshares values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + temp + ")");
                                    } else {
                                        db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) + temp) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                    }
                                    flagOne = true;
                                }

                                db.insertUpdateDelete("update onlineaccount set Balance = " + (balance - rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                this.setBalance((balance - rs.getDouble(3)));

                                //updating seller records
                                db.insertUpdateDelete("delete from salesorder where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MinSellingPrice = " + rs.getDouble(3) + "");
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) + rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                            }
                        }
                        if (num > 0) {
                            //adding the remaining shares to pending orders
                            db.insertUpdateDelete("insert into purchaseorders values('" + share + "', " + num + ", " + price + ", '" + id + "')");
                            System.out.println("#### Remaining " + num + " shares of " + share + " have been added in the Pending Purchase Orders");
                            System.out.println("#### Trade Partially Completed");
                        } else {
                            System.out.println("#### Trade successfully completed");
                        }
                    } else {
                        System.out.println("**** Currently No shares of " + share + " are available in the Pending Sales Orders ****");
                        db.insertUpdateDelete("insert into purchaseorders values('" + share + "', " + num + ", " + price + ", '" + id + "')");
                        System.out.println("#### Your request has been added into the Pending Purchase Orders");
                    }
                } else if (selection.equalsIgnoreCase("x")) {
                    ;
                } else {
                    System.out.println("**** INVALID INPUT. PLEASE TRY AGAIN. ****");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //rs.close();
                stat.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sellShares() {
        DatabaseFunctions db = new DatabaseFunctions();
        InputValidations in = new InputValidations();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        String selection = "";

        Scanner scr = new Scanner(System.in);
        System.out.println();

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            while (!selection.equalsIgnoreCase("x")) {

                String share = "";
                boolean flag = false;
                boolean flagOne = false;
                int num = 0;
                double price = 0;

                System.out.println("#### SELL STOCKS ####");
                System.out.println("1: Limit Order");
                System.out.println("2: Market Order");
                System.out.println("x: Exit");

                selection = scr.nextLine();

                if (selection.equalsIgnoreCase("1")) {

                    System.out.println();
                    System.out.println("#### LIMIT ORDER");

                    //display a list of stocks that the user possess
                    rs = stat.executeQuery("select * from myshares where LoginID = '" + id + "'");
                    System.out.println();
                    System.out.println("#### List of Stocks you possess");
                    System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                    while (rs.next()) {
                        System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                    }
                    System.out.println();

                    //check if the user enters a valid share
                    while (!flag) {
                        share = in.getShare("sell");
                        if (db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'")) {
                            flag = true;
                        } else {
                            System.out.println("**** You DO NOT possess this share. Please try again. ****");
                        }
                    }

                    flag = false;

                    System.out.println();
                    //display the shares desired by the user
                    rs.beforeFirst();
                    System.out.println("#### List of " + share + " stocks you possess");
                    System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                    while (rs.next()) {
                        if (rs.getString(2).equalsIgnoreCase(share)) {
                            System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                        }
                    }

                    while (!flag) {
                        num = 0;
                        num = in.getNumOfShares("sell");

                        //Check if the user owns right amount of shares of that particular share, so that he/she is eligible to sell them                    
                        rs = stat.executeQuery("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "' and NumberOfShares >= " + num);

                        if (rs.next()) {
                            price = in.getPrice(num, share, "sell");

                            //we can now sell the stocks
                            //check whether there is any pending purchase order that can buy this particular stock
                            rs = stat.executeQuery("select * from purchaseorders where ShareSymbol = '" + share + "' and NumberOfShares <= " + num + " and MaxBuyingPrice >= " + price + " order by MaxBuyingPrice desc");

                            if (rs.next()) {
                                //found a pending order
                                System.out.println("#### Pending Purchase Order that matches your requirement");
                                rs.beforeFirst();
                                System.out.printf("%20s%20s%20s%20s\n", "Symbol", "Number_Of_Shares", "Max_Buying_Price", "Buyer");
                                while (rs.next()) {
                                    System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                                }

                                rs.beforeFirst();
                                while (rs.next()) {
                                    //sell the stock to all the eligible buyers
                                    //First updating the seller accounts
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) - rs.getInt(2)) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");

                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + rs.getInt(2) + ", 'SELL')");
                                    db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                    //updating the seller balance
                                    db.insertUpdateDelete("update onlineaccount set Balance = " + (balance + rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                    //updating the current object
                                    this.setBalance(balance + rs.getDouble(3));

                                    //Now update the buyer accounts                        
                                    db.insertUpdateDelete("delete from purchaseorders where ShareSymbol = '" + rs.getString(1) + "' and NumberOfShares = " + rs.getInt(2) + " and MaxBuyingPrice = " + rs.getDouble(3) + "");
                                    db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + rs.getString(4) + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + rs.getInt(2) + ", 'BUY')");

                                    //update the buyers balance 
                                    db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) - rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");

                                    //finding the buyers shares from myshares table.. so that the count of shares can be updated.
                                    if (db.ifRecordsExist("select * from myshares where LoginID = '" + rs.getString(4) + "' and Symbol = '" + share + "'")) {
                                        db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(rs.getString(4), share) + rs.getInt(2)) + " where LoginID = '" + rs.getString(4) + "' and Symbol = '" + share + "'");
                                    } else {
                                        db.insertUpdateDelete("insert into myshares values('" + rs.getString(4) + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getInt(2) + ")");
                                    }
                                }
                                System.out.println("#### Sold " + rs.getInt(2) + " shares for $" + rs.getDouble(3));
                                System.out.println("#### Trade Successfully Completed.");
                            } else {
                                //update the count of stocks in seller accounts after putting the order in sales order (If no potential buyer is available)
                                if ((db.getTotalShares(id, share) - num) > 0) {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) - num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                } else {
                                    db.insertUpdateDelete("delete from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                }

                                //did not find a pending order
                                System.out.println("**** Currently NO Pending Purchase Order match your requirement ****");
                                System.out.println("#### Your sales order will be added to the Queue until a potential buyer is found");
                                db.insertUpdateDelete("insert into salesorder values('" + share + "', '" + num + "', '" + price + "', '" + id + "')");
                                System.out.println("**** Trade not completed ****");
                            }
                            flag = true;
                        } else {
                            System.out.println("**** You DO NOT have " + num + " shares of " + share + " to sell. ****");
                            System.out.println("**** Please enter the correct number of shares you wish to sell ****");
                            System.out.println();
                        }
                    }
                } else if (selection.equalsIgnoreCase("2")) {
                    System.out.println();
                    System.out.println("#### MARKET ORDER");

                    //display a list of stocks that the user possess
                    rs = stat.executeQuery("select * from myshares where LoginID = '" + id + "'");
                    System.out.println();
                    System.out.println("#### List of Stocks you possess");
                    System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                    while (rs.next()) {
                        System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                    }
                    System.out.println();

                    //check if the user possess this particular share
                    flag = false;
                    while (!flag) {
                        share = in.getShare("sell");

                        rs = stat.executeQuery("select * from myshares where Symbol = '" + share + "' and LoginID = '" + id + "'");
                        if (rs.next()) {
                            flag = true;
                        } else {
                            System.out.println("**** You DO NOT possess this share ****");
                        }
                    }
                    flag = false;

                    //display the shares desired by the user
                    rs.beforeFirst();
                    System.out.println("#### List of " + share + " stocks I possess");
                    System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                    while (rs.next()) {
                        if (rs.getString(2).equalsIgnoreCase(share)) {
                            System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                        }
                    }
                    System.out.println();
                    while (!flag) {
                        num = in.getNumOfShares("sell");
                        price = 0;
                        if (db.ifRecordsExist("select * from myshares where LoginID = '" + id + "' and Symbol = '" + share + "' and NumberOfShares >= " + num + "")) {
                            //we can now sell the stocks
                            //check whether there is any pending purchase order that can buy this particular stock
                            rs = stat.executeQuery("select * from purchaseorders where ShareSymbol = '" + share + "' order by MaxBuyingPrice desc");

                            if (rs.next()) {
                                //found a pending order
                                System.out.println("#### Pending Purchase Order that matches your requirement");
                                rs.beforeFirst();
                                System.out.printf("%20s%20s%20s%20s\n", "Symbol", "Number_Of_Shares", "Max_Buying_Price", "Buyer");
                                while (rs.next()) {
                                    System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                                }

                                flagOne = false;
                                rs.beforeFirst();
                                while (rs.next()) {
                                    if (num == rs.getInt(2)) {
                                        System.out.println("#### Sold " + num + " shares for $" + rs.getDouble(3) + "");
                                        //update seller accounts
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'SELL')");
                                        db.insertUpdateDelete("delete from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (balance + rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                        this.setBalance(balance + rs.getDouble(3));

                                        //update buyer accounts
                                        db.insertUpdateDelete("delete from purchaseorders where ShareSymbol = '" + share + "' and NumberOfShares = " + num + " and MaxBuyingPrice = " + rs.getDouble(3) + " and Buyer = '" + rs.getString(4) + "'");
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + rs.getString(4) + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'BUY')");
                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) - rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");

                                        db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                        num = 0;
                                        break;
                                    } else if (num < rs.getInt(2)) {
                                        System.out.println("#### Sold " + num + " shares for $" + rs.getDouble(3) + "");
                                        //update seller accounts
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'SELL')");
                                        db.insertUpdateDelete("delete from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (balance + rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                        this.setBalance(balance + rs.getDouble(3));

                                        //update buyer accounts
                                        db.insertUpdateDelete("update purchaseorders set NumberOfShares = " + (rs.getInt(2) - num) + " where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MaxBuyingPrice = " + rs.getDouble(3) + " and Buyer = '" + rs.getString(4) + "'");
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + rs.getString(4) + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + num + ", 'BUY')");
                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) - rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");

                                        db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                        num = 0;
                                        break;
                                    } else if (num > rs.getInt(2)) {
                                        int temp = num;
                                        num = (num - rs.getInt(2));
                                        System.out.println("#### Sold " + rs.getInt(2) + " shares for $" + rs.getDouble(3) + "");
                                        //update seller accounts
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + id + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + rs.getInt(2) + ", 'SELL')");

                                        while (!flagOne) {
                                            db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) - temp) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                            flagOne = true;
                                        }

                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (balance + rs.getDouble(3)) + " where LoginID = '" + id + "'");
                                        this.setBalance(balance + rs.getDouble(3));

                                        //update buyer accounts
                                        db.insertUpdateDelete("delete from purchaseorders where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MaxBuyingPrice = " + rs.getDouble(3) + " and Buyer = '" + rs.getString(4) + "'");
                                        db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values ('" + rs.getString(4) + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + rs.getInt(2) + ", 'BUY')");
                                        db.insertUpdateDelete("update onlineaccount set Balance = " + (db.getUserBalance(rs.getString(4)) - rs.getDouble(3)) + " where LoginID = '" + rs.getString(4) + "'");

                                        db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                    }
                                }
                                if (num > 0) {
                                    System.out.println("#### Adding the remaining " + num + " shares of " + share + " into the Pending Sales Order");
                                    db.insertUpdateDelete("insert into salesorder values('" + share + "', " + num + ", " + price + ", '" + id + "')");
                                    System.out.println("#### Trade Partially completed");
                                } else {
                                    System.out.println("#### Trade Successfully Completed");
                                }
                            } else {
                                //update the count of stocks in seller accounts after putting the order in sales order (If no potential buyer is available)
                                if ((db.getTotalShares(id, share) - num) > 0) {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(id, share) - num) + " where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                } else {
                                    db.insertUpdateDelete("delete from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'");
                                }

                                //did not find a pending order
                                System.out.println("**** Currently NO Pending Purchase Order match your requirement ****");
                                System.out.println("#### Your sales order will be added to the Queue until a potential buyer is found");
                                db.insertUpdateDelete("insert into salesorder values('" + share + "', '" + num + "', '" + price + "', '" + id + "')");
                                System.out.println("**** Trade NOT Completed ****");
                            }
                            flag = true;
                        } else {
                            System.out.println("**** You DO NOT have " + num + " shares of " + share + " to sell. ****");
                            System.out.println("**** Please enter the correct number of shares you wish to sell ****");
                            System.out.println();
                        }
                    }
                } else if (selection.equalsIgnoreCase("x")) {
                    ;
                } else {
                    System.out.println("**** INVALID INPUT. PLEASE TRY AGAIN. ****");
                }
                System.out.println();
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

    public void search() {
        String stock = "";
        String input = "";

        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        System.out.println();
        System.out.println("#### Search for a particular stock");
        System.out.println("Enter the Symbol or the name of the Stock to search for: ");

        stock = scr.nextLine();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from stocks where Symbol like '%" + stock + "%' or StockName like '%" + stock + "%'");

            if (rs.next()) {
                //found the stock
                System.out.println();
                System.out.printf("%20s%20s%20s%20s%20s%20s%20s\n", "Symbol", "Stock Name", "RecentTradedPrice", "MaxBuyingPrice", "SharesAtMaxPrice", "MinSellingPrice", "SharesAtMinPrice");
                System.out.printf("%20s%20s%20f%20f%20d%20f%20f\n", rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
                System.out.println();

                System.out.println("Do you wish to add this stock to the watch list? (Y/N)");
                input = scr.nextLine();

                if (input.equalsIgnoreCase("Y")) {
                    if (!db.ifRecordsExist("select * from watchlist where Symbol = '" + stock + "' and LoginID = '" + id + "'")) {
                        db.insertUpdateDelete("insert into watchlist values('" + id + "', '" + rs.getString(1) + "')");
                        System.out.println("#### Stock has been successfully added to your watchlist");
                    } else {
                        System.out.println("#### Stock " + stock + " is already present in your watchlist. Hence, Not added again.");
                    }
                    System.out.println();
                } else {
                    System.out.println("#### You will be directed back to online account homepage");
                    System.out.println();
                }
            } else {
                System.out.println("**** NOT A VALID STOCK ****");
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

    public void displayWatchList() {
        String input = "";
        String stockToRemove = "";

        Scanner scr = new Scanner(System.in);
        DatabaseFunctions db = new DatabaseFunctions();

        System.out.println();

        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            List<String> lst = db.getStringValues("select Symbol from watchlist where LoginID = '" + id + "'");

            if (db.ifRecordsExist("select Symbol from watchlist where LoginID = '" + id + "'")) {

                System.out.println("#### Your Watch-List / Favorite Stocks");
                System.out.printf("%20s%20s%20s%20s%20s%20s%20s\n", "Symbol", "Stock Name", "RecentTradedPrice", "MaxBuyingPrice", "SharesAtMaxPrice", "MinSellingPrice", "SharesAtMinPrice");

                for (String index : lst) {
                    rs = stat.executeQuery("select * from stocks where Symbol = '" + index + "'");
                    rs.next();
                    System.out.printf("%20s%20s%20f%20f%20d%20f%20f\n", rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getInt(5), rs.getDouble(6), rs.getDouble(7));
                }

                System.out.println();
                System.out.println("Do you wish to remove any stock from your favorites list? (Y/N)");
                input = scr.nextLine();

                if (input.equalsIgnoreCase("Y")) {
                    System.out.println("Enter the symbol of the stock you wish to remove: ");
                    stockToRemove = scr.nextLine();

                    if (db.ifRecordsExist("select * from watchlist where Symbol = '" + stockToRemove + "' and LoginID = '" + id + "'")) {
                        db.insertUpdateDelete("delete from watchlist where Symbol = '" + stockToRemove + "' and LoginID = '" + id + "'");
                        System.out.println("#### Stock " + stockToRemove + " successfully removed from your watch-list");
                    } else {
                        System.out.println("**** No such Stock in your watch-list");
                    }
                    System.out.println();
                } else {
                    System.out.println("#### You will be directed back to online account homepage");
                }
            } else {
                System.out.println("**** You have NOT added stocks to your Watch-List as of now");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //rs.close();
                stat.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void displayOrders() {
        DatabaseFunctions db = new DatabaseFunctions();
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            if (db.ifRecordsExist("select * from myorders where LoginID = '" + id + "'")) {
                System.out.println();
                System.out.println("#### Most Recent Orders");

                rs = stat.executeQuery("select * from (select * from myorders where LoginID = '" + id + "' order by TransactionNumber desc) TEMP limit 5");

                System.out.printf("%20s%20s%20s%20s%20s\n", "Symbol", "Stock_Name", "Traded_Price", "Number_of_Shares", "Trade_Type");
                while (rs.next()) {
                    System.out.printf("%20s%20s%20f%20d%20s\n", rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getInt(6), rs.getString(7));
                }
            } else {
                System.out.println("**** You have NOT performed any transactions as of now ****");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stat.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateStockDatabase() {
        DatabaseFunctions db = new DatabaseFunctions();
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();

            List<String> lst = db.getStringValues("select Symbol from stocks");

            for (String index : lst) {
                double MaxBuyingPrice = 0;
                int NumOfSharesAvailableAtMaxPrice = 0;
                double MinSellingPrice = 0;
                int NumOfSharesToBeSoldAtMinPrice = 0;

                rs = stat.executeQuery("select * from purchaseorders where ShareSymbol = '" + index + "' order by MaxBuyingPrice desc");

                if (rs.next()) {
                    MaxBuyingPrice = rs.getDouble("MaxBuyingPrice");
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getDouble("MaxBuyingPrice") == MaxBuyingPrice) {
                            NumOfSharesAvailableAtMaxPrice += rs.getInt("NumberOfShares");
                        }
                    }
                }

                rs.beforeFirst();

                rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + index + "' order by MinSellingPrice");

                if (rs.next()) {
                    MinSellingPrice = rs.getDouble("MinSellingPrice");
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getDouble("MinSellingPrice") == MinSellingPrice) {
                            NumOfSharesToBeSoldAtMinPrice += rs.getInt("NumberOfShares");
                        }
                    }
                }
                db.insertUpdateDelete("update stocks set MaxBuyingPrice = " + MaxBuyingPrice + ", NumOfSharesAvailableAtMaxPrice = " + NumOfSharesAvailableAtMaxPrice + ", MinSellingPrice = " + MinSellingPrice + ", NumOfSharesToBeSoldAtMinPrice = " + NumOfSharesToBeSoldAtMinPrice + " where Symbol = '" + index + "'");
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
