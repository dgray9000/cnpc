package com.graysoda.cnpc.Database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.Database.Model.PairDataModel;
import com.graysoda.cnpc.Datum.AssetData;
import com.graysoda.cnpc.Datum.PairData;

import java.util.ArrayList;

class PairDAO {
    private final SQLiteDatabase db;
    private final AssetDAO aDAO;

    PairDAO(SQLiteDatabase db) {
        this.db = db;
        aDAO = new AssetDAO(db);
    }

    ArrayList<PairData> getAll(){
        ArrayList<PairData> pairData = new ArrayList<>();
        Cursor c = db.query(PairDataModel.TABLE_NAME,
                null,null,null,
                null,null,null,null);
        if (c!= null && c.moveToFirst()){
            do {
                pairData.add(this.buildPair(c));
            } while (c.moveToNext());
        }

        if (c != null && !c.isClosed())
            c.close();

        return pairData;
    }

    boolean delete(long id){
        return db.delete(PairDataModel.TABLE_NAME,
                PairDataModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)})>0;
    }

    long insert(PairData data){
        return db.insert(PairDataModel.TABLE_NAME,
                null,
                makeContentValues(data));
    }

    private ContentValues makeContentValues(PairData data) {
        ContentValues cv = new ContentValues();
        cv.put(PairDataModel.COLUMN_SYMBOL, data.getSymbol());
        cv.put(PairDataModel.COLUMN_BASE, aDAO.insert(data.getBase()));
        cv.put(PairDataModel.COLUMN_QUOTE, aDAO.insert(data.getQuote()));
        return cv;
    }

    private PairData buildPair(Cursor c) {
        long id = c.getLong(0);
        String pairSymbol = c.getString(1);
        AssetData base = aDAO.get(c.getInt(2));
        AssetData quote = aDAO.get(c.getInt(3));

        return new PairData(pairSymbol,id,base,quote);
    }
}
