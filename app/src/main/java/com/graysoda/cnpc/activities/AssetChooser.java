package com.graysoda.cnpc.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.NotificationCreator;
import com.graysoda.cnpc.R;
import com.graysoda.cnpc.database.dao.DataManager;
import com.graysoda.cnpc.datum.Asset;
import com.graysoda.cnpc.datum.Exchange;
import com.graysoda.cnpc.datum.NotificationData;
import com.graysoda.cnpc.datum.Pair;

import java.util.ArrayList;

/**
 * Created by david.grayson on 3/23/2018.
 */

public class AssetChooser extends AppCompatActivity implements View.OnClickListener{
	private static final String TAG = Constants.TAG + " AssetChooser: ";
    private static DataManager dm;
    private Asset chosenBase, chosenQuote;
    private Pair chosenPair;
    private Exchange chosenExchange;
    private String updateInterval;
    private Button base, quote, exchange, update;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_chooser);

        dm = new DataManager(this);
        base = findViewById(R.id.assetChooser_button_base);
        quote = findViewById(R.id.assetChooser_button_quote);
        exchange = findViewById(R.id.assetChooser_button_exchange);
        update = findViewById(R.id.assetChooser_button_updateInterval);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.assetChooser_button_done:
                Log.d(TAG, "done button clicked");
                if (chosenPair == null && chosenQuote != null && chosenBase != null && chosenExchange != null){
					chosenPair = dm.getPairByAssets(chosenBase, chosenQuote, chosenExchange);

					if (chosenPair != null){
						makeNotification(new NotificationData(updateInterval, chosenExchange, chosenPair));
						finish();
					} else {
						Toast.makeText(this, "chosenPair is null", Toast.LENGTH_SHORT).show();
					}

				} else if (chosenPair != null && chosenExchange != null && updateInterval != null) {
					makeNotification(new NotificationData(updateInterval, chosenExchange, chosenPair));
					finish();
				}
                else {
					Toast.makeText(this, "It appears you haven't selected some stuff, please do or hit the Cancel button at the bottom.", Toast.LENGTH_LONG).show();
				}
                break;
            case R.id.assetChooser_button_cancel:
				Log.d(TAG,"cancel button clicked");
				finish();
                break;
            case R.id.assetChooser_button_base:
				Log.d(TAG,"base button clicked");
				makeAssetDialog(dm.getBases(), getString(R.string.choose_base_dialog_title), 0);
                break;
            case R.id.assetChooser_button_quote:
				Log.d(TAG,"quote button clicked");
				makeAssetDialog(getQuoteList(), getString(R.string.choose_quote_dialog_title), 1);
                break;
            case R.id.assetChooser_button_exchange:
				Log.d(TAG,"exchange button clicked");
				makeExchangeDialog(getExchangeList(), getString(R.string.choose_exchange_dialog_title));
                break;
            case R.id.assetChooser_button_updateInterval:
				Log.d(TAG,"update interval button clicked");
				makeUpdateIntervalDialog();
                break;

            default: break;
        }
    }

	private void makeUpdateIntervalDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item);

		stringArrayAdapter.addAll(Constants.UPDATE_INTERVAL_CHOICES);

		builder.setTitle(R.string.choose_updateInterval_dialog_title);
		builder.setAdapter(stringArrayAdapter, (dialog, which) -> setChosen(stringArrayAdapter.getItem(which),3));

		builder.create().show();
	}

	private void makeExchangeDialog(ArrayList<Exchange> exchangeList, String title) {
		ArrayList<Exchange> filteredList = new ArrayList<>(exchangeList);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogTitle = this.getLayoutInflater().inflate(R.layout.dialog_title, null);
		EditText search = dialogTitle.findViewById(R.id.dialog_search_bar);
		ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_item,convertExchangeToStringArray(filteredList));

		((TextView)dialogTitle.findViewById(R.id.custom_dialog_title)).setText(title); //sets the custom dialog title

		builder.setCustomTitle(dialogTitle);
		builder.setAdapter(stringArrayAdapter, (dialog, which) -> setChosen(filteredList.get(which), 2));

		AlertDialog dialog = builder.show();

		// these 3 lines allow the dialog to resize and the
		// keyboard to work for search functionality
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable filter) {
				filteredList.clear();
				stringArrayAdapter.clear();

				if (filter.toString().isEmpty()){
					filteredList.addAll(exchangeList);
				} else {
					for (Exchange exchange : exchangeList){
						if (exchange.getName().toLowerCase().contains(filter.toString().toLowerCase())){
							filteredList.add(exchange);
						}
					}
				}

				stringArrayAdapter.addAll(convertExchangeToStringArray(filteredList));
				stringArrayAdapter.notifyDataSetChanged();
			}
		});
	}

	private void makeAssetDialog(ArrayList<Asset> assetList, String title, int button) {
		ArrayList<Asset> filteredList = new ArrayList<>(assetList);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogTitle = this.getLayoutInflater().inflate(R.layout.dialog_title, null);
		EditText search = dialogTitle.findViewById(R.id.dialog_search_bar);
		ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_item,convertAssetToStringArray(filteredList));

		Log.d("demo", "adapter count = " + stringArrayAdapter.getCount());

		((TextView)dialogTitle.findViewById(R.id.custom_dialog_title)).setText(title); //sets the custom dialog title

		builder.setCustomTitle(dialogTitle);
		builder.setAdapter(stringArrayAdapter, (dialog, which) -> setChosen(filteredList.get(which), button));

		AlertDialog dialog = builder.show();

		// these 3 lines allow the dialog to resize and the
		// keyboard to work for search functionality
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable filter) {
				filteredList.clear();
				stringArrayAdapter.clear();

				if (filter.toString().isEmpty()){
					filteredList.addAll(assetList);
				} else {
					for (Asset asset : assetList){
						String s = asset.getSymbol() + " - " + asset.getName();
						if (s.toLowerCase().contains(filter.toString().toLowerCase())){
							filteredList.add(asset);
						}
					}
				}

				stringArrayAdapter.addAll(convertAssetToStringArray(filteredList));
				stringArrayAdapter.notifyDataSetChanged();
			}
		});
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
		ArrayList<Asset> unfilteredList = dm.getQuotes();

		if (chosenExchange != null){

		}

		return unfilteredList;
	}

	private void makeNotification(NotificationData data) {
        new NotificationCreator().create(getApplicationContext(), (int) new DataManager(getApplicationContext()).insertNotification(data), updateInterval);
    }

	private ArrayList<String> convertExchangeToStringArray(ArrayList<Exchange> exchangeList) {
		ArrayList<String> strings = new ArrayList<>();

		for (Exchange exchange : exchangeList){
			String s = exchange.getName();
			strings.add(s);
		}

		return strings;
	}

	private ArrayList<String> convertAssetToStringArray(ArrayList<Asset> assets) {
		ArrayList<String> strings = new ArrayList<>();
		String s;
		Asset asset;

		for (int i=0; i<assets.size(); i++){
			asset = assets.get(i);
			s = asset.getSymbol().toUpperCase() + " - " + asset.getName();
			strings.add(s);
		}

		return strings;
	}

	public void setChosen(Object obj, int button) {
		String buttonText;

		//button 0=base 1=quote 2=exchange 3=updateInterval
		if (obj instanceof Asset){
			buttonText = ((Asset) obj).getSymbol().toUpperCase() + " - " + ((Asset) obj).getName();
			if (button == 0){
				chosenBase = (Asset) obj;
				base.setText(buttonText);
			} else if (button == 1){
				chosenQuote = (Asset) obj;
				quote.setText(buttonText);
			}
		} else if (obj instanceof Exchange){
			chosenExchange = (Exchange) obj;
			buttonText = ((Exchange) obj).getName();
			exchange.setText(buttonText);
		} else if (obj instanceof String){
			updateInterval = (String) obj;
			update.setText(updateInterval);
		}
	}
}
