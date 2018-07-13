package com.graysoda.cnpc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.graysoda.cnpc.Database.DataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class NotificationPublisher extends BroadcastReceiver{
    static final String ID = "id";
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String DTAG = "NotificationPublisher";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private int priority;
    private boolean showOnLockScreen, vibrate, ringtoneEnabled;
    private String ringtone;
    private static final String channelId = "Prices";
    private long[] vibratePattern = {1000,1000,1000};


    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context,channelId);

        //getting settings that apply to notifications
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(DTAG,"#onRecieve: sharedPrefs get int = " + sp.getString(context.getString(R.string.pref_notification_priority),"error"));
        priority = Integer.parseInt(sp.getString(context.getString(R.string.pref_notification_priority), String.valueOf(NotificationManagerCompat.IMPORTANCE_MIN)));

        Log.d(DTAG, "#onRecieve: showOnLockScreen = " + sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen),false));
        showOnLockScreen = sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen), true);

        Log.d(DTAG,"#onRecieve: vibrate = " + sp.getBoolean(context.getString(R.string.pref_vibrate),true));
        vibrate = sp.getBoolean(context.getString(R.string.pref_vibrate),false);

        Log.d(DTAG,"#onRecieve: ringtoneEnabled = " + sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),true));
        ringtoneEnabled = sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),false);

        Log.d(DTAG,"#onRecieve: ringtone = " + sp.getString(context.getString(R.string.pref_ringtone),"error"));
        ringtone = sp.getString(context.getString(R.string.pref_ringtone),null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(channelId) == null || !isNotificationChannelCorrect()){
                Log.d(DTAG,"#onRecieve: notification channel is not correct");
                createNotificationChannel(context.getString(R.string.channel_description));
            }
        }

        //checks if its the BOOT_COMPLETED action
        if (intent.getAction() != null && intent.getAction().equals(ACTION)){
            bootCompleteAction(context);
        } else {
            regularNotification(context,intent.getIntExtra(ID,-1));
        }
    }

    private boolean isNotificationChannelCorrect() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);

            if(
                    channel.getImportance() == priority &&
                    (!ringtoneEnabled || channel.getSound().toString().equals(ringtone)) &&
                    channel.getLockscreenVisibility() == (showOnLockScreen ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET) &&
                    channel.shouldVibrate() == vibrate
              )
            {

                return true;
            }
            notificationManager.deleteNotificationChannel(channelId);

        }

        return false;
    }

    private void regularNotification(Context context, int id){
        //Log.d(DTAG","getting [" + id + "] from db");
        if (id > -1){
            NotificationData data = new DataManager(context).getNotification(id);

            if (data != null && data.getIsOn()){
                new FetchPrice().execute(data.getRoute(),
                        data.getBaseSymbol(),
                        data.getQuoteSymbol(),
                        data.getExchange(),
                        data.getUpdateInterval(),
                        data.getPairSymbol(),
                        String.valueOf(id));
            }
        } else {
            throw new RuntimeException("Error retrieving id from database");
        }
    }

    private void bootCompleteAction(Context context) {
        Toast.makeText(context, "BOOT_COMPLETED action received", Toast.LENGTH_LONG).show();

        for (NotificationData data: new DataManager(context).getAllNotifications()){
            if (data.getIsOn())
                new NotificationCreator().create(context, (int) data.getId(),data.getUpdateInterval());
        }
    }

    private void createNotificationChannel(String description) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d(DTAG,"#createNotificationChannel: Android is Oreo");
            NotificationChannel channel = new NotificationChannel(channelId,"Price Updates",priority);

            channel.setDescription(description);

            if(ringtoneEnabled){
                Log.d(DTAG, "ringtone is enabled");
                channel.setSound(Uri.parse(ringtone),channel.getAudioAttributes());
            } else {
                Log.d(DTAG, "ringtone not enabled");
                channel.setSound(null, null);
            }

            if (vibrate){
                Log.d(DTAG, "vibrate enabled");
                channel.setVibrationPattern(vibratePattern);
            }

            channel.setLockscreenVisibility(showOnLockScreen ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void publishNotification(NotificationData priceData) {
        DecimalFormat df;
        if (priceData.getQuoteSymbol().equals("USD") || priceData.getQuoteSymbol().equals("USDT")){
            df = new DecimalFormat("0.00");
        } else {
            df = new DecimalFormat("0.0000");
        }

        mBuilder.setContentTitle("1 " + priceData.getBaseSymbol().toUpperCase() + " = " + priceData.getPriceLast() + " "
                + priceData.getQuoteSymbol().toUpperCase() + " on " + priceData.getExchange());
        mBuilder.setContentText("high: " + priceData.getPriceHigh() +
                " | low: " + priceData.getPriceLow() +
                " | %change: " + df.format(priceData.getChangePercentage()));
        mBuilder.setSmallIcon(getImageResource(priceData.getBaseSymbol()));
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setPriority(priority);

        mBuilder.setVisibility(showOnLockScreen ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET);

        if (vibrate)
            mBuilder.setVibrate(vibratePattern);

        if (ringtoneEnabled)
            mBuilder.setSound(Uri.parse(ringtone));

        notificationManager.notify(priceData.getPairSymbol(),(int) priceData.getId(),mBuilder.build());
    }

    /**
     * Gets the price data based on the url from the db
     */
    class FetchPrice extends AsyncTask<String,Void,NotificationData> {
        @Override
        protected void onPostExecute(NotificationData priceData) {
            super.onPostExecute(priceData);
            publishNotification(priceData);
        }

        @Override
        protected NotificationData doInBackground(String... strings) {
                try {

                    URL url = new URL(strings[0]);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                    int statusCode = connection.getResponseCode();
                    if (statusCode == HttpsURLConnection.HTTP_OK){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = reader.readLine();
                        while(line!=null){
                            //Log.d("fetch",line);
                            sb.append(line);
                            line=reader.readLine();
                        }
                        return parsePriceData(sb.toString(),strings);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        NotificationData parsePriceData(String s, String[] strings) throws JSONException {
            JSONObject root = new JSONObject(s);
            JSONObject result = root.getJSONObject("result");
            JSONObject allowance = root.getJSONObject("allowance");

            JSONObject price = result.getJSONObject("price");
            JSONObject change = price.getJSONObject("change");

            String route = strings[0];
            String base = strings[1];
            String quote = strings[2];
            String exchange = strings[3];
            String updateInterval = strings[4];
            String pairSymbol = strings[5];
            long id = Long.parseLong(strings[6]);

            double lastPrice = price.getDouble("last");
            double highPrice = price.getDouble("high");
            double lowPrice = price.getDouble("low");
            double percentage = change.getDouble("percentage");
            double absolute = change.getDouble("absolute");
            double volume = result.getDouble("volume");

            return new NotificationData(lastPrice, highPrice, lowPrice, percentage, absolute, volume, route, updateInterval, exchange, base, quote, pairSymbol, id);
        }
    }

    private int getImageResource(String baseSymbol) {
        switch (baseSymbol.toLowerCase()){
            case "ada": return R.mipmap.ada;
            case "amp": return R.mipmap.amp;
            case "ant": return R.mipmap.ant;
            case "ardr": return R.mipmap.ardr;
            case "ark": return R.mipmap.ark;
            case "avt": return R.mipmap.avt;
            case "bat": return R.mipmap.bat;
            case "bcc": return R.mipmap.bcc;
            case "bch": return R.mipmap.bch;
            case "bcn": return R.mipmap.bcn;
            case "bcy": return R.mipmap.bcy;
            case "bela": return R.mipmap.bela;
            case "blk": return R.mipmap.blk;
            case "block": return R.mipmap.block;
            case "bnt": return R.mipmap.bnt;
            case "bt1":
            case "bt2":
            case "btc": return R.mipmap.btc;
            case "btcd": return R.mipmap.btcd;
            case "btg": return R.mipmap.btg;
            case "btm": return R.mipmap.btm;
            case "bts": return R.mipmap.bts;
            case "burst": return R.mipmap.burst;
            case "clam": return R.mipmap.clam;
            case "cvc": return R.mipmap.cvc;
            case "dao": return R.mipmap.dao;
            case "dash": return R.mipmap.dash;
            case "dat": return R.mipmap.dat;
            case "data": return R.mipmap.data;
            case "dcr": return R.mipmap.dcr;
            case "dgb": return R.mipmap.dgb;
            case "doge": return R.mipmap.doge;
            case "edg": return R.mipmap.edg;
            case "edo": return R.mipmap.edo;
            case "elf":return R.mipmap.elf;
            case "emc": return R.mipmap.emc;
            case "emc2": return R.mipmap.emc2;
            case "eng": return R.mipmap.eng;
            case "eos": return R.mipmap.eos;
            case "etc": return R.mipmap.etc;
            case "eth": return R.mipmap.eth;
            case "etp": return R.mipmap.etp;
            case "eur": return R.mipmap.eur;
            case "exp": return R.mipmap.exp;
            case "fct": return R.mipmap.fct;
            case "fldc": return R.mipmap.fldc;
            case "flo": return R.mipmap.flo;
            case "fun": return R.mipmap.fun;
            case "game": return R.mipmap.game;
            case "gbyte": return R.mipmap.gbyte;
            case "gno": return R.mipmap.gno;
            case "gnt": return R.mipmap.gnt;
            case "grc": return R.mipmap.grc;
            case "huc": return R.mipmap.huc;
            case "icn": return R.mipmap.icn;
            case "icx": return R.mipmap.icx;
            case "iot": return R.mipmap.iot;
            case "kmd": return R.mipmap.kmd;
            case "lbc": return R.mipmap.lbc;
            case "link": return R.mipmap.link;
            case "lrc": return R.mipmap.lrc;
            case "lsk": return R.mipmap.lsk;
            case "ltc": return R.mipmap.ltc;
            case "maid": return R.mipmap.maid;
            case "mana": return R.mipmap.mana;
            case "mco": return R.mipmap.mco;
            case "mln": return R.mipmap.mln;
            case "mona": return R.mipmap.mona;
            case "nav": return R.mipmap.nav;
            case "gas":
            case "neo": return R.mipmap.neo;
            case "neos": return R.mipmap.neos;
            case "nmc": return R.mipmap.nmc;
            case "note": return R.mipmap.note;
            case "nxc": return R.mipmap.nxc;
            case "nxs": return R.mipmap.nxs;
            case "nxt": return R.mipmap.nxt;
            case "omg": return R.mipmap.omg;
            case "omni": return R.mipmap.omni;
            case "part": return R.mipmap.part;
            case "pasc": return R.mipmap.pasc;
            case "pay": return R.mipmap.pay;
            case "pink": return R.mipmap.pink;
            case "pivx": return R.mipmap.pivx;
            case "pot": return R.mipmap.pot;
            case "powr": return R.mipmap.powr;
            case "ppc": return R.mipmap.ppc;
            case "qtum": return R.mipmap.qtum;
            case "rads": return R.mipmap.rads;
            case "rcn": return R.mipmap.rcn;
            case "rdd": return R.mipmap.rdd;
            case "rep": return R.mipmap.rep;
            case "ric": return R.mipmap.ric;
            case "rlc": return R.mipmap.rlc;
            case "salt": return R.mipmap.salt;
            case "san": return R.mipmap.san;
            case "sc": return R.mipmap.sc;
            case "sdc": return R.mipmap.sdc;
            case "storj":
            case "sjcx": return R.mipmap.sjcx;
            case "snt": return R.mipmap.snt;
            case "srn": return R.mipmap.srn;
            case "sbd":
            case "steem": return R.mipmap.steem;
            case "strat": return R.mipmap.strat;
            case "sys": return R.mipmap.sys;
            case "tnb": return R.mipmap.tnb;
            case "trx": return R.mipmap.trx;
            case "usd": return R.mipmap.usd;
            case "usdt": return R.mipmap.usdt;
            case "via": return R.mipmap.via;
            case "vrc": return R.mipmap.vrc;
            case "vrm": return R.mipmap.vrm;
            case "waves": return R.mipmap.waves;
            case "xbc": return R.mipmap.xbc;
            case "xcp": return R.mipmap.xcp;
            case "xem": return R.mipmap.xem;
            case "xlm": return R.mipmap.xlm;
            case "xmr": return R.mipmap.xmr;
            case "xpm": return R.mipmap.xpm;
            case "xrp": return R.mipmap.xrp;
            case "xtz": return R.mipmap.xtz;
            case "xvc": return R.mipmap.xvc;
            case "xvg": return R.mipmap.xvg;
            case "xzc": return R.mipmap.xzc;
            case "zec": return R.mipmap.zec;
            case "zen": return R.mipmap.zen;
            case "zrx": return R.mipmap.zrx;
        }
        return 0;
    }
}
