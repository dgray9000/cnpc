package com.graysoda.cnpc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingFragment())
                .commit();
    }
}
