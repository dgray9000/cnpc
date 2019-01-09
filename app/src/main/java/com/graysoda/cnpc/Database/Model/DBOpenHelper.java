package com.graysoda.cnpc.Database.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class DBOpenHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "CNPC.db";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        NotificationModel.onCreate(sqLiteDatabase);
        AssetModel.onCreate(sqLiteDatabase);
        PairDataModel.onCreate(sqLiteDatabase);
        MarketModel.onCreate(sqLiteDatabase);
        PairRouteModel.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        NotificationModel.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        AssetModel.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        PairDataModel.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        MarketModel.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        PairRouteModel.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}
