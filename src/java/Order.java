/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SONY
 */
public class Order {

    private String symbol;
    private String nameOfStock;
    private double tradedPrice;
    private int numberOfShares;
    private String tradeType;
    
    private double minSellingPrice;
    private String seller;

    public Order(String symbol, String nameOfStock, int numberOfShares) {
        this.symbol = symbol;
        this.nameOfStock = nameOfStock;
        this.numberOfShares = numberOfShares;
    }

    public Order(String symbol, String nameOfStock, double tradedPrice, int numberOfShares, String tradeType) {
        this.symbol = symbol;
        this.nameOfStock = nameOfStock;
        this.tradedPrice = tradedPrice;
        this.numberOfShares = numberOfShares;
        this.tradeType = tradeType;
    }
    
    public Order(String symbol, int numberOfShares, double minSellingPrice, String seller){
        this.symbol = symbol;
        this.numberOfShares = numberOfShares;
        this.minSellingPrice = minSellingPrice;
        this.seller = seller;
    }

    public double getMinSellingPrice() {
        return minSellingPrice;
    }

    public void setMinSellingPrice(double minSellingPrice) {
        this.minSellingPrice = minSellingPrice;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNameOfStock() {
        return nameOfStock;
    }

    public void setNameOfStock(String nameOfStock) {
        this.nameOfStock = nameOfStock;
    }

    public double getTradedPrice() {
        return tradedPrice;
    }

    public void setTradedPrice(double tradedPrice) {
        this.tradedPrice = tradedPrice;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

}
