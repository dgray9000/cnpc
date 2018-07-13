package com.graysoda.cnpc.Database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PairRouteTable {
    static final String TABLE_NAME = "PairRouteData";
    static final String COLUMN_SYMBOL = "pair_symbol";
    static final String COLUMN_ROUTE = "route";
    static final String COLUMN_MARKET = "market";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_SYMBOL + " TEXT NOT NULL, ");
        sb.append(COLUMN_ROUTE + " TEXT NOT NULL, ");
        sb.append(COLUMN_MARKET + " INTEGER, ");
        sb.append("FOREIGN KEY("+COLUMN_MARKET+") REFERENCES "+ MarketDataTable.TABLE_NAME+"("+MarketDataTable.COLUMN_ID+")");
        sb.append(");");
        try {
            db.execSQL(sb.toString());
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }
}
