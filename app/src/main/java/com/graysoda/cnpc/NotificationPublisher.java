package com.graysoda.cnpc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.graysoda.cnpc.activities.MainActivity;
import com.graysoda.cnpc.datum.NotificationData;
import com.squareup.picasso.Picasso;

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

public class NotificationPublisher extends AsyncTask<String, Void, Void>{
    private static final String TAG = Constants.TAG + " Publisher:";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private int priority;
    private boolean showOnLockScreen, vibrate, ringtoneEnabled;
    private String ringtone;
    private long[] vibratePattern = {1000,1000,1000};

    public NotificationPublisher(NotificationCompat.Builder mBuilder, NotificationManager notificationManager, int priority, boolean showOnLockScreen, boolean vibrate, boolean ringtoneEnabled, String ringtone) {
		this.mBuilder = mBuilder;
		this.notificationManager = notificationManager;
		this.priority = priority;
		this.showOnLockScreen = showOnLockScreen;
		this.vibrate = vibrate;
		this.ringtoneEnabled = ringtoneEnabled;
		this.ringtone = ringtone;
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
            Log.v(TAG, e.getMessage());
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
        Intent intent = new Intent(MyApplication.get(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.get(),105,intent,PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(priceData.getPairSymbol(),(int) priceData.getId(),mBuilder.build());
    }

    @Override
    protected Void doInBackground(String... strings) {
		try {

			URL url = new URL(strings[0]);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			int statusCode = connection.getResponseCode();
			if (statusCode == HttpsURLConnection.HTTP_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				while(line!=null){
					sb.append(line);
					line=reader.readLine();
				}
				publishNotification(parsePriceData(sb.toString(),strings));
			}
		} catch (IOException | JSONException e) {
			Log.d(TAG, e.getMessage());
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

    /**
     * Gets the price data based on the url from the db
     */
}
