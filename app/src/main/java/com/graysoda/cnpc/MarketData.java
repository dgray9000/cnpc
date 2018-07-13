package com.graysoda.cnpc;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class MarketData {
    private final long id;
    private final String exchange;
    private HashMap<String, String> pairRoute = new HashMap<>();
    private final boolean active;

    public MarketData(long id, String exchange, HashMap<String, String> pairRoute) {
        this.id = id;
        this.exchange = exchange;
        this.active = true;
        this.pairRoute = pairRoute;
    }

    MarketData(String exchange, String pair, Boolean active, String route) {
        this.id = -1;
        this.exchange = exchange;
        this.active = active;
        pairRoute.put(pair,route);
    }



    public long getId() {
        return id;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoute(String pair){
        if (pairRoute.containsKey(pair))
            return pairRoute.get(pair);
        else
            return null;
    }

    public Set<String> getPairs(){
        return pairRoute.keySet();
    }

    boolean hasPair(String pair){
        return pairRoute.containsKey(pair);
    }

    void addPairRoute(String pair, String route){
        pairRoute.put(pair,route);
    }

    public HashMap<String, String> getPairRoute() {
        return pairRoute;
    }

    public boolean isActive() {
        return active;
    }
}
