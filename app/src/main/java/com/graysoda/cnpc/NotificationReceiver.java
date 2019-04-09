package com.graysoda.cnpc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.NotificationData;

import static com.graysoda.cnpc.Constants.ACTION;
import static com.graysoda.cnpc.Constants.ID;
import static com.graysoda.cnpc.Constants.channelId;

public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.TAG + " Receiver:";
	private NotificationCompat.Builder mBuilder;
	private NotificationManager notificationManager;
	private int priority;
	private boolean showOnLockScreen, vibrate, ringtoneEnabled;
	private String ringtone;
	private long[] vibratePattern = {1000,1000,1000};

	@Override
	public void onReceive(Context context, Intent intent) {
		//new NotificationPublisher(context, intent).execute();

		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(context,channelId);

		//getting settings that apply to notifications
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Log.v(TAG, "#onRecieve: sharedPrefs get int = " + sp.getString(context.getString(R.string.pref_notification_priority),"error"));
		priority = Integer.parseInt(sp.getString(context.getString(R.string.pref_notification_priority), String.valueOf(NotificationManagerCompat.IMPORTANCE_MIN)));

		Log.v(TAG, "#onRecieve: showOnLockScreen = " + sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen),false));
		showOnLockScreen = sp.getBoolean(context.getString(R.string.pref_show_on_lock_screen), true);

		Log.v(TAG, "#onRecieve: vibrate = " + sp.getBoolean(context.getString(R.string.pref_vibrate),true));
		vibrate = sp.getBoolean(context.getString(R.string.pref_vibrate),false);

		Log.v(TAG, "#onRecieve: ringtoneEnabled = " + sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),true));
		ringtoneEnabled = sp.getBoolean(context.getString(R.string.pref_ringtone_enabled),false);

		Log.v(TAG, "#onRecieve: ringtone = " + sp.getString(context.getString(R.string.pref_ringtone),"error"));
		ringtone = sp.getString(context.getString(R.string.pref_ringtone),null);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if(notificationManager.getNotificationChannel(channelId) == null || !isNotificationChannelCorrect()){
				Log.v(TAG, "#onRecieve: notification channel is not correct");
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

	private void regularNotification(Context context, int id){
		//Log.d(DTAG","getting [" + id + "] from db");
		if (id > -1){
			NotificationData data = new DataManager(context).getNotification(id);

			if (data != null && data.getIsOn()){
				new NotificationPublisher(mBuilder, notificationManager, priority, showOnLockScreen, vibrate, ringtoneEnabled, ringtone).execute(data.getRoute(),
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
			Log.v(TAG, "#createNotificationChannel: Android is Oreo");
			NotificationChannel channel = new NotificationChannel(channelId,"Price Updates",priority);

			channel.setDescription(description);

			if(ringtoneEnabled){
				Log.v(TAG, "ringtone is enabled");
				channel.setSound(Uri.parse(ringtone),channel.getAudioAttributes());
			} else {
				Log.v(TAG, "ringtone not enabled");
				channel.setSound(null, null);
			}

			if (vibrate){
				Log.v(TAG, "vibrate enabled");
				channel.setVibrationPattern(vibratePattern);
			}

			channel.setLockscreenVisibility(showOnLockScreen ? NotificationCompat.VISIBILITY_PUBLIC : NotificationCompat.VISIBILITY_SECRET);

			notificationManager.createNotificationChannel(channel);
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

}
