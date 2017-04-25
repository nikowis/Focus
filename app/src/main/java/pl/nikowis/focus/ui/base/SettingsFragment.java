package pl.nikowis.focus.ui.base;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_ADD_PAGE = "pref_add_pages";
    public static final String KEY_PREF_SELECTED_PAGES = "select_pages";

    private Set<String> pages = new HashSet<>();
    private MultiSelectListPreference listPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pages = prefs.getStringSet(KEY_PREF_SELECTED_PAGES, new HashSet<String>());

        addPreferencesFromResource(R.xml.preferences);

        listPreference = (MultiSelectListPreference) findPreference(KEY_PREF_SELECTED_PAGES);
        setListPreferenceData();

        Preference addPagePref = findPreference(KEY_PREF_ADD_PAGE);
        addPagePref.setPersistent(false);
        addPagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(((String)newValue).isEmpty()) {
                    Toast.makeText(getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        pages.add(newValue.toString());
                        setListPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getActivity(), "This page is on the list", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

    }

    private void setListPreferenceData() {
        CharSequence[] entries = pages.toArray(new CharSequence[0]);
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entries);
    }

}
