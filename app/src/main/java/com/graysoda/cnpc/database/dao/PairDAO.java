package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.database.model.PairDataModel;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.Pair;

import java.util.ArrayList;

class PairDAO {
	private static final String TAG = Constants.TAG + " PairDAO:";
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

		Log.d("demo", "pairs returned from db [" + pairData.size() + "]");

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
		String sql = "SELECT * FROM " + PairDataModel.TABLE_NAME + " WHERE " + PairDataModel.COLUMN_BASE+"=? AND " + PairDataModel.COLUMN_QUOTE + "=?";

		Cursor c = db.rawQuery(sql,new String[]{String.valueOf(base.getId()),String.valueOf(quote.getId())});

//				db.query(PairDataModel.TABLE_NAME,
//				null,
//				PairDataModel.COLUMN_BASE + "=? AND " + PairDataModel.COLUMN_QUOTE + "=?",
//				new String[]{String.valueOf(base.getId()),String.valueOf(quote.getId())},
//				null,null,null);

		if (c!= null && c.moveToFirst()){
			Log.d(TAG, "getPairByAssets: c not null and moved to first");
			do {
				pairs.add(this.buildPair(c));
			} while (c.moveToNext());
		}

		if (c != null && !c.isClosed()){
			c.close();
		}

		Log.d(TAG, exchange.getPairs().toString());

		for (Pair pair : pairs){
			Log.d(TAG, "getPairByAssets: pair = [" + pair.getSymbol() + "]");
			if (exchange.hasPair(pair.getSymbol())){
				Log.d(TAG, "getPairByAssets: exchange has pair");
				return pair;
			}
		}

		return null;
	}
}
