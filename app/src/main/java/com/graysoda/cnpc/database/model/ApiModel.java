package com.graysoda.cnpc.database.model;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ApiModel {
	public static final String TABLE_NAME = "api";
	public static final String COLUMN_REVISION = "revision";

	static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
		sb.append(COLUMN_REVISION + " TEXT NOT NULL);");
		try{
			db.execSQL(sb.toString());
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	static void onUgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
		onCreate(db);
	}
}
