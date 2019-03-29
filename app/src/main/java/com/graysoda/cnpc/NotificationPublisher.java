package com.graysoda.cnpc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import com.graysoda.cnpc.activities.MainActivity;
import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.NotificationData;
import com.squareup.picasso.Picasso;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

import static com.graysoda.cnpc.Constants.ACTION;
import static com.graysoda.cnpc.Constants.ID;
import static com.graysoda.cnpc.Constants.channelId;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class NotificationPublisher extends BroadcastReceiver{
    private static final Logger logger = Constants.getLogger(NotificationPublisher.class);
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private int priority;
    private boolean showOnLockScreen, vibrate, ringtoneEnabled;
    private String ringtone;
    private long[] vibratePattern = {1000,1000,1000};

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context,channelId);

        //getting settings that apply to notifications
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        logger.trace("#onRecieve: sharedPrefs get int = " + sp.getString(context.getString(R.string.pref_notification_priority),"error"));
        priority = Integer.parseInt(sp.getString(context.getString(R.string.pref_notification_priority), String.valueOf(NotificationManagerCompat.IMPORTANCE_MIN)));

        logger.trace("#onRecieve: showOnLockScreen = " + sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen),false));
        showOnLockScreen = sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen), true);

        logger.trace("#onRecieve: vibrate = " + sp.getBoolean(context.getString(R.string.pref_vibrate),true));
        vibrate = sp.getBoolean(context.getString(R.string.pref_vibrate),false);

        logger.trace("#onRecieve: ringtoneEnabled = " + sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),true));
        ringtoneEnabled = sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),false);

        logger.trace("#onRecieve: ringtone = " + sp.getString(context.getString(R.string.pref_ringtone),"error"));
        ringtone = sp.getString(context.getString(R.string.pref_ringtone),null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(channelId) == null || !isNotificationChannelCorrect()){
                logger.trace("#onRecieve: notification channel is not correct");
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
            logger.trace("#createNotificationChannel: Android is Oreo");
            NotificationChannel channel = new NotificationChannel(channelId,"Price Updates",priority);

            channel.setDescription(description);

            if(ringtoneEnabled){
                logger.trace("ringtone is enabled");
                channel.setSound(Uri.parse(ringtone),channel.getAudioAttributes());
            } else {
                logger.trace("ringtone not enabled");
                channel.setSound(null, null);
            }

            if (vibrate){
                logger.trace("vibrate enabled");
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
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);

        try {
            mBuilder.setLargeIcon(Picasso.get().load(Constants.iconUrl+priceData.getBaseSymbol()+".png").get());
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setPriority(priority);

        mBuilder.setVisibility(showOnLockScreen ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET);

        if (vibrate)
            mBuilder.setVibrate(vibratePattern);

        if (ringtoneEnabled)
            mBuilder.setSound(Uri.parse(ringtone));

        // intents to open the app when the notification is clicked on
        Intent intent = new Intent(mBuilder.mContext,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mBuilder.mContext,105,intent,PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);

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
                } catch (IOException | JSONException e) {
                    logger.error(e.getMessage());
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
}
