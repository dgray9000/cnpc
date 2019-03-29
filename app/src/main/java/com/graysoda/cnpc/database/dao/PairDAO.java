package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.database.model.PairDataModel;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.Pair;

import java.util.ArrayList;

class PairDAO {
    private final SQLiteDatabase db;
    private final AssetDAO aDAO;

    PairDAO(SQLiteDatabase db) {
        this.db = db;
        aDAO = new AssetDAO(db);
    }

    ArrayList<Pair> getAll(){
        ArrayList<Pair> pairData = new ArrayList<>();
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

    long insert(Pair data){
        return db.insert(PairDataModel.TABLE_NAME,
                null,
                makeContentValues(data));
    }

    private ContentValues makeContentValues(Pair data) {
        ContentValues cv = new ContentValues();
        cv.put(PairDataModel.COLUMN_SYMBOL, data.getSymbol());
        cv.put(PairDataModel.COLUMN_BASE, aDAO.insert(data.getBase()));
        cv.put(PairDataModel.COLUMN_QUOTE, aDAO.insert(data.getQuote()));
        return cv;
    }

    private Pair buildPair(Cursor c) {
        long id = c.getLong(0);
        String pairSymbol = c.getString(1);
        Asset base = aDAO.get(c.getInt(2));
        Asset quote = aDAO.get(c.getInt(3));

        return new Pair(pairSymbol,id,base,quote);
    }

	Pair getPairByAssets(Asset base, Asset quote, Exchange exchange) {
		ArrayList<Pair> pairs = new ArrayList<>();

		Cursor c = db.query(PairDataModel.TABLE_NAME,
				null,
				PairDataModel.COLUMN_BASE + "=? AND " + PairDataModel.COLUMN_QUOTE + "=?",
				new String[]{String.valueOf(base.getId()),String.valueOf(quote.getId())},
				null,null,null);

		if (c!= null && c.moveToFirst()){
			do {
				pairs.add(this.buildPair(c));
			} while (c.moveToNext());
		}

		if (c != null && !c.isClosed()){
			c.close();
		}

		for (Pair pair : pairs){
			if (exchange.hasPair(pair.getSymbol())){
				return pair;
			}
		}

		return null;
	}
}
