package com.graysoda.cnpc;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.NotificationData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class MainRVA extends RecyclerView.Adapter<MainRVA.ItemViewHolder> {
    private static final String TAG = "MainRVA#";
    private static ArrayList<NotificationData> notifications;
    private static ArrayList<Integer> markedForDeletion;
    private static final String defaultUpdateText = " is updated every ";
    private static final String defaultExchangeText = " from ";
    private final Context context;
    private DataManager dm;
    private boolean areDeleting = false;

    public MainRVA(Context context){
        notifications = new ArrayList<>();
        markedForDeletion = new ArrayList<>();
        this.context = context;
        dm = new DataManager(context);
        notifications.addAll(dm.getAllNotifications());
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final NotificationData notification = notifications.get(position);
        final int pos = position;
        String text = notification.getPairSymbol().toUpperCase() + defaultUpdateText + notification.getUpdateInterval() +
                defaultExchangeText + notification.getExchange();

        holder.textView.setText(text);
        holder.onOff.setChecked(notification.getIsOn());
        Picasso.get().load(Constants.iconUrl+notification.getBaseSymbol()+".png").into(holder.symbol);

        if (holder.onOff.isChecked()){
            new NotificationCreator().create(context, (int) notification.getId(),notification.getUpdateInterval());
        }

        holder.onOff.setOnCheckedChangeListener((compoundButton, b) -> {
            notifications.get(pos).setIsOn(b);

            if (b){
                dm.updateNotification(notification);
                new NotificationCreator().create(context, (int) notification.getId(),notification.getUpdateInterval());
                Toast.makeText(context, "Notification " + notification.getPairSymbol().toUpperCase() + " is on.", Toast.LENGTH_SHORT).show();
            } else {
                stopUpdates(notification);
            }
        });

        if (areDeleting){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.bringToFront();
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                markedForDeletion.add(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateDataSet(){
        notifications.clear();
        notifications.addAll(dm.getAllNotifications());
        notifyDataSetChanged();
    }

    public void activateDeleteOptions(){
        areDeleting = true;
        notifyDataSetChanged();
    }

    public void deactivateDeleteOptions(){
        areDeleting = false;
        notifyDataSetChanged();
    }

    public void delete(){
        for (Integer i: markedForDeletion){
            if (dm.deleteNotification(notifications.get(i).getId())){
                stopUpdates(notifications.get(i));
                notifications.remove(notifications.get(i));
            } else {
                Toast.makeText(context, "Error deleting notification [" + i + "].", Toast.LENGTH_SHORT).show();
            }
        }
        areDeleting = false;
        notifyDataSetChanged();
    }

    private void stopUpdates(NotificationData data) {
        if (!areDeleting){
            dm.updateNotification(data);
        }
        int id = (int) data.getId();

        new NotificationCreator().cancel(context, id);
        ((NotificationManager) Objects.requireNonNull(context.getSystemService(Context.NOTIFICATION_SERVICE))).cancel(data.getPairSymbol(), id);
        Toast.makeText(context, "Notification " + data.getPairSymbol().toUpperCase() + " is off.", Toast.LENGTH_SHORT).show();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView symbol;
        TextView textView;
        Switch onOff;
        RelativeLayout ll;
        CheckBox delete;

        ItemViewHolder(View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.rvTicker_imageView);
            textView = itemView.findViewById(R.id.rvTicker_textView);
            onOff = itemView.findViewById(R.id.rvTicker_switch);
            ll = itemView.findViewById(R.id.linearLayout_cardView);
            delete = itemView.findViewById(R.id.checkBox);
        }
    }
}
