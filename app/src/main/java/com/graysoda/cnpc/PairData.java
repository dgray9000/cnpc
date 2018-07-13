package com.graysoda.cnpc;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class PairData {
    private final String symbol;
    private final AssetData base;
    private final AssetData quote;
    private final long id;

    public PairData(String symbol, long id, AssetData base, AssetData quote) {
        this.symbol = symbol;
        this.base = base;
        this.quote = quote;
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public AssetData getBase() {
        return base;
    }

    public AssetData getQuote() {
        return quote;
    }

    public long getId() {
        return id;
    }
}
