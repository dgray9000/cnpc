package com.graysoda.cnpc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static com.graysoda.cnpc.Constants.ID;

/**
 * Created by david.grayson on 3/23/2018.
 */

class NotificationCreator {
    NotificationCreator(){}

    private Long getUpdateDelay(String updateInterval){
        switch (updateInterval){
            case "1 minute": return (long)(60*1000);
            case "5 minutes": return (long)(60*5*1000);
            case "15 minutes": return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case "30 minutes": return AlarmManager.INTERVAL_HALF_HOUR;
            default:
            case "1 hour": return AlarmManager.INTERVAL_HOUR;
        }
    }

    void create(Context context,int id, String updateInterval){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(ID,id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis()+(1000),getUpdateDelay(updateInterval), pendingIntent);
        } else {
            //Log.d("NotificationCreator","AlarmManager is null");
            try {
                throw new InstantiationException("alarmManager is null");
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    void cancel(Context context, int id){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null){
            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(ID,id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
        } else {
            try{
                throw new InstantiationException("alarmManager is null");
            } catch (InstantiationException e){
                e.printStackTrace();
            }
        }
    }
}
