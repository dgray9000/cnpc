package com.graysoda.cnpc.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

class PairRouteDataAccessObject {
    private static final String TAG = "PairRouteDAO";
    private final SQLiteDatabase db;

    PairRouteDataAccessObject(SQLiteDatabase db) {
        this.db = db;
    }

    HashMap<String, String> get(long id){
        HashMap<String,String> pairRouteMap = new HashMap<>();
        Cursor c = db.query(PairRouteTable.TABLE_NAME,
                null,
                PairRouteTable.COLUMN_MARKET+"=?",
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

        return pairRouteMap;
    }

    boolean delete(Long id) {
        return db.delete(PairRouteTable.TABLE_NAME,
                PairRouteTable.COLUMN_MARKET+"=?",
                new String[]{String.valueOf(id)})>0;
    }

    boolean insert(HashMap<String,String> pairRoute, long market){
        for (String s : pairRoute.keySet()) {
            Log.d(TAG,"inserting ["+s+"]");
            if (!insertSingle(s,pairRoute.get(s),market))
                return false;
            Log.d(TAG,"["+s+"] inserted successfully");
        }
        return true;
    }

    private boolean insertSingle(String pair, String route, long market){
        return db.insert(PairRouteTable.TABLE_NAME,
                null,
                makeContentValues(pair, route, market)) != -1;
    }

    private ContentValues makeContentValues(String pair, String route, long market) {
        ContentValues cv = new ContentValues();
        cv.put(PairRouteTable.COLUMN_SYMBOL, pair);
        cv.put(PairRouteTable.COLUMN_ROUTE, route);
        cv.put(PairRouteTable.COLUMN_MARKET, market);
        return cv;
    }
}
