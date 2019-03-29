package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.database.model.AssetModel;
import com.graysoda.cnpc.datum.Asset;

import java.util.ArrayList;

public class AssetDAO {
    private final SQLiteDatabase db;

    AssetDAO(SQLiteDatabase db){this.db = db;}

    Asset get(int id){
        Asset assetData = null;
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

    long insert(Asset data){
        Asset assetData = get(data.getId());
        return assetData != null ? assetData.getId() :
                db.insert(AssetModel.TABLE_NAME,
                        null,
                        makeContentValues(data));
    }

    boolean delete(Asset data){
        return db.delete(AssetModel.TABLE_NAME,AssetModel.COLUMN_ID+"=?",new String[]{String.valueOf(data.getId())}) > 0;
    }

    private ContentValues makeContentValues(Asset data) {
        ContentValues cv = new ContentValues();
        cv.put(AssetModel.COLUMN_ID, data.getId());
        cv.put(AssetModel.COLUMN_NAME, data.getName());
        cv.put(AssetModel.COLUMN_SYMBOL, data.getSymbol());
        return cv;
    }

    private Asset buildAsset(Cursor c) {
        int id = c.getInt(0);
        String symbol = c.getString(1);
        String name = c.getString(2);

        return new Asset(id,symbol,name);
    }

	ArrayList<Asset> getAll() {
		ArrayList<Asset> assets = new ArrayList<>();
		Cursor c = db.query(AssetModel.TABLE_NAME,null,null,null,null,null,null,null);

		if (c != null && c.moveToFirst()){
			do {
				assets.add(this.buildAsset(c));
			} while (c.moveToNext());
		}

		if (c != null && !c.isClosed()){
			c.close();
		}

		return assets;
	}
}
