package com.graysoda.cnpc.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.MarketData;
import com.graysoda.cnpc.NotificationData;
import com.graysoda.cnpc.PairData;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class DataManager {
    private final SQLiteDatabase db;
    private final NotificationDataAccessObject nDAO;
    private final PairDataAccessObject pDAO;
    private final MarketDataAccessObject mDAO;
    private final DBOpenHelper dbOpenHelper;

    public DataManager(Context context){
        dbOpenHelper = new DBOpenHelper(context);
        db = dbOpenHelper.getWritableDatabase();
        nDAO = new NotificationDataAccessObject(db);
        pDAO = new PairDataAccessObject(db);
        mDAO = new MarketDataAccessObject(db);
    }

    public void close(){db.close();}
    /****************************
        General Methods
    ****************************/

    public void rebuildDatabase(int oldVersion, int newVersion){
        dbOpenHelper.onRebuild(db,oldVersion, newVersion);
        new PopulateDatabase().execute();
    }

    /*****************************
        Notification Methods
    *****************************/

    public ArrayList<NotificationData> getAllNotifications(){return nDAO.getAll();}

    public boolean updateNotification(NotificationData data){return nDAO.update(data);}

    public long insertNotification(NotificationData data){return nDAO.insert(data);}

    public boolean deleteNotification(Long id){return nDAO.delete(id);}

    public NotificationData getNotification(long notificationID) {
        return nDAO.get(notificationID);
    }

    public boolean hasNotification(long notificationID){
        return nDAO.has(notificationID);
    }

    /******************************
        PairData Methods
    ******************************/

    public ArrayList<PairData> getAllPairs(){
        return pDAO.getAll();
    }

    public long insertPair(PairData data){
        return pDAO.insert(data);
    }

    public boolean deletePair(long id){
        return pDAO.delete(id);
    }

    /******************************
        MarketData Methods
    *******************************/

    public ArrayList<MarketData> getAllMarkets(){
        return mDAO.getAll();
    }

    public long insertMarket(MarketData data){
        return mDAO.insert(data);
    }

    public boolean deleteMarket(long id){
        return mDAO.delete(id);
    }
}
