package com.graysoda.cnpc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.graysoda.cnpc.R;
import com.graysoda.cnpc.SettingFragment;

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
