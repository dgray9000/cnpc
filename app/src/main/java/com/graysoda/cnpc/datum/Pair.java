package com.graysoda.cnpc.datum;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class Pair {
    private final String symbol;
    private final Asset base;
    private final Asset quote;
    private final long id;

    public Pair(String symbol, long id, Asset base, Asset quote) {
        this.symbol = symbol;
        this.base = base;
        this.quote = quote;
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Asset getBase() {
        return base;
    }

    public Asset getQuote() {
        return quote;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair){
            return ((Pair) obj).id == id;
        }

        return false;
    }
}
