package com.graysoda.cnpc;

import android.content.res.Resources;

import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.graysoda.cnpc.Constants.ACTIVE;
import static com.graysoda.cnpc.Constants.ALLOWANCE;
import static com.graysoda.cnpc.Constants.BASE;
import static com.graysoda.cnpc.Constants.ID;
import static com.graysoda.cnpc.Constants.NAME;
import static com.graysoda.cnpc.Constants.PAIR;
import static com.graysoda.cnpc.Constants.QUOTE;
import static com.graysoda.cnpc.Constants.RESULT;
import static com.graysoda.cnpc.Constants.REVISION;
import static com.graysoda.cnpc.Constants.ROUTE;
import static com.graysoda.cnpc.Constants.SYMBOL;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class JSONParser {
    public ArrayList<Pair> parsePairs(String s) throws JSONException {
        ArrayList<Pair> pairs = new ArrayList<>();

        JSONObject root = new JSONObject(s);
        JSONArray result = root.getJSONArray(RESULT);
        JSONObject allowance = root.getJSONObject(ALLOWANCE);

        for(int i=0;i<result.length();i++){
            JSONObject pair = result.getJSONObject(i);
            pairs.add(parsePair(pair));
        }

        return pairs;
    }

    public ArrayList<Exchange> parseExchanges(String s) throws JSONException {
        ArrayList<Exchange> exchanges = new ArrayList<>();

        JSONObject root = new JSONObject(s);
        JSONArray result = root.getJSONArray(RESULT);
        JSONObject allowance = root.getJSONObject(ALLOWANCE);

        for (int i = 0; i < result.length(); i++)
        {
            JSONObject jsonObject = result.getJSONObject(i);
            if (jsonObject.getBoolean(ACTIVE)){
            	exchanges.add(parseExchange(jsonObject));
			}
        }

        return exchanges;
    }

    public ArrayList<Asset> parseAssets(String s) throws JSONException {
    	ArrayList<Asset> assets = new ArrayList<>();

    	JSONObject root = new JSONObject(s);
    	JSONArray result = root.getJSONArray(RESULT);
    	JSONObject allowance = root.getJSONObject(ALLOWANCE);

		for (int i = 0; i < result.length(); i++) {
			JSONObject asset = result.getJSONObject(i);
			assets.add(parseAsset(asset));
		}

		return assets;
	}

    private Pair parsePair(JSONObject pair) throws JSONException {
        String symbol = pair.getString(SYMBOL);
        int id = pair.getInt(ID);
        Asset base = parseAsset(pair.getJSONObject(BASE));
        Asset quote = parseAsset(pair.getJSONObject(QUOTE));

        return new Pair(symbol,id,base,quote);
    }

    private Asset parseAsset(JSONObject asset) throws JSONException {
        int id = asset.getInt(ID);
        String symbol = asset.getString(SYMBOL);
        String name = asset.getString(NAME);

        return new Asset(id,symbol,name);
    }

    private Exchange parseExchange(JSONObject exchange) throws JSONException {
    	int id = exchange.getInt(ID);
    	String name = exchange.getString(NAME);
		HashMap<String, String> pairsAndRoutes = new HashMap<>();

    	try{
			URL url = new URL(Resources.getSystem().getString(R.string.baseCryptowatchApiUrl) + Resources.getSystem().getString(R.string.market) + "s/" + name);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			int statusCode = connection.getResponseCode();

			if (statusCode == 200){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();

				while(line != null){
					sb.append(line);
					line = reader.readLine();
				}

				pairsAndRoutes = parseMarketResponse(sb.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new Exchange(id, name, pairsAndRoutes);
    }

	private HashMap<String, String> parseMarketResponse(String s) throws JSONException {
		HashMap<String, String> map = new HashMap<>();

		JSONObject root = new JSONObject(s);
		JSONArray result = root.getJSONArray(RESULT);
		JSONObject allowance = root.getJSONObject(ALLOWANCE);

		for (int i = 0; i < result.length(); i++) {
			JSONObject jsonObject = result.getJSONObject(i);

			if (jsonObject.getBoolean(ACTIVE)){
				map.put(jsonObject.getString(PAIR), jsonObject.getString(ROUTE));
			}
		}

		return map;
	}

	public String parseRevision(String s) throws JSONException {
    	JSONObject root = new JSONObject(s);
    	JSONObject result = root.getJSONObject(RESULT);
    	JSONObject allowance = root.getJSONObject(ALLOWANCE);

    	return result.getString(REVISION);
	}
}
