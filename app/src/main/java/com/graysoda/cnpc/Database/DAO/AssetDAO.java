package com.graysoda.cnpc.Database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.Database.Model.AssetModel;
import com.graysoda.cnpc.Datum.AssetData;

public class AssetDAO {
    private final SQLiteDatabase db;

    AssetDAO(SQLiteDatabase db){this.db = db;}

    AssetData get(int id){
        AssetData assetData = null;
        Cursor c = db.query(AssetModel.TABLE_NAME,
                null,AssetModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)},
                null,null,null);

        if (c != null && c.moveToFirst()){
            assetData = this.buildAsset(c);
        }

        if (c != null && !c.isClosed()){
            c.close();
        }

        return assetData;
    }

    long insert(AssetData data){
        AssetData assetData = get(data.getId());
        return assetData != null ? assetData.getId() :
                db.insert(AssetModel.TABLE_NAME,
                        null,
                        makeContentValues(data));
    }

    private ContentValues makeContentValues(AssetData data) {
        ContentValues cv = new ContentValues();
        cv.put(AssetModel.COLUMN_ID, data.getId());
        cv.put(AssetModel.COLUMN_NAME, data.getName());
        cv.put(AssetModel.COLUMN_SYMBOL, data.getSymbol());
        return cv;
    }

    private AssetData buildAsset(Cursor c) {
        int id = c.getInt(0);
        String symbol = c.getString(1);
        String name = c.getString(2);

        return new AssetData(id,symbol,name);
    }
}
