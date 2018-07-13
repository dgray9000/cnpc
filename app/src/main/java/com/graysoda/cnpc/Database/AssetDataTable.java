package com.graysoda.cnpc.Database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class AssetDataTable {
    static final String TABLE_NAME = "AssetData";
    static final String COLUMN_ID = "json_id";
    static final String COLUMN_SYMBOL = "ticker_symbol";
    static final String COLUMN_NAME = "name";

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
