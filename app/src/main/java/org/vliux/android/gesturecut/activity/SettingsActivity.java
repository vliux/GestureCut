package org.vliux.android.gesturecut.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.service.GestureWindowService;
import org.vliux.android.gesturecut.util.PreferenceHelper;

/**
 * Created by vliux on 4/28/14.
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_frame, new SettingsFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(getString(R.string.pref_key_float_wnd).equals(key)){
                if(PreferenceHelper.getUserPref(getActivity(), R.string.pref_key_float_wnd, true)){
                    GestureWindowService.showWindow(getActivity());
                }else{
                    GestureWindowService.hideWindow(getActivity());
                }
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(getText(R.string.setting_rate_title).equals(preference.getTitle())){
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName()));
                startActivity(rateIntent);
                return true;
            }else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }
    }
}
