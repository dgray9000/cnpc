package com.graysoda.cnpc.Database.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.Database.Model.DBOpenHelper;
import com.graysoda.cnpc.Datum.MarketData;
import com.graysoda.cnpc.Datum.NotificationData;
import com.graysoda.cnpc.Datum.PairData;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class DataManager {
    private final SQLiteDatabase db;
    private final NotificationDAO nDAO;
    private final PairDAO pDAO;
    private final MarketDAO mDAO;

    public DataManager(Context context){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        db = dbOpenHelper.getWritableDatabase();
        nDAO = new NotificationDAO(db);
        pDAO = new PairDAO(db);
        mDAO = new MarketDAO(db);
    }

    public void close(){db.close();}
    /****************************
        General Methods
    ****************************/


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
