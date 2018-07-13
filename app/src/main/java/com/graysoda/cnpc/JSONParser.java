package com.graysoda.cnpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

class JSONParser {
    private static final String RESULT = "result";
    private static final String ALLOWANCE = "allowance";
    private static final String SYMBOL = "symbol";
    private static final String ID = "id";
    private static final String BASE = "base";
    private static final String QUOTE = "quote";
    private static final String ROUTE = "route";
    private static final String NAME = "name";
    private static final String EXCHANGE = "exchange";
    private static final String PAIR = "pair";
    private static final String ACTIVE = "active";

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
