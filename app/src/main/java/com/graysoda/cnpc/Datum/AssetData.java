package com.graysoda.cnpc.Datum;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class AssetData {
    private int id;
    private String symbol, name;

    public AssetData(int id, String symbol, String name) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }
}
