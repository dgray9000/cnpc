package com.graysoda.cnpc.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.DataFetcher;
import com.graysoda.cnpc.MainRVA;
import com.graysoda.cnpc.R;

import org.apache.log4j.Logger;


public class MainActivity extends AppCompatActivity {
    private static final Logger logger = Constants.getLogger(MainActivity.class);
    private MainRVA rva;
    private LinearLayout deleteButtonBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        viewSetup();

		new DataFetcher(this).execute(getString(R.string.baseCryptowatchApiUrl));
    }

	@SuppressLint("RestrictedApi")
	private void viewSetup() {
		fab = findViewById(R.id.floatingActionButton);
		deleteButtonBar = findViewById(R.id.linearLayout_main);

		fab.setOnClickListener(v -> {
			Intent intent = new Intent(getBaseContext(),AssetChooser.class);
			getApplicationContext().startActivity(intent);
		});

		RecyclerView rv = findViewById(R.id.rv);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rva = new MainRVA(getApplicationContext());
		rv.setAdapter(rva);

		findViewById(R.id.Button_main_cancelDelete).setOnClickListener(view -> {
			deleteButtonBar.setVisibility(View.INVISIBLE);
			fab.setVisibility(View.VISIBLE);
			rva.deactivateDeleteOptions();
		});

		findViewById(R.id.Button_main_confirmDelete).setOnClickListener(view -> {
			rva.delete();
			deleteButtonBar.setVisibility(View.INVISIBLE);
			fab.setVisibility(View.VISIBLE);
		});
	}

	@Override
    protected void onResume() {
        logger.trace("on resume");
        super.onResume();
        rva.updateDataSet();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                logger.debug("settings activity selected from menu");
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_delete: //make check boxes appear with delete and cancel button on bottom of display
                logger.debug("delete option selected from menu");
                deleteButtonBar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                rva.activateDeleteOptions();
                break;
            case R.id.menu_log:
                logger.debug("log view option selected from menu");
                Intent logView = new Intent(this,LogView.class);
                startActivity(logView);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
