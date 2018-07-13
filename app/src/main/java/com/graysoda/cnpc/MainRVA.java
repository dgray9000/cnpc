package com.graysoda.cnpc;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.graysoda.cnpc.Database.DataManager;
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

    MainRVA(Context context){
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
        Picasso.with(context).load(getImageResource(notification.getBaseSymbol())).into(holder.symbol);

        if (holder.onOff.isChecked()){
            new NotificationCreator().create(context, (int) notification.getId(),notification.getUpdateInterval());
        }

        holder.onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notifications.get(pos).setIsOn(b);

                if (b){
                    dm.updateNotification(notification);
                    new NotificationCreator().create(context, (int) notification.getId(),notification.getUpdateInterval());
                    Toast.makeText(context, "Notification " + notification.getPairSymbol().toUpperCase() + " is on.", Toast.LENGTH_SHORT).show();
                } else {
                    stopUpdates(notification);
                }
            }
        });

        if (areDeleting){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.bringToFront();
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    markedForDeletion.add(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
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

    void updateDataSet(){
        notifications.clear();
        notifications.addAll(dm.getAllNotifications());
        notifyDataSetChanged();
    }

    void activateDeleteOptions(){
        areDeleting = true;
        notifyDataSetChanged();
    }

    void deactivateDeleteOptions(){
        areDeleting = false;
        notifyDataSetChanged();
    }

    void delete(){
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
