package pl.nikowis.focus.ui.facebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class FacebookSettings {

    private Set<String> pagesIdsAndNames;
    private Set<String> customPages;
    private MultiSelectListPreference selectedPagesPreference;
    private MultiSelectListPreference selectedCustomPagesPreference;
    private Preference addPagePreference;
    private CheckBoxPreference usingCustomPreference;
    private Preference reloadFacebookLikes;
    private Preference facebookLogout;
    private boolean usingCustom;
    private SharedPreferences prefs;
    private boolean userLoggedIn;
    private Context context;
    private SettingsFragment fragment;

    public FacebookSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        addPagePreference = fragment.findPreference(context.getString(R.string.key_pref_facebook_add_page));
        selectedPagesPreference = (MultiSelectListPreference) fragment.findPreference(context.getString(R.string.key_pref_facebook_selected_pages));
        selectedCustomPagesPreference = (MultiSelectListPreference) fragment.findPreference(context.getString(R.string.key_pref_facebook_selected_custom_pages));
        usingCustomPreference = (CheckBoxPreference) fragment.findPreference(context.getString(R.string.key_pref_facebook_using_custom_pages));
        reloadFacebookLikes = fragment.findPreference(context.getString(R.string.key_pref_facebook_reload_likes));
        reloadFacebookLikes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPagesPreferences();
                new FacebookLikesLoader(context, createLikesLoadedCallback()).loadAllLikes();
                setSelectedPagesPreferenceData();
                return true;
            }
        });
        facebookLogout = settingsFragment.findPreference(context.getString(R.string.key_pref_facebook_logout));
        userLoggedIn = Profile.getCurrentProfile() != null;
        Preference pageCount = settingsFragment.findPreference(context.getString(R.string.key_pref_facebook_page_count));
        pageCount.setEnabled(userLoggedIn);
        facebookLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPagesPreferences();
                prefs.edit().remove(context.getString(R.string.key_pref_facebook_selected_custom_pages)).apply();
                LoginManager.getInstance().logOut();
                navigateToMainActivity();
                return true;
            }
        });

        customPages = prefs.getStringSet(context.getString(R.string.key_pref_facebook_selected_custom_pages), new HashSet<String>());

        usingCustom = usingCustomPreference.isChecked();
        setupEnabledPreferences();
        usingCustomPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                usingCustom = (Boolean) newValue;
                setupEnabledPreferences();
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
                    Toast.makeText(fragment.getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String page = newValue.toString() + context.getString(R.string.facebook_id_name_separator) + newValue.toString();
                        customPages.add(page);
                        selectedCustomPagesPreference.getValues().add(page);
                        setSelectedPagesPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(fragment.getActivity(), R.string.facebook_select_page_already_on_list, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    private void clearPagesPreferences() {
        prefs.edit().remove(context.getString(R.string.key_pref_facebook_selected_pages)).apply();
        prefs.edit().remove(context.getString(R.string.key_pref_facebook_liked_pages_ids_and_names)).apply();
    }

    private FacebookLikesLoader.FinishedLoadingListener createLikesLoadedCallback() {
        return new FacebookLikesLoader.FinishedLoadingListener() {
            @Override
            public void finished() {
                navigateToMainActivity();
            }
        };
    }

    private void setupEnabledPreferences() {
        addPagePreference.setEnabled(userLoggedIn && usingCustom);
        selectedCustomPagesPreference.setEnabled(userLoggedIn && usingCustom);
        selectedPagesPreference.setEnabled(userLoggedIn && !usingCustom);
        reloadFacebookLikes.setEnabled(userLoggedIn && !usingCustom);
        facebookLogout.setEnabled(userLoggedIn);
        usingCustomPreference.setEnabled(userLoggedIn);
    }

    private void setSelectedPagesPreferenceData() {
        if (usingCustom) {
            List<String> pagesNames = getPageNamesList(customPages);
            CharSequence[] entries = pagesNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = customPages.toArray(new CharSequence[0]);
            selectedCustomPagesPreference.setEntries(entries);
            selectedCustomPagesPreference.setEntryValues(entriesValues);
        } else {
            pagesIdsAndNames = prefs.getStringSet(context.getString(R.string.key_pref_facebook_liked_pages_ids_and_names), new HashSet<String>());
            List<String> pagesNames = getPageNamesList(pagesIdsAndNames);
            CharSequence[] entries = pagesNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = pagesIdsAndNames.toArray(new CharSequence[0]);
            selectedPagesPreference.setEntries(entries);
            selectedPagesPreference.setEntryValues(entriesValues);
        }
    }

    private void navigateToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        fragment.startActivity(i);
        fragment.getActivity().finish();
    }

    public List<String> getPageNamesList(Set<String> idsAndNames) {
        ArrayList<String> names = new ArrayList<>(idsAndNames.size());
        for (String pageIdAndName : idsAndNames) {
            String[] split = pageIdAndName.split(context.getString(R.string.facebook_id_name_separator));
            names.add(split[1]);
        }
        return names;
    }
}
