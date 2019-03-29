package com.graysoda.cnpc.datum;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class Asset {
    private int id;
    private String symbol, name;

    public Asset(int id, String symbol, String name) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Asset)
            return ((Asset) obj).getId() == id;

        return false;
    }
}
