package org.vliux.android.gesturecut.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.service.GestureWindowService;
import org.vliux.android.gesturecut.util.PreferenceHelper;

/**
 * Created by vliux on 4/28/14.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
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
            if(getString(R.string.pref_key_lockscreen_status).equals(key)){
                if(PreferenceHelper.getUserPref(getActivity(), R.string.pref_key_lockscreen_status, true)){
                    GestureWindowService.showWindow(getActivity());
                }else{
                    GestureWindowService.hideWindow(getActivity());
                }
            }
        }
    }
}
