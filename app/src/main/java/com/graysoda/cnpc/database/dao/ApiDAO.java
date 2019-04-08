package com.graysoda.cnpc.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.graysoda.cnpc.database.model.ApiModel;

public class ApiDAO {
	private final SQLiteDatabase db;

	public ApiDAO(SQLiteDatabase db) {
		this.db = db;
	}

	public String getRevision(){
		String revision = "";
		Cursor c = db.query(ApiModel.TABLE_NAME,null,null,null,null,null,null,null);

		if (c != null && c.moveToFirst()){
			revision = c.getString(0);
		}

		if (c != null && !c.isClosed()){
			c.close();
		}

		return revision;
	}

	boolean update(String revision){
		db.delete(ApiModel.TABLE_NAME,ApiModel.COLUMN_REVISION+" !=?",new String[]{revision});
		return db.insert(ApiModel.TABLE_NAME, null, makeContentValues(revision)) > 0;
	}

	private ContentValues makeContentValues(String revision) {
		ContentValues cv = new ContentValues();
		cv.put(ApiModel.COLUMN_REVISION, revision);
		return cv;
	}
}
