package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.database.model.ExchangeModel;
import com.graysoda.cnpc.datum.Exchange;

import java.util.ArrayList;
import java.util.HashMap;

class ExchangeDAO {
    private static final String TAG = Constants.TAG + " ExchangeDAO:";
    private final SQLiteDatabase db;
    private final PairRouteDAO prDAO;

    ExchangeDAO(SQLiteDatabase db) {
        this.db = db;
        prDAO = new PairRouteDAO(db);
    }

    ArrayList<Exchange> getAll(){
        ArrayList<Exchange> exchangeData = new ArrayList<>();
        Cursor c = db.query(ExchangeModel.TABLE_NAME,
                null,null,null,null,
                null,null);
        if (c!=null && c.moveToFirst()){
            do {
                exchangeData.add(this.buildExchangeData(c));
            } while (c.moveToNext());
        }

        if (c!=null && !c.isClosed())
            c.close();

        return exchangeData;
    }

    long insert(Exchange data){
        long id = db.insert(ExchangeModel.TABLE_NAME,
                null,
                makeContentValues(data));
        Log.d(TAG,"id from insert = ["+id+"] for [" + data.getName() + "]");
        if (id != -1 && prDAO.insert(data.getPairRoute(),id)){
            return id;
        }
        else
            return id;
    }

    boolean delete(Long id){
        return db.delete(ExchangeModel.TABLE_NAME,
                ExchangeModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)})>0
                &&
                prDAO.delete(id);
    }

    private ContentValues makeContentValues(Exchange data){
        ContentValues cv = new ContentValues();
        cv.put(ExchangeModel.COLUMN_EXCHANGE,data.getName());
        return cv;
    }

    private Exchange buildExchangeData(Cursor c) {
        long id = c.getLong(0);
        String exchange = c.getString(1);
        HashMap<String,String> pairRoutes = prDAO.get(id);

        return new Exchange(id,exchange,pairRoutes);
    }
}
