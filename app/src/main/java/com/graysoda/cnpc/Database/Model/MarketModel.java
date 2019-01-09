package com.graysoda.cnpc.Database.Model;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MarketModel {
    public static final String TABLE_NAME = "MarketData";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXCHANGE = "exchange_name";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COLUMN_EXCHANGE + " TEXT NOT NULL");
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
