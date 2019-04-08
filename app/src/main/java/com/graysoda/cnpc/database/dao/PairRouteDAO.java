package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.database.model.PairRouteModel;

import java.util.HashMap;

class PairRouteDAO {
    private static final String TAG = Constants.TAG + " PairRouteDAO";
    private final SQLiteDatabase db;

    PairRouteDAO(SQLiteDatabase db) {
        this.db = db;
    }

    HashMap<String, String> get(long id){
        HashMap<String,String> pairRouteMap = new HashMap<>();
        Cursor c = db.query(PairRouteModel.TABLE_NAME,
                null,
                PairRouteModel.COLUMN_MARKET+"=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        if (c!=null && c.moveToFirst()){
            do {
                pairRouteMap.put(c.getString(0),c.getString(1));
            } while (c.moveToNext());
        }

        if (c!=null && !c.isClosed())
            c.close();

        Log.d(TAG, "[" + pairRouteMap.size() + "] routes returned for id [" + id + "]");

        return pairRouteMap;
    }

    boolean delete(Long id) {
        return db.delete(PairRouteModel.TABLE_NAME,
                PairRouteModel.COLUMN_MARKET+"=?",
                new String[]{String.valueOf(id)})>0;
    }

    boolean insert(HashMap<String,String> pairRoute, long market){
        for (String s : pairRoute.keySet()) {
            Log.v(TAG,"inserting ["+s+"]");

            if (!insertSingle(s,pairRoute.get(s),market))
                return false;

            Log.v(TAG,"["+s+"] inserted successfully");
        }
        return true;
    }

    private boolean insertSingle(String pair, String route, long market){
        return db.insert(PairRouteModel.TABLE_NAME,
                null,
                makeContentValues(pair, route, market)) != -1;
    }

    private ContentValues makeContentValues(String pair, String route, long market) {
        ContentValues cv = new ContentValues();
        cv.put(PairRouteModel.COLUMN_SYMBOL, pair);
        cv.put(PairRouteModel.COLUMN_ROUTE, route);
        cv.put(PairRouteModel.COLUMN_MARKET, market);
        return cv;
    }
}
