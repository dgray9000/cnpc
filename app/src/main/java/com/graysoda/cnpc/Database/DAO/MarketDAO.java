package com.graysoda.cnpc.Database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graysoda.cnpc.Database.Model.MarketModel;
import com.graysoda.cnpc.Datum.MarketData;

import java.util.ArrayList;
import java.util.HashMap;

class MarketDAO {
    private static final String TAG = "MarketDAO";
    private final SQLiteDatabase db;
    private final PairRouteDAO prDAO;

    MarketDAO(SQLiteDatabase db) {
        this.db = db;
        prDAO = new PairRouteDAO(db);
    }

    ArrayList<MarketData> getAll(){
        ArrayList<MarketData> marketData = new ArrayList<>();
        Cursor c = db.query(MarketModel.TABLE_NAME,
                null,null,null,null,
                null,null);
        if (c!=null && c.moveToFirst()){
            do {
                marketData.add(this.buildMarketData(c));
            } while (c.moveToNext());
        }

        if (c!=null && !c.isClosed())
            c.close();

        return marketData;
    }

    long insert(MarketData data){
        long id = db.insert(MarketModel.TABLE_NAME,
                null,
                makeContentValues(data));
        Log.d(TAG,"id from insert = ["+id+"]");
        if (id != -1 && prDAO.insert(data.getPairRoute(),id)){
            return id;
        }
        else
            return id;
    }

    boolean delete(Long id){
        return db.delete(MarketModel.TABLE_NAME,
                MarketModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)})>0
                &&
                prDAO.delete(id);
    }

    private ContentValues makeContentValues(MarketData data){
        ContentValues cv = new ContentValues();
        cv.put(MarketModel.COLUMN_EXCHANGE,data.getExchange());
        return cv;
    }

    private MarketData buildMarketData(Cursor c) {
        long id = c.getLong(0);
        String exchange = c.getString(1);
        HashMap<String,String> pairRoutes = prDAO.get(id);

        return new MarketData(id,exchange,pairRoutes);
    }
}
