package com.graysoda.cnpc;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static Application instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static Context get(){
		return instance;
	}
}
