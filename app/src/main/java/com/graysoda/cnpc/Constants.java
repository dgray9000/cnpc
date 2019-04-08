package com.graysoda.cnpc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public final class Constants {
	public static final String TAG = "CNPC";
    public static final String BASE_CRYPTOWATCH_URL = "https://api.cryptowat.ch";
    public static final String ASSETS_URL = BASE_CRYPTOWATCH_URL + "/assets";
    public static final String MARKET_URL = BASE_CRYPTOWATCH_URL + "/market";
    public static final String EXCHANGE_URL = BASE_CRYPTOWATCH_URL + "/exchanges";
    public static final String PAIRS_URL = BASE_CRYPTOWATCH_URL + "/pairs";
    public static final String[] UPDATE_INTERVAL_CHOICES = new String[]
			{
					"1 minute",
					"5 minutes",
					"10 minutes",
					"15 minutes",
					"30 minutes",
					"1 hour"
			};
    public static final String iconUrl = "https://raw.githubusercontent.com/atomiclabs/cryptocurrency-icons/master/32/icon/";
    public static final String RESULT = "result";
    public static final String ALLOWANCE = "allowance";
    public static final String SYMBOL = "symbol";
    public static final String ID = "id";
    public static final String BASE = "base";
    public static final String QUOTE = "quote";
    public static final String ROUTE = "route";
    public static final String NAME = "name";
    public static final String EXCHANGE = "exchange";
    public static final String PAIR = "pair";
    public static final String ACTIVE = "active";
    public static final String channelId = "Prices";
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    public static final String REVISION = "revision";
    public static final int PERMISSION_REQUEST_CODE = 87;

    public static boolean hasPermissions(Context context, String... permissions){
    	if (context != null && permissions != null){
    		for (String s : permissions){
    			if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED){
    				return false;
				}
			}
		}
		return true;
	}
}
