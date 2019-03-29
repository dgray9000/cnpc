package com.graysoda.cnpc.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.database.model.DBOpenHelper;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.NotificationData;
import com.graysoda.cnpc.datum.Pair;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class DataManager {
    private final SQLiteDatabase db;
    private final NotificationDAO notificationDAO;
    private final PairDAO pairDAO;
    private final ExchangeDAO exchangeDAO;
    private final ApiDAO apiDAO;
    private final AssetDAO assetDAO;
    private final DBOpenHelper dbOpenHelper;


    public DataManager(Context context){
        dbOpenHelper = new DBOpenHelper(context);
        db = dbOpenHelper.getWritableDatabase();
        notificationDAO = new NotificationDAO(db);
        pairDAO = new PairDAO(db);
        exchangeDAO = new ExchangeDAO(db);
        apiDAO = new ApiDAO(db);
        assetDAO = new AssetDAO(db);
    }

    public void close(){db.close();}
    /****************************
        General Methods
    ****************************/
    public String getRevision(){
    	return apiDAO.getRevision();
	}

	public boolean update(String revision){
    	dbOpenHelper.onUpgrade(db,db.getVersion(),db.getVersion() + 1);
    	return apiDAO.update(revision);
	}

    /*****************************
        Notification Methods
    *****************************/

    public ArrayList<NotificationData> getAllNotifications(){return notificationDAO.getAll();}

    public boolean updateNotification(NotificationData data){return notificationDAO.update(data);}

    public long insertNotification(NotificationData data){return notificationDAO.insert(data);}

    public boolean deleteNotification(Long id){return notificationDAO.delete(id);}

    public NotificationData getNotification(long notificationID) {
        return notificationDAO.get(notificationID);
    }

    public boolean hasNotification(long notificationID){
        return notificationDAO.has(notificationID);
    }

    /******************************
        Pair Methods
    ******************************/

    public ArrayList<Pair> getAllPairs(){
        return pairDAO.getAll();
    }

    public long insertPair(Pair data){
        return pairDAO.insert(data);
    }

    public boolean deletePair(long id){
        return pairDAO.delete(id);
    }

    /******************************
        Exchange Methods
    *******************************/

    public ArrayList<Exchange> getAllExchanges(){
        return exchangeDAO.getAll();
    }

    public long insertExchange(Exchange data){
        return exchangeDAO.insert(data);
    }

    public boolean deleteExchange(long id){
        return exchangeDAO.delete(id);
    }

	/******************************
	 	Asset Methods
	 ******************************/
	public ArrayList<Asset> getAllAssets(){
		return assetDAO.getAll();
	}

	public long insertAsset(Asset data){
		return assetDAO.insert(data);
	}

	public Asset getAsset(int id){
		return assetDAO.get(id);
	}

	public boolean deleteAsset(Asset data){
		return assetDAO.delete(data);
	}

	public Pair getPairByAssets(Asset chosenBase, Asset chosenQuote, Exchange chosenExchange) {
		return pairDAO.getPairByAssets(chosenBase, chosenQuote, chosenExchange);
	}
}
