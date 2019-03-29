package com.graysoda.cnpc;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DataFetcher extends AsyncTask<String, Void, Void> {
	private final DataManager dm;
	private JSONParser parser = new JSONParser();

	public DataFetcher(Context context){
		dm = new DataManager(context);
	}

	@Override
	protected Void doInBackground(String... strings) {

		String revision = null;
		try {
			revision = parser.parseRevision(getJsonResponse(strings[0]));

			if (!dm.getRevision().equals(revision)){
				updateDB(revision);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void updateDB(String revision) throws JSONException {
		dm.update(revision);
		updateAssets();
		updateExchanges();
	}

	private void updateExchanges() throws JSONException {
		String url = Resources.getSystem().getString(R.string.baseCryptowatchApiUrl) + Resources.getSystem().getString(R.string.exchanges);
		ArrayList<Exchange> exchanges = parser.parseExchanges(getJsonResponse(url));

		for(Exchange e : exchanges){
			dm.insertExchange(e);
		}
	}

	private void updateAssets() throws JSONException {
		String url = Resources.getSystem().getString(R.string.baseCryptowatchApiUrl) + Resources.getSystem().getString(R.string.assets);
		ArrayList<Asset> assets = parser.parseAssets(getJsonResponse(url));

		for (Asset a : assets) {
			dm.insertAsset(a);
		}

	}

	private String getJsonResponse(String urlString){
		try {
			URL url = new URL(urlString);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			int statusCode = connection.getResponseCode();

			if (statusCode == HttpsURLConnection.HTTP_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();

				while(line != null){
					sb.append(line);
					line = reader.readLine();
				}

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
