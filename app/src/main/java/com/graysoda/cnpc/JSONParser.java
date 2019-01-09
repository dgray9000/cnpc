package com.graysoda.cnpc;

import com.graysoda.cnpc.Datum.AssetData;
import com.graysoda.cnpc.Datum.MarketData;
import com.graysoda.cnpc.Datum.PairData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.graysoda.cnpc.Constants.ACTIVE;
import static com.graysoda.cnpc.Constants.ALLOWANCE;
import static com.graysoda.cnpc.Constants.BASE;
import static com.graysoda.cnpc.Constants.EXCHANGE;
import static com.graysoda.cnpc.Constants.ID;
import static com.graysoda.cnpc.Constants.NAME;
import static com.graysoda.cnpc.Constants.PAIR;
import static com.graysoda.cnpc.Constants.QUOTE;
import static com.graysoda.cnpc.Constants.RESULT;
import static com.graysoda.cnpc.Constants.ROUTE;
import static com.graysoda.cnpc.Constants.SYMBOL;

/**
 * Created by david.grayson on 3/23/2018.
 */

class JSONParser {
    ArrayList<PairData> parsePairs (String s) throws JSONException {
        ArrayList<PairData> pairs = new ArrayList<>();

        JSONObject root = new JSONObject(s);
        JSONArray result = root.getJSONArray(RESULT);
        JSONObject allowance = root.getJSONObject(ALLOWANCE);

        for(int i=0;i<result.length();i++){
            JSONObject pair = result.getJSONObject(i);
            pairs.add(parsePair(pair));
        }

        return pairs;
    }

    ArrayList<MarketData> parseMarkets(String s) throws JSONException {
        ArrayList<MarketData> markets = new ArrayList<>();

        JSONObject root = new JSONObject(s);
        JSONArray result = root.getJSONArray(RESULT);
        JSONObject allowance = root.getJSONObject(ALLOWANCE);

        for (int i=0;i<result.length();i++){
            JSONObject market = result.getJSONObject(i);
            markets.add(parseMarket(market));
        }

        return markets;
    }

    private PairData parsePair(JSONObject pair) throws JSONException {
        String symbol = pair.getString(SYMBOL);
        int id = pair.getInt(ID);
        AssetData base = parseAsset(pair.getJSONObject(BASE));
        AssetData quote = parseAsset(pair.getJSONObject(QUOTE));

        return new PairData(symbol,id,base,quote);
    }

    private AssetData parseAsset(JSONObject asset) throws JSONException {
        int id = asset.getInt(ID);
        String symbol = asset.getString(SYMBOL);
        String name = asset.getString(NAME);

        return new AssetData(id,symbol,name);
    }

    private MarketData parseMarket(JSONObject market) throws JSONException {
        String exchange = market.getString(EXCHANGE);
        String pair = market.getString(PAIR);
        Boolean active = market.getBoolean(ACTIVE);
        String route = market.getString(ROUTE);

        return new MarketData(exchange,pair,active,route);
    }
}
