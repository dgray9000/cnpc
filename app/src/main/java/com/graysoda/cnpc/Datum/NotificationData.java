package com.graysoda.cnpc.Datum;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class NotificationData {
    private double priceLast, priceHigh, priceLow, changeAbsolute;
    private long id;
    private double changePercentage, volume;
    private String route, updateInterval, exchange, pairSymbol, quoteSymbol, baseSymbol;
    private boolean isOn = true;

    public NotificationData() {
        this.priceLast = -1;
        this.priceHigh = -1;
        this.priceLow = -1;
        this.changeAbsolute = -1;
        this.changePercentage = -1;
        this.volume = -1;
    }

    public NotificationData(long id, String pairSymbol,String baseSymbol, String quoteSymbol, String exchange, String route, String updateInterval, int isOn){
        this.id = id;
        this.pairSymbol = pairSymbol;
        this.exchange = exchange;
        this.route = route;
        this.updateInterval = updateInterval;
        this.isOn = (isOn == 1);
        this.baseSymbol = baseSymbol;
        this.quoteSymbol = quoteSymbol;
    }

    public NotificationData(double priceLast, double priceHigh, double priceLow, double changePercentage,
                     double changeAbsolute, double volume, String route, String updateInterval,
                     String exchange, String baseSymbol, String quoteSymbol, String pairSymbol,long id) {
        this.priceLast = priceLast;
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;
        this.changePercentage = changePercentage;
        this.changeAbsolute = changeAbsolute;
        this.volume = volume;
        this.route = route;
        this.updateInterval = updateInterval;
        this.exchange = exchange;
        this.baseSymbol = baseSymbol;
        this.quoteSymbol = quoteSymbol;
        this.pairSymbol = pairSymbol;
        this.id = id;
    }

    public NotificationData(String updateInterval, MarketData mMarket, PairData mPair) {
        this.updateInterval = updateInterval;
        this.exchange = mMarket.getExchange();
        this.route = mMarket.getRoute(mPair.getSymbol()) + "/summary";
        this.baseSymbol = mPair.getBase().getSymbol();
        this.pairSymbol = mPair.getSymbol();
        this.quoteSymbol = mPair.getQuote().getSymbol();
    }

    public String getBaseSymbol(){return baseSymbol;}

    public String getQuoteSymbol(){return quoteSymbol;}

    public void setId(long id){this.id = id;}

    public long getId(){return id;}

    public String getPairSymbol(){return pairSymbol;}

    public String getExchange(){return exchange;}

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }

    public boolean getIsOn(){
        return isOn;
    }

    public String getUpdateInterval() {
        return updateInterval;
    }

    public String getRoute() {
        return route;
    }

    public double getPriceLast() {
        return priceLast;
    }

    public double getPriceHigh() {
        return priceHigh;
    }

    public double getPriceLow() {
        return priceLow;
    }

    public double getChangeAbsolute() {
        return changeAbsolute;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public double getVolume() {
        return volume;
    }
}
