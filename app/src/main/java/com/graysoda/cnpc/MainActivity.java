package com.graysoda.cnpc;

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


public class MainActivity extends AppCompatActivity {
    private MainRVA rva;
    private LinearLayout deleteButtonBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

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
        super.onResume();
        rva.updateDataSet();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_delete: //make check boxes appear with delete and cancel button on bottom of display
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
}
