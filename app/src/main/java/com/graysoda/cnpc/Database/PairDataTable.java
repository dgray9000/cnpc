package com.graysoda.cnpc.Database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class PairDataTable {
    static final String TABLE_NAME = "PairData";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_SYMBOL = "pair_symbol";
    static final String COLUMN_BASE = "base";
    static final String COLUMN_QUOTE = "quote";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER primary key autoincrement, ");
        sb.append(COLUMN_SYMBOL + " TEXT NOT NULL, ");
        sb.append(COLUMN_BASE + " INTEGER, ");
        sb.append(COLUMN_QUOTE + " INTEGER, ");
        sb.append("FOREIGN KEY("+COLUMN_BASE+") REFERENCES "+ AssetDataTable.TABLE_NAME+"("+AssetDataTable.COLUMN_ID+")");
        sb.append("FOREIGN KEY("+COLUMN_QUOTE+") REFERENCES "+AssetDataTable.TABLE_NAME+"("+AssetDataTable.COLUMN_ID+")");
        sb.append(");");
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
