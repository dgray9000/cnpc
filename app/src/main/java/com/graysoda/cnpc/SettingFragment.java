package com.graysoda.cnpc;


import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragmentCompat {
    private static final String KEY_RINGTONE_PREFERENCE = "pref_ringtone";
    private static final int REQUEST_CODE_ALERT_RINGTONE = 103;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(KEY_RINGTONE_PREFERENCE)){
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

            String existingValue = getRingtonePreferenceValue();
            if (existingValue != null){
                if (existingValue.length() == 0){
                    //Select Silent
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                }
            } else {
                //No ringtone has been selected, set to default
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
            }

            startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null){
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null){
                setRingtonePreferenceValue(ringtone.toString());
            } else {
                // Silent was selected
                setRingtonePreferenceValue("");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getRingtonePreferenceValue() {
        return getContext().getSharedPreferences
                (
                        getString(R.string.key_notification_shared_preferences),
                        Context.MODE_PRIVATE
                )
                .getString
                (
                        getString(R.string.pref_ringtone),
                        null
                );
    }

    public void setRingtonePreferenceValue(String ringtonePreferenceValue) {
        getContext().getSharedPreferences
                (
                        getString(R.string.key_notification_shared_preferences),
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString
                        (
                                getString(R.string.pref_ringtone),
                                ringtonePreferenceValue
                        )
                .apply();
    }
}
