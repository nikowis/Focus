package pl.nikowis.focus.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_ADD_PAGE = "pref_add_pages";
    public static final String KEY_PREF_SELECTED_PAGES = "select_pages";
    public static final String KEY_PREF_SELECTED_CUSTOM_PAGES = "select_custom_pages";
    public static final String KEY_PREF_LIKED_PAGES_IDS = "liked_pages_ids";
    public static final String KEY_PREF_LIKED_PAGES_NAMES = "liked_pages_names";

    private Set<String> pagesIds;
    private Set<String> pagesNames;
    private MultiSelectListPreference listPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pagesIds = prefs.getStringSet(KEY_PREF_LIKED_PAGES_IDS, new HashSet<String>());
        pagesNames = prefs.getStringSet(KEY_PREF_LIKED_PAGES_NAMES, new HashSet<String>());

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
                        //dodac osobna liste do customowych
//                        pagesIds.add(newValue.toString());
//                        pagesNames.add(newValue.toString());
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
        CharSequence[] entries = pagesNames.toArray(new CharSequence[0]);
        CharSequence[] entriesValues = pagesIds.toArray(new CharSequence[0]);
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entriesValues);
    }

}
