package com.graysoda.cnpc.database.model;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class NotificationModel {
    public static final String TABLE_NAME = "NotificationData";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROUTE = "route";
    public static final String COLUMN_UPDATE_INTERVAL = "update_interval";
    public static final String COLUMN_IS_ON = "is_on";
    public static final String COLUMN_EXCHANGE = "exchange";
    public static final String COLUMN_PAIR_SYMBOL = "pair_symbol";
    public static final String COLUMN_BASE_SYMBOL = "base_symbol";
    public static final String COLUMN_QUOTE_SYMBOL = "quote_symbol";

    static void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER primary key autoincrement, ");
        sb.append(COLUMN_PAIR_SYMBOL + " text not null, ");
        sb.append(COLUMN_BASE_SYMBOL + " text not null, ");
        sb.append(COLUMN_QUOTE_SYMBOL + " text not null, ");
        sb.append(COLUMN_EXCHANGE + " text not null, ");
        sb.append(COLUMN_ROUTE + " text not null, ");
        sb.append(COLUMN_UPDATE_INTERVAL + " text not null, ");
        sb.append(COLUMN_IS_ON + " INTEGER not null");
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
