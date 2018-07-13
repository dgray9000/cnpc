package com.graysoda.cnpc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.graysoda.cnpc.Database.DataManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class AssetChooser extends AppCompatActivity implements View.OnClickListener{
    private static ArrayList<PairData> mPairList;
    private static ArrayList<MarketData> mMarketList;
    private static android.app.AlertDialog dialog;
    private Button baseButton, quoteButton, exchange, updateIntervalButton;
    private AssetData mBase, mQuote;
    private PairData mPair;
    private MarketData mMarket;
    private String updateInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_chooser);
        dialog = new ProgressDialog.Builder(this,ProgressDialog.STYLE_SPINNER).setMessage("Fetching data...").create();
        dialog.show();

        new DataFetcher(0).execute(getString(R.string.baseCryptowatchApiUrl)+getString(R.string.pairs));
        new DataFetcher(1).execute(getString(R.string.baseCryptowatchApiUrl)+getString(R.string.markets));

        //initializing arrays
        mPairList = new ArrayList<>();
        mMarketList = new ArrayList<>();

        baseButton = findViewById(R.id.assetChooser_button_base);
        quoteButton = findViewById(R.id.assetChooser_button_quote);
        exchange = findViewById(R.id.assetChooser_button_exchange);
        updateIntervalButton = findViewById(R.id.assetChooser_button_updateInterval);
    }

    @Override
    public void onClick(View v) {
        Log.d("onClick","a button was clicked");
        switch(v.getId()){
            case R.id.assetChooser_button_done:
                if (mPair != null && mMarket != null && updateInterval != null)
                    makeNotification(new NotificationData(updateInterval,mMarket,mPair));
                else
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("Error")
                            .setMessage("It appears you haven't selected some stuff, please do or hit the Cancel button at the bottom.")
                            .setCancelable(true)
                            .create().show();
                finish();
                break;
            case R.id.assetChooser_button_cancel:
                finish();
                break;
            case R.id.assetChooser_button_base:
                makeSingleChoiceDialog(makeBaseList(),0);
                break;
            case R.id.assetChooser_button_quote:
                makeSingleChoiceDialog(makeQuoteList(),1);
                break;
            case R.id.assetChooser_button_exchange:
                Log.d("onClick","Exchange button clicked");
                makeSingleChoiceDialog(makeMarketList(),2);
                break;
            case R.id.assetChooser_button_updateInterval:
                ArrayList<String> updateIntervalChoices = new ArrayList<>();
                if(Collections.addAll(updateIntervalChoices, getResources().getStringArray(R.array.update_interval_choices)))
                    makeSingleChoiceDialog(updateIntervalChoices,3);
                break;

            default: break;
        }
    }

    private void makeNotification(NotificationData data) {
        new NotificationCreator().create(getApplicationContext(), (int) new DataManager(getApplicationContext()).insertNotification(data), updateInterval);
    }

    private void makeSingleChoiceDialog(ArrayList data, final int button){
        Log.d("makeSingleChoiceDialog","start");
        //button 0=base 1=quote 2=exchange 3=updateInterval
        String title;
        switch(button){
            case 0: title = "Choose the base"; break;
            case 1: title = "Choose the quote"; break;
            case 2: title = "Choose the exchange"; break;
            case 3: title = "Choose the update interval"; break;
            default: title = "Error, this should not happen!";
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_singlechoice);

        for (Object obj:data){
            if (obj instanceof AssetData)
                adapter.add(((AssetData) obj).getName());
            else if (obj instanceof MarketData)
                adapter.add(((MarketData) obj).getExchange());
            else if (obj instanceof String)
                adapter.add((String) obj);
        }

        if (!adapter.isEmpty()){
            builder.setAdapter(adapter, (dialogInterface, i) -> {
                String s = adapter.getItem(i);
                switch (button){
                    case 0:
                        baseButton.setText(s);
                        setBase(s);
                        quoteButton.setEnabled(true);
                        break;
                    case 1:
                        quoteButton.setText(s);
                        setQuote(s);
                        break;
                    case 2:
                        exchange.setText(s);
                        setMarket(s);
                        break;
                    case 3:
                        updateIntervalButton.setText(s);
                        updateInterval = s;
                        break;
                }
            }).create().show();
        }
    }

    private void setMarket(String s) {
        for (MarketData marketData : mMarketList){
            if (marketData.getExchange().equals(s))
                mMarket = marketData;
        }
    }

    private void setQuote(String s) {
        for (AssetData assetData : makeQuoteList()){
            if (assetData.getName().equals(s)) {
                mQuote = assetData;
                if (mBase != null)
                    setPair();
                break;
            }
        }
    }

    private void setPair() {
        for (PairData pairData : mPairList){
            if (pairData.getQuote().getId() == mQuote.getId() && pairData.getBase().getId() == mBase.getId()) {
                mPair = pairData;
                break;
            }
        }
    }

    private void setBase(String s) {
        for (AssetData assetData : makeBaseList()){
            if (assetData.getName().equals(s)) {
                mBase = assetData;
                if (mQuote != null)
                    setPair();
                break;
            }
        }
    }

    private ArrayList<AssetData> makeBaseList() {
        //String TAG = "makeBaseList";
        ArrayList<AssetData> baseList = new ArrayList<>();
        ArrayList<Integer> baseIds = new ArrayList<>();
        AssetData base;
        int quoteID = mQuote != null ? mQuote.getId() : -1;

        for(PairData pairData : mPairList){
            base = pairData.getBase();
            if (mMarket == null){
                for (MarketData marketData : mMarketList) {
                    if (marketData.hasPair(pairData.getSymbol()) || quoteID != -1 && quoteID == pairData.getQuote().getId()){
                        if (!baseIds.contains(base.getId())){
                            baseList.add(base);
                            baseIds.add(base.getId());
                        }
                    }
                }
            } else if (mMarket.hasPair(pairData.getSymbol()) || quoteID!= -1 && quoteID == pairData.getQuote().getId()){
                if (!baseIds.contains(base.getId())){
                    baseList.add(base);
                    baseIds.add(base.getId());
                }
            }
        }

        return baseList;
    }

    private ArrayList<AssetData> makeQuoteList() {
        //String TAG = "makeQuoteList";
        ArrayList<AssetData> quoteList = new ArrayList<>();
        ArrayList<Integer> quoteIds = new ArrayList<>();
        AssetData quote;
        int baseID = mBase != null ? mBase.getId() : -1;


        for (PairData pairData : mPairList) {
            quote = pairData.getQuote();
            if (mMarket == null){
                for (MarketData marketData : mMarketList) {
                    if (marketData.hasPair(pairData.getSymbol()) || baseID != -1 && baseID == pairData.getBase().getId()){
                        if (!quoteIds.contains(quote.getId())){
                            quoteList.add(quote);
                            quoteIds.add(quote.getId());
                        }
                    }
                }
            } else if (mMarket.hasPair(pairData.getSymbol()) || baseID != -1 && baseID == pairData.getBase().getId()) {
                if (!quoteIds.contains(quote.getId())){
                    quoteList.add(quote);
                    quoteIds.add(quote.getId());
                }
            }
        }

        return quoteList;
    }

    private ArrayList<MarketData> makeMarketList() {
        //String TAG = "makeMarketList";
        ArrayList<MarketData> markets = new ArrayList<>();
        ArrayList<String> marketNames = new ArrayList<>();

        //Log.d(TAG,"starting");

        for (MarketData marketData : mMarketList){
            if (!marketNames.contains(marketData.getExchange())){
                if (mPair != null && marketData.hasPair(mPair.getSymbol())){
                    markets.add(marketData);
                    marketNames.add(marketData.getExchange());
                } else if (mBase != null || mQuote != null) {
                    String assetSymbol = mBase != null ? mBase.getSymbol() : mQuote.getSymbol();
                    for (String s : marketData.getPairs()) {
                        if (s.contains(assetSymbol) && !marketNames.contains(marketData.getExchange())){
                            markets.add(marketData);
                            marketNames.add(marketData.getExchange());
                        }
                    }
                } else {
                    return mMarketList;
                }
            }
        }

        return markets;
    }

    private static void setPairList(ArrayList<PairData> pairList) {
        mPairList = pairList;
        if (mMarketList.size()>0)
            dialog.dismiss();
    }

    private static void setMarketList(ArrayList<MarketData> marketList) {
        //String TAG = "setMarketList";
        //Log.d(TAG,"start");

        HashMap<String,MarketData> exchangeMap = new HashMap<>();
        for (MarketData data : marketList) {
            if (data.isActive()){
                if (exchangeMap.containsKey(data.getExchange())){
                    for (String s:data.getPairs()) {
                        if (!exchangeMap.get(data.getExchange()).hasPair(s)) {
                            exchangeMap.get(data.getExchange()).addPairRoute(s, data.getRoute(s));
                        }
                    }
                } else{
                    exchangeMap.put(data.getExchange(),data);
                }
            }
        }

        mMarketList.addAll(exchangeMap.values());

        if (mPairList.size()>0)
            dialog.dismiss();
        //Log.d(TAG,"end");
    }

    class DataFetcher extends AsyncTask<String,Void,ArrayList> {
        private final int type; //0=pairs, 1=markets,

        DataFetcher(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList o) {
            super.onPostExecute(o);
            ArrayList<PairData> pairData = new ArrayList<>();
            ArrayList<MarketData> marketData = new ArrayList<>();
            if (o.size()>0){
                for (Object obj : o) {
                    if (obj instanceof PairData){
                        pairData.add((PairData) obj);
                    } else if (obj instanceof MarketData){
                        marketData.add((MarketData) obj);
                    }
                }
                if (pairData.size()>0)
                    setPairList(pairData);
                else if (marketData.size()>0)
                    setMarketList(marketData);
            } else {
                Toast.makeText(AssetChooser.this, "Data not returned from API server", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList doInBackground(String[] strings) {
            ArrayList data = new ArrayList();
            try {
                URL url = new URL(strings[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                int statusCode = connection.getResponseCode();
                if (statusCode == HttpsURLConnection.HTTP_OK){
                    JSONParser parser = new JSONParser();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = reader.readLine();
                    while(line!=null){
                        sb.append(line);
                        line=reader.readLine();
                    }
                    switch (type){
                        case 0: data = parser.parsePairs(sb.toString());
                            break;
                        case 1: data = parser.parseMarkets(sb.toString());
                            break;
                    }
                } else {
                    Log.d("DataFetcher","bad status code: " + statusCode);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }
    }
}
