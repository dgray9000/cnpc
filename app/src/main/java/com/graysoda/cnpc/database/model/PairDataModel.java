package com.graysoda.cnpc.database.model;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PairDataModel {
    public static final String TABLE_NAME = "PairData";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SYMBOL = "pair_symbol";
    public static final String COLUMN_BASE = "base";
    public static final String COLUMN_QUOTE = "quote";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER primary key autoincrement, ");
        sb.append(COLUMN_SYMBOL + " TEXT NOT NULL, ");
        sb.append(COLUMN_BASE + " INTEGER, ");
        sb.append(COLUMN_QUOTE + " INTEGER, ");
        sb.append("FOREIGN KEY("+COLUMN_BASE+") REFERENCES "+ AssetModel.TABLE_NAME+"("+AssetModel.COLUMN_ID+")");
        sb.append("FOREIGN KEY("+COLUMN_QUOTE+") REFERENCES "+AssetModel.TABLE_NAME+"("+AssetModel.COLUMN_ID+")");
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
