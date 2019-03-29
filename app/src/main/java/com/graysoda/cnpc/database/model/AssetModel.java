package com.graysoda.cnpc.database.model;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AssetModel {
    public static final String TABLE_NAME = "AssetData";
    public static final String COLUMN_ID = "json_id";
    public static final String COLUMN_SYMBOL = "ticker_symbol";
    public static final String COLUMN_NAME = "name";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER NOT NULL, ");
        sb.append(COLUMN_SYMBOL + " TEXT NOT NULL, ");
        sb.append(COLUMN_NAME + " TEXT NOT NULL);");
        try{
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
