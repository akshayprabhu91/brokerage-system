/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author SONY
 */
@Named(value = "loginAccount")
@SessionScoped
@ManagedBean
public class loginAccount implements Serializable {
    
    private String ssn;
    private String loginID;
    private String fullName;
    private String pwd;
    private double balance;

    //attributes for tradein
    private String tradeType;
    private String share;
    private int numOfShares;
    private double price;
    private String message;
    
    public String getShare() {
        return share;
    }
    
    public void setShare(String share) {
        this.share = share;
    }
    
    public String getTradeType() {
        return tradeType;
    }
    
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
    
    public int getNumOfShares() {
        return numOfShares;
    }
    
    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public loginAccount() {
        
    }
    
    public loginAccount(String s, String l, String f, String p, double b) {
        this.ssn = s;
        this.loginID = l;
        this.fullName = f;
        this.pwd = p;
        this.balance = b;
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
    
    public String getSsn() {
        return ssn;
    }
    
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String checkLogin() {
        //load the Driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return ("internalError");
        }
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from onlineaccount where LoginID = '" + loginID + "'");
            if (rs.next()) {
                if (pwd.equals(rs.getString(5))) {
                    this.loginID = rs.getString(2);
                    this.fullName = rs.getString(3);
                    this.ssn = rs.getString(4);
                    this.pwd = rs.getString(5);
                    this.balance = rs.getDouble(6);
                    return ("welcome.xhtml");
                } else {
                    return ("loginNotOK.xhtml");
                }
            } else {
                return ("loginNotOK.xhtml");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
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
    
    public String logOut() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }
    
    public ArrayList<Order> fetchRecentOrders() {
        //load the Driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return null;
        }
        
        ArrayList<Order> arr = new ArrayList<Order>();
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from myorders where LoginID = '" + loginID + "' order by TransactionNumber desc limit 5");
            while (rs.next()) {
                arr.add(new Order(rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getInt(6), rs.getString(7)));
            }
            return arr;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
    
    public ArrayList<String> fetchTradeTypes() {
        ArrayList<String> arr = new ArrayList<String>();
        arr.add("BUY");
        arr.add("SELL");
        return arr;
    }
    
    public String trade() {
        //load the driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return "Internal Error";
        }
        
        DatabaseFunctions db = new DatabaseFunctions();
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            if (tradeType.equals("BUY")) {
                //check if the user enters a valid share
                if (!db.ifRecordsExist("select * from stocks where Symbol = '" + share + "'")) {
                    this.setMessage("Not a valid Stock...");
                    return ("tradeNotOk.xhtml");
                }
                
                rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and MinSellingPrice > 0 order by MinSellingPrice");
                
                if (rs.next()) {
                    // System.out.println();
                    // System.out.println("#### Available shares of " + share + ":");
                    // System.out.printf("%20s%20s%20s%20s\n", "Share", "Total Available", "Price per share", "Seller");
                    // rs.beforeFirst();
                    // display the available shares for sale
                    // while (rs.next()) {
                    //    System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                    // }

                    rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and NumberOfShares >= " + numOfShares + " and MinSellingPrice > 0 order by MinSellingPrice");
                    
                    if (rs.next()) {
                        // System.out.println();
                        // System.out.println("#### Available " + num + " shares of " + share + ":");
                        // System.out.printf("%20s%20s%20s%20s\n", "Share", "Total Available", "Price per share", "Seller");
                        //display the current share returned
                        // rs.beforeFirst();
                        //display the available shares for sale
                        // while (rs.next()) {
                        //    System.out.printf("%20s%20d%20f%20s\n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
                        // }

                        // System.out.println();
                        // System.out.println("#### Your available balance is: " + balance);
                        // price = in.getPrice(num, share, "buy");
                        // rs.beforeFirst();
                        // rs.next();
//                        if (balance < rs.getDouble(3)) {
//                            this.setBalance(in.depositMoney(id, balance));
//                        }
                        rs = stat.executeQuery("select * from salesorder where ShareSymbol = '" + share + "' and NumberOfShares >= " + numOfShares + " and MinSellingPrice <= " + price + " and MinSellingPrice > 0 order by MinSellingPrice");
                        
                        if (rs.next()) {
                            //buy the first stock which is found. Since, this is a limit order
                            if (rs.getInt(2) == numOfShares) {
                                //update buyer databases
                                db.insertUpdateDelete("delete from salesorder where ShareSymbol = '" + share + "' and NumberOfShares = " + numOfShares + " and MinSellingPrice = " + rs.getDouble(3));
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + loginID + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + numOfShares + ", 'BUY')");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + price + " where Symbol = '" + share + "'");
                                //update the user balance
                                db.insertUpdateDelete("update onlineaccount set balance = " + (balance - price) + " where LoginID = '" + loginID + "'");
                                //modifying the current object
                                this.setBalance(balance - price);
                                //update the count of shares in users account
                                if (db.ifRecordsExist("select * from myshares where LoginID = '" + loginID + "' and Symbol = '" + share + "'")) {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(loginID, share) + numOfShares) + " where LoginID = '" + loginID + "' and Symbol = '" + share + "'");
                                } else {
                                    db.insertUpdateDelete("insert into myshares values('" + loginID + "', '" + share + "', '" + db.getStockName(share) + "', " + numOfShares + ")");
                                }

                                //update seller databases
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + rs.getString(4) + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + numOfShares + ", 'SELL')");
                                db.insertUpdateDelete("update onlineaccount set balance = " + (db.getUserBalance(rs.getString(4)) + price) + " where LoginID = '" + rs.getString(4) + "'");
                            } else {
                                db.insertUpdateDelete("update salesorder set NumberOfShares = " + (rs.getInt(2) - numOfShares) + " where ShareSymbol = '" + share + "' and NumberOfShares = " + rs.getInt(2) + " and MinSellingPrice = " + rs.getDouble(3));
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + loginID + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + numOfShares + ", 'BUY')");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + price + " where Symbol = '" + share + "'");
                                //update the user balance
                                db.insertUpdateDelete("update onlineaccount set balance = " + (balance - price) + " where LoginID = '" + loginID + "'");
                                //modifying the current object
                                this.setBalance(balance - price);
                                //update the count of shares in users account
                                if (db.ifRecordsExist("select * from myshares where LoginID = '" + loginID + "' and Symbol = '" + share + "'")) {
                                    db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(loginID, share) + numOfShares) + " where LoginID = '" + loginID + "' and Symbol = '" + share + "'");
                                } else {
                                    db.insertUpdateDelete("insert into myshares values('" + loginID + "', '" + share + "', '" + db.getStockName(share) + "', " + numOfShares + ")");
                                }

                                //update seller databases
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + rs.getString(4) + "', '" + share + "','" + db.getStockName(share) + "'," + price + ", " + numOfShares + ", 'SELL')");
                                db.insertUpdateDelete("update onlineaccount set balance = " + (db.getUserBalance(rs.getString(4)) + price) + " where LoginID = '" + rs.getString(4) + "'");
                            }
                            this.setMessage("#### You bought " + numOfShares + " shares of " + share + " for $" + price + "");
                            // System.out.println("#### You bought " + num + " shares of " + share + " for $" + price + "");
                            // System.out.println("#### Trade Completed");
                            return "tradeOk.xhtml";
                        } else {
                            // User Quoted a lesser price
                            // System.out.println("**** You Quoted a much lesser price. NO Stock available at this price. ****");
                            // System.out.println("**** Trade cannot be completed ****");
                            this.setMessage("You Quoted a much lesser price. NO Stock available at this price. Your request has been added to the pending orders.");
                            db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + numOfShares + "', '" + price + "', '" + loginID + "')");
                            // System.out.println("#### Your request has been added to the pending orders ####");
                            // System.out.println();
                            return "tradeNotOk.xhtml";
                        }
                    } else {
                        // System.out.println("**** Currently there are NO " + num + " shares available of " + share + " at the same rate ****");
                        // System.out.println("**** Trade Cannot be Completed ****");
                        // System.out.println("#### Your request will be added to the pending orders ####");
                        // price = in.getPrice(num, share, "buy");
                        this.setMessage("Currently there are NO " + numOfShares + " shares available of " + share + " at the same rate. Your request has been added to the pending orders.");
                        db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + numOfShares + "', '" + price + "', '" + loginID + "')");
                        return "tradeNotOk.xhtml";
                    }
                } else {
                    // System.out.println("**** Currently NO " + share + " share available for sale ****");
                    // System.out.println("**** Trade Cannot be Completed ****");
                    // System.out.println("#### Your request will be added to the pending orders ####");
                    this.setMessage("Currently NO " + share + " share available for sale. Your request is added in the pending orders.");
                    db.insertUpdateDelete("insert into purchaseorders values('" + share + "', '" + numOfShares + "', '" + price + "', '" + loginID + "')");
                    return ("tradeNotOk.xhtml");
                    // System.out.println("#### Your request has been added to the pending orders ####");
                }
            } else if (tradeType.equals("SELL")) {

                // System.out.println();
                // System.out.println("#### LIMIT ORDER");
                //display a list of stocks that the user possess
                rs = stat.executeQuery("select * from myshares where LoginID = '" + loginID + "'");
                // System.out.println();
                // System.out.println("#### List of Stocks you possess");
                // System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                // while (rs.next()) {
                //    System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                // }
                // System.out.println();
                // check if the user enters a valid share
                // while (!flag) {
                //     share = in.getShare("sell");
                if (!db.ifRecordsExist("select * from myshares where LoginID = '" + loginID + "' and Symbol = '" + share + "'")) {
                    //flag = true;
                    this.setMessage("You Do NOT possess this share. Please enter a Valid share.");
                    return ("tradeNotOk.xhtml");
                }
                // }

                // flag = false;
                // System.out.println();
                //display the shares desired by the user
                rs.beforeFirst();
                // System.out.println("#### List of " + share + " stocks you possess");
                // System.out.printf("%20s%20s%20s\n", "Symbol", "Name of Stock", "Number_of_Shares");

                // while (rs.next()) {
                // if (rs.getString(2).equalsIgnoreCase(share)) {
                // System.out.printf("%20s%20s%20d\n", rs.getString(2), rs.getString(3), rs.getInt(4));
                // }
                // }
                boolean flag = false;
                while (!flag) {
                    // num = 0;
                    // num = in.getNumOfShares("sell");

                    //Check if the user owns right amount of shares of that particular share, so that he/she is eligible to sell them                    
                    rs = stat.executeQuery("select * from myshares where LoginID = '" + loginID + "' and Symbol = '" + share + "' and NumberOfShares >= " + numOfShares);
                    
                    if (rs.next()) {
                        // price = in.getPrice(num, share, "sell");

                        // we can now sell the stocks
                        // check whether there is any pending purchase order that can buy this particular stock
                        rs = stat.executeQuery("select * from purchaseorders where ShareSymbol = '" + share + "' and NumberOfShares <= " + numOfShares + " and MaxBuyingPrice >= " + price + " order by MaxBuyingPrice desc");
                        
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
                                db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(loginID, share) - rs.getInt(2)) + " where LoginID = '" + loginID + "' and Symbol = '" + share + "'");
                                
                                db.insertUpdateDelete("insert into myorders(LoginID, Symbol, NameOfStock, TradedPrice, NumberOfShares, TradeType) values('" + loginID + "', '" + share + "', '" + db.getStockName(share) + "', " + rs.getDouble(3) + ", " + rs.getInt(2) + ", 'SELL')");
                                db.insertUpdateDelete("update stocks set RecentTradedPrice = " + rs.getDouble(3) + " where Symbol = '" + share + "'");
                                //updating the seller balance
                                db.insertUpdateDelete("update onlineaccount set Balance = " + (balance + rs.getDouble(3)) + " where LoginID = '" + loginID + "'");
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
                            this.setMessage("Sold " + rs.getInt(2) + " shares for $" + rs.getDouble(3));
                            //System.out.println("#### Sold " + rs.getInt(2) + " shares for $" + rs.getDouble(3));
                            //System.out.println("#### Trade Successfully Completed.");
                            return ("tradeOk.xhtml");
                        } else {
                            // update the count of stocks in seller accounts after putting the order in sales order (If no potential buyer is available)
                            if ((db.getTotalShares(loginID, share) - numOfShares) > 0) {
                                db.insertUpdateDelete("update myshares set NumberOfShares = " + (db.getTotalShares(loginID, share) - numOfShares) + " where LoginID = '" + loginID + "' and Symbol = '" + share + "'");
                            } else {
                                db.insertUpdateDelete("delete from myshares where LoginID = '" + loginID + "' and Symbol = '" + share + "'");
                            }

                            // did not find a pending order
                            this.setMessage("Currently NO Pending Purchase Order match your requirement. Your sales order will be added to the Queue until a potential buyer is found.");
                            // System.out.println("**** Currently NO Pending Purchase Order match your requirement ****");
                            // System.out.println("#### Your sales order will be added to the Queue until a potential buyer is found");
                            db.insertUpdateDelete("insert into salesorder values('" + share + "', '" + numOfShares + "', '" + price + "', '" + loginID + "')");
                            // System.out.println("**** Trade not completed ****");
                            return ("tradeNotOk.xhtml");
                        }
                    } else {
                        this.setMessage("You DO NOT have " + numOfShares + " shares of " + share + " to sell.");
                        // System.out.println("**** You DO NOT have " + num + " shares of " + share + " to sell. ****");
                        // System.out.println("**** Please enter the correct number of shares you wish to sell ****");
                        // System.out.println();
                        return ("tradeNotOk.xhtml");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.setMessage("Exception!");
            return ("tradeNotOk.xhtml");
        } finally {
            try {
                rs.close();
                stat.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return "";
    }
    
    public ArrayList<Order> fetchStocksPossessed() {

        //load the driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return null;
        }
        
        ArrayList<Order> arr = new ArrayList<Order>();
        DatabaseFunctions db = new DatabaseFunctions();
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from myshares where LoginID = '" + loginID + "'");
            while (rs.next()) {
                arr.add(new Order(rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
    
    public ArrayList<Order> fetchPendingStocksToBeSold() {

        //load the driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            return null;
        }
        
        ArrayList<Order> arr = new ArrayList<Order>();
        DatabaseFunctions db = new DatabaseFunctions();
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from salesorder");
            while (rs.next()) {
                arr.add(new Order(rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4)));
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
