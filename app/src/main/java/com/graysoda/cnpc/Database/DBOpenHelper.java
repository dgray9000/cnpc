package com.graysoda.cnpc.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class DBOpenHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "CNPC.db";

    DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        NotificationDataTable.onCreate(sqLiteDatabase);
        AssetDataTable.onCreate(sqLiteDatabase);
        PairDataTable.onCreate(sqLiteDatabase);
        MarketDataTable.onCreate(sqLiteDatabase);
        PairRouteTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        NotificationDataTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        AssetDataTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        PairDataTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        MarketDataTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        PairRouteTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    void onRebuild(SQLiteDatabase db, int oldVersion, int newVersion){
        //Dropping tables
        AssetDataTable.onUpgrade(db, oldVersion, newVersion);
        PairDataTable.onUpgrade(db, oldVersion, newVersion);
        MarketDataTable.onUpgrade(db, oldVersion, newVersion);
        PairRouteTable.onUpgrade(db, oldVersion, newVersion);

        //recreating tables
        AssetDataTable.onCreate(db);
        PairDataTable.onCreate(db);
        MarketDataTable.onCreate(db);
        PairRouteTable.onCreate(db);
    }
}
