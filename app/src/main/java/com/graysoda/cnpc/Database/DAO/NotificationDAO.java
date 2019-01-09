package com.graysoda.cnpc.Database.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graysoda.cnpc.Database.Model.NotificationModel;
import com.graysoda.cnpc.Datum.NotificationData;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

class NotificationDAO {
    private final SQLiteDatabase db;

    NotificationDAO(SQLiteDatabase db){this.db = db;}

    ArrayList<NotificationData> getAll() {
        ArrayList<NotificationData> dataArrayList = new ArrayList<>();
        Cursor c = db.query(NotificationModel.TABLE_NAME,
                null,null,null,null,null,null,null);
        if (c != null && c.moveToFirst()){
            do {
                dataArrayList.add(this.buildNotificationData(c));
            } while (c.moveToNext());
        }

        if (c != null && !c.isClosed())
            c.close();

        return dataArrayList;
    }

    private NotificationData buildNotificationData(Cursor c) {
        long id = c.getLong(0);
        String pair = c.getString(1);
        String base = c.getString(2);
        String quote = c.getString(3);
        String exchange = c.getString(4);
        String route = c.getString(5);
        String updateInterval = c.getString(6);
        int isOn = c.getInt(7);

        return new NotificationData(id, pair, base, quote, exchange, route, updateInterval, isOn);
    }

    private ContentValues makeContentValues(NotificationData data){
        ContentValues cv = new ContentValues();
        cv.put(NotificationModel.COLUMN_PAIR_SYMBOL, data.getPairSymbol());
        cv.put(NotificationModel.COLUMN_BASE_SYMBOL, data.getBaseSymbol());
        cv.put(NotificationModel.COLUMN_QUOTE_SYMBOL, data.getQuoteSymbol());
        cv.put(NotificationModel.COLUMN_EXCHANGE, data.getExchange());
        cv.put(NotificationModel.COLUMN_ROUTE, data.getRoute());
        cv.put(NotificationModel.COLUMN_UPDATE_INTERVAL, data.getUpdateInterval());
        cv.put(NotificationModel.COLUMN_IS_ON, ((data.getIsOn())?1:0));
        return cv;
    }

    boolean update(NotificationData data){
        return db.update(NotificationModel.TABLE_NAME,
                makeContentValues(data),
                NotificationModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(data.getId())}) == 1;
    }

    boolean delete(Long id){
        return db.delete(NotificationModel.TABLE_NAME,
                NotificationModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)})>0;
    }

    long insert(NotificationData data){
        return db.insert(NotificationModel.TABLE_NAME,
                null,
                makeContentValues(data));
    }

    boolean has(long id){
        return get(id) != null;
    }

    NotificationData get(long notificationID) {
        NotificationData notificationData = null;
        Log.d("NDAO","get: id=" + notificationID);
        Cursor c = db.query(NotificationModel.TABLE_NAME,
                null,
                NotificationModel.COLUMN_ID+"=?",
                new String[]{String.valueOf(notificationID)},
                null,
                null,
                null,
                null);

        if (c != null) {
            Log.d("NDAO", "get: column count = " + c.getColumnCount());
            Log.d("NDAO", "get: count = " + c.getCount());
            if (c.moveToFirst()){
                Log.d("NDAO", "get: position = " + c.getPosition());
                notificationData = this.buildNotificationData(c);
            } else {
                Log.d("NDAO","get: cursor couldn't move to first.");
            }
        } else {
            Log.d("NDAO","get: cursor was null.");
        }

        if (c != null && !c.isClosed()){
            c.close();
        }

        return notificationData;
    }
}
