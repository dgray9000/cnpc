package com.graysoda.cnpc;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.Pair;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DataFetcher extends AsyncTask<String, Void, Void> {
	private static final String TAG = Constants.TAG + " DataFetcher: ";
	private final DataManager dm;
	private JSONParser parser = new JSONParser();

	public DataFetcher(Context context){
		dm = new DataManager(context);
	}

	@Override
	protected Void doInBackground(String... strings) {

		String revision;
		try {
			revision = parser.parseRevision(getJsonResponse(Constants.BASE_CRYPTOWATCH_URL));

			if (dm.getRevision() == null
					|| !dm.getRevision().equals(revision)
					|| dm.getAllExchanges().size() == 0
					|| dm.getAllPairs().size() == 0
					|| dm.getAllAssets().size() == 0)
			{
				updateDB(revision);
			}

			//updateDB(revision);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "ALL DONE UPDATING THE DATABASE");

		return null;
	}

	private void updateDB(String revision) {
		dm.update(revision);
		updatePairs();
		updateExchanges();
	}

	private void updatePairs() {
		Log.v(TAG, "updatePairs start");
		ArrayList<Pair> pairs = null;

		try{
			pairs = parser.parsePairs(getJsonResponse(Constants.PAIRS_URL));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(pairs != null){
			for (Pair p : pairs) {
				dm.insertPair(p);
			}
		} else {
			Log.d(TAG, " pairs is null.");
		}

		Log.v(TAG, "updatePairs end");
	}

	private void updateExchanges() {
		Log.v(TAG, "updateExchanges start");
		String url = Constants.EXCHANGE_URL;
		ArrayList<Exchange> exchanges = null;

		try {
			exchanges = parser.parseExchanges(getJsonResponse(url));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		for(Exchange e : exchanges){
			dm.insertExchange(e);
		}

		Log.v(TAG, "updateExchanges end");
	}

	private String getJsonResponse(String urlString){
		Log.v(TAG, "getJsonResponse start");
		try {
			URL url = new URL(urlString);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			int statusCode = connection.getResponseCode();

			if (statusCode == HttpsURLConnection.HTTP_OK){
				Log.d(TAG, "connection OK");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();

				while(line != null){
					sb.append(line);
					line = reader.readLine();
				}

				Log.d(TAG, "JSON returned [" + !sb.toString().isEmpty() + "]");

				connection.disconnect();

				Log.v(TAG, "getJsonResponse end");

				return sb.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
