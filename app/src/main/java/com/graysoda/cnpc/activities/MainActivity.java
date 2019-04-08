package com.graysoda.cnpc.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.graysoda.cnpc.Constants;
import com.graysoda.cnpc.DataFetcher;
import com.graysoda.cnpc.MainRVA;
import com.graysoda.cnpc.R;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = Constants.TAG + " MainActivity: ";
    private MainRVA rva;
    private LinearLayout deleteButtonBar;
    private FloatingActionButton fab;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d(TAG, "requestCode [" + requestCode + "]");

		for (int i=0; i< permissions.length; i++){
			Log.d(TAG, "permission [" + permissions[i] + "] [" + (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Constants.hasPermissions(getApplicationContext(), Manifest.permission.INTERNET, Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED)){
			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        	if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)){
				builder.setTitle(R.string.rational_title_internet)
						.setMessage(R.string.rational_msg_internet)
						.setPositiveButton(android.R.string.ok,(dialog, which) -> dialog.dismiss());
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Constants.PERMISSION_REQUEST_CODE);
			}

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.VIBRATE)){
				builder.setTitle(R.string.rational_title_vibrate)
						.setMessage(R.string.rational_msg_vibrate)
						.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, Constants.PERMISSION_REQUEST_CODE);
			}

			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)){
				builder.setTitle(R.string.rational_title_boot)
						.setMessage(R.string.rational_msg_boot)
						.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, Constants.PERMISSION_REQUEST_CODE);
			}
		}



		PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

		viewSetup();

		new DataFetcher(this).execute();
    }

	@SuppressLint("RestrictedApi")
	private void viewSetup() {
		Log.v(TAG, "viewSetup start");

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

		Log.v(TAG, "viewSetup end");
	}

	@Override
    protected void onResume() {
		Log.v(Constants.TAG,"on resume");
        super.onResume();
        rva.updateDataSet();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
				Log.d(Constants.TAG,"settings activity selected from menu");
				Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_delete: //make check boxes appear with delete and cancel button on bottom of display
				Log.d(Constants.TAG,"delete option selected from menu");
				deleteButtonBar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                rva.activateDeleteOptions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	protected void onStop() {
		super.onStop();
		//TODO close db via Interface
	}
}
