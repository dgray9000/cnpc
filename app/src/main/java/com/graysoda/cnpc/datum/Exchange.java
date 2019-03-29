package com.graysoda.cnpc.datum;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class Exchange {
    private final long id;
    private final String name;
    private HashMap<String, String> pairRoute = new HashMap<>();

    public Exchange(long id, String name, HashMap<String, String> pairRoute) {
        this.id = id;
        this.name = name;
        this.pairRoute.putAll(pairRoute);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean hasPair(String pair){
        return pairRoute.containsKey(pair);
    }

    public void addPairRoute(String pair, String route){
        pairRoute.put(pair,route);
    }

    public HashMap<String, String> getPairRoute() {
        return pairRoute;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Exchange)
            return ((Exchange) obj).getId() == id;

        return false;
    }
}
