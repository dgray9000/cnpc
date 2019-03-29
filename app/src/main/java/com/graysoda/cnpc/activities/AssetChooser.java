package com.graysoda.cnpc.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.DialogRVA;
import com.graysoda.cnpc.NotificationCreator;
import com.graysoda.cnpc.R;
import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.NotificationData;
import com.graysoda.cnpc.datum.Pair;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class AssetChooser extends AppCompatActivity implements View.OnClickListener{
    private static final Logger logger = Constants.getLogger(AssetChooser.class);
    private DataManager dm;
    private static Asset chosenBase, chosenQuote;
    private Pair chosenPair;
    private static Exchange chosenExchange;
    private static String updateInterval;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_chooser);

        dm = new DataManager(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.assetChooser_button_done:
                logger.debug("done button clicked");
                if (chosenPair == null && chosenQuote != null && chosenBase != null && chosenExchange != null){
					chosenPair = dm.getPairByAssets(chosenBase, chosenQuote, chosenExchange);
				} else if (chosenPair != null && chosenExchange != null && updateInterval != null) {
					makeNotification(new NotificationData(updateInterval, chosenExchange, chosenPair));
				}
                else {
					new AlertDialog.Builder(getApplicationContext())
							.setTitle("Error")
							.setMessage("It appears you haven't selected some stuff, please do or hit the Cancel button at the bottom.")
							.setCancelable(true)
							.create().show();
					finish();
				}
                break;
            case R.id.assetChooser_button_cancel:
                logger.debug("cancel button clicked");
                finish();
                break;
            case R.id.assetChooser_button_base:
                logger.debug("base button clicked");
                makeSingleChoiceDialog(getBaseList(),0);
                break;
            case R.id.assetChooser_button_quote:
                logger.debug("quote button clicked");
                makeSingleChoiceDialog(getQuoteList(),1);
                break;
            case R.id.assetChooser_button_exchange:
                logger.debug("exchange button clicked");
                makeSingleChoiceDialog(getExchangeList(),2);
                break;
            case R.id.assetChooser_button_updateInterval:
                logger.debug("update interval button clicked");
                ArrayList<String> updateIntervalChoices = new ArrayList<>();
                if(Collections.addAll(updateIntervalChoices, getResources().getStringArray(R.array.update_interval_choices)))
                    makeSingleChoiceDialog(updateIntervalChoices,3);
                break;

            default: break;
        }
    }

	private ArrayList<Exchange> getExchangeList() {
		ArrayList<Exchange> exchanges = new ArrayList<>();

		for(Exchange exchange : dm.getAllExchanges()){
			if (!exchanges.contains(exchange)){
				exchanges.add(exchange);
			}
		}

		return exchanges;
	}

	private ArrayList<Asset> getQuoteList() {
		ArrayList<Asset> quotes = new ArrayList<>();

		for(Pair pair : dm.getAllPairs()){
			if (!quotes.contains(pair.getQuote())){
				quotes.add(pair.getQuote());
			}
		}

		return quotes;
	}

	private ArrayList<Asset> getBaseList() {
    	ArrayList<Asset> bases = new ArrayList<>();

    	for(Pair pair : dm.getAllPairs()){
    		if (!bases.contains(pair.getBase())){
    			bases.add(pair.getBase());
			}
		}

		return bases;
	}

	private void makeNotification(NotificationData data) {
        new NotificationCreator().create(getApplicationContext(), (int) new DataManager(getApplicationContext()).insertNotification(data), updateInterval);
    }

    private void makeSingleChoiceDialog(ArrayList data, final int button){
        Log.d("makeSingleChoiceDialog","start");
        //button 0=base 1=quote 2=exchange 3=updateInterval
        String title;

        switch(button){
            case 0: title = "Choose the base";
                    break;
            case 1: title = "Choose the quote";
                    break;
            case 2: title = "Choose the exchange";
                    break;
            case 3: title = "Choose the update interval";
                    break;
            default: title = "Error, this should not happen!";
        }

        logger.debug("title set to [" + title + "]");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogBody = this.getLayoutInflater().inflate(R.layout.dialog_body, null);

		RecyclerView dialogRV = dialogBody.findViewById(R.id.dialog_recyclerView);
		EditText search = dialogBody.findViewById(R.id.dialog_search_bar);

        DialogRVA dialogRVA = new DialogRVA(data, button);

        dialogRV.setAdapter(dialogRVA);
        dialogRV.setLayoutManager(new LinearLayoutManager(this));

		builder.setTitle(title);
		builder.setView(dialogBody);
		builder.create().show();

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				dialogRVA.filter(s.toString());
			}
		});
    }

	public static void setChosen(Object obj, int button) {
		//button 0=base 1=quote 2=exchange 3=updateInterval
		if (obj instanceof Asset){
			if (button == 0){
				chosenBase = (Asset) obj;
			} else if (button == 1){
				chosenQuote = (Asset) obj;
			}
		} else if (obj instanceof Exchange){
			chosenExchange = (Exchange) obj;
		} else if (obj instanceof String){
			updateInterval = (String) obj;
		}
	}
}
