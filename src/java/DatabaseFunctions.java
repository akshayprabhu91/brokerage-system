/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SONY
 */
public class DatabaseFunctions {

    public void insertUpdateDelete(String str) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            stat.executeUpdate(str);
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

    public boolean ifRecordsExist(String str) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery(str);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    public double getUserBalance(String id) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select Balance from onlineaccount where LoginID = '" + id + "'");
            rs.next();
            return rs.getDouble("Balance");

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
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

    public String getStockName(String symbol) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select StockName from stocks where Symbol = '" + symbol + "'");
            rs.next();
            return rs.getString("StockName");

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

    public int getTotalShares(String id, String share) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select NumberOfShares from myshares where LoginID = '" + id + "' and Symbol = '" + share + "'");
            rs.next();
            return rs.getInt("NumberOfShares");

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
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

    public List<String> getStringValues(String str) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            List<String> lst = new ArrayList<String>();
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery(str);
            while (rs.next()) {
                lst.add(rs.getString("Symbol"));
            }
            return lst;
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

    public int getLoginCount(String loginID) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select LoginCount from failedloginattempts where LoginID = '" + loginID + "'");
            rs.next();
            return rs.getInt("LoginCount");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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

    public int getCurrentAccountNumber() {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select NextID from nextaccountnumber");
            rs.next();
            return rs.getInt("NextID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
//                rs.close();
                stat.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getPassword(String loginID) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select Password from onlineaccount where LoginID = '" + loginID + "'");
            rs.next();
            return rs.getString("Password");

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

    public String getAccountNumber(String loginID) {
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/prabhua6510";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, "prabhua6510", "1441868");
            stat = conn.createStatement();
            rs = stat.executeQuery("select AccountNum from onlineaccount where LoginID = '" + loginID + "'");
            rs.next();
            return rs.getString("AccountNum");

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
