package pl.nikowis.focus.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import pl.nikowis.focus.R;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_ADD_PAGE = "pref_add_pages";
    public static final String KEY_PREF_SELECTED_PAGES = "pref_select_pages";
    public static final String KEY_PREF_SELECTED_CUSTOM_PAGES = "pref_select_custom_pages";
    public static final String KEY_PREF_USING_CUSTOM_PAGES = "pref_using_custom_pages";
    public static final String KEY_PREF_LIKED_PAGES_IDS = "pref_liked_pages_ids";
    public static final String KEY_PREF_LIKED_PAGES_NAMES = "pref_liked_pages_names";

    private Set<String> pagesIds;
    private Set<String> pagesNames;
    private Set<String> customPages;
    private MultiSelectListPreference selectedPagesPreference;
    private MultiSelectListPreference selectedCustomPagesPreference;
    private Preference addPagePreference;
    private CheckBoxPreference usingCustomPreference;
    private boolean usingCustom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        addPagePreference = findPreference(KEY_PREF_ADD_PAGE);
        selectedPagesPreference = (MultiSelectListPreference) findPreference(KEY_PREF_SELECTED_PAGES);
        selectedCustomPagesPreference = (MultiSelectListPreference) findPreference(KEY_PREF_SELECTED_CUSTOM_PAGES);
        usingCustomPreference = (CheckBoxPreference) findPreference(KEY_PREF_USING_CUSTOM_PAGES);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pagesIds = prefs.getStringSet(KEY_PREF_LIKED_PAGES_IDS, new HashSet<String>());
        pagesNames = prefs.getStringSet(KEY_PREF_LIKED_PAGES_NAMES, new HashSet<String>());
        customPages = prefs.getStringSet(KEY_PREF_SELECTED_CUSTOM_PAGES, new HashSet<String>());



        usingCustom = usingCustomPreference.isChecked();
        addPagePreference.setEnabled(usingCustom);
        selectedCustomPagesPreference.setEnabled(usingCustom);
        selectedPagesPreference.setEnabled(!usingCustom);
        usingCustomPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                usingCustom = (Boolean) newValue;
                addPagePreference.setEnabled(usingCustom);
                selectedCustomPagesPreference.setEnabled(usingCustom);
                selectedPagesPreference.setEnabled(!usingCustom);
                setSelectedPagesPreferenceData();
                return true;
            }
        });
        setSelectedPagesPreferenceData();

        addPagePreference.setPersistent(false);
        addPagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((String) newValue).isEmpty()) {
                    Toast.makeText(getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        customPages.add(newValue.toString());
                        setSelectedPagesPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getActivity(), "This page is on the list", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    private void setSelectedPagesPreferenceData() {
        if (usingCustom) {
            CharSequence[] entries = customPages.toArray(new CharSequence[0]);
            selectedCustomPagesPreference.setEntries(entries);
            selectedCustomPagesPreference.setEntryValues(entries);
        } else {
            CharSequence[] entries = pagesNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = pagesIds.toArray(new CharSequence[0]);
            selectedPagesPreference.setEntries(entries);
            selectedPagesPreference.setEntryValues(entriesValues);
        }
    }
}
