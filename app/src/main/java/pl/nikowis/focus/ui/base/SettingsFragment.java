package pl.nikowis.focus.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.R;
import pl.nikowis.focus.ui.facebook.FacebookLikesLoader;

/**
 * Created by Nikodem on 3/24/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    /**
     * Preference key for adding new custom pages.
     */
    public static final String KEY_PREF_ADD_PAGE = "pref_add_pages";
    /**
     * Preference key for selected regular pages.
     */
    public static final String KEY_PREF_SELECTED_PAGES = "pref_select_pages";
    /**
     * Preference key for selected custom pages.
     */
    public static final String KEY_PREF_SELECTED_CUSTOM_PAGES = "pref_select_custom_pages";
    /**
     * Preference key for boolean describing if custom pages are being used.
     */
    public static final String KEY_PREF_USING_CUSTOM_PAGES = "pref_using_custom_pages";
    /**
     * Preference key for liked pages ids concatenated with their names.
     */
    public static final String KEY_PREF_LIKED_PAGES_IDS_AND_NAMES = "pref_liked_pages_ids_and_names";

    /**
     * Id - name separator.
     */
    public static final String ID_NAME_SEPARATOR = ";;;;";

    /**
     * Preference key for reload facebook likes.
     */
    public static final String KEY_PREF_RELOAD_FACEBOOK_LIKES = "pref_reload_facebook_likes";

    /**
     * Preference key for facebook logout button.
     */
    public static final String KEY_PREF_FACEBOOK_LOGOUT = "pref_facebook_logout";

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPagePreference = findPreference(KEY_PREF_ADD_PAGE);
        selectedPagesPreference = (MultiSelectListPreference) findPreference(KEY_PREF_SELECTED_PAGES);
        selectedCustomPagesPreference = (MultiSelectListPreference) findPreference(KEY_PREF_SELECTED_CUSTOM_PAGES);
        usingCustomPreference = (CheckBoxPreference) findPreference(KEY_PREF_USING_CUSTOM_PAGES);
        reloadFacebookLikes = findPreference(KEY_PREF_RELOAD_FACEBOOK_LIKES);
        reloadFacebookLikes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPagesPreferences();
                new FacebookLikesLoader(getActivity(), createLikesLoadedCallback()).loadAllLikes();
                setSelectedPagesPreferenceData();
                return true;
            }
        });
        facebookLogout = findPreference(KEY_PREF_FACEBOOK_LOGOUT);
        userLoggedIn = Profile.getCurrentProfile() != null;

        facebookLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPagesPreferences();
                prefs.edit().remove(KEY_PREF_SELECTED_CUSTOM_PAGES).apply();
                LoginManager.getInstance().logOut();
                navigateToMainActivity();
                return true;
            }
        });

        customPages = prefs.getStringSet(KEY_PREF_SELECTED_CUSTOM_PAGES, new HashSet<String>());

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
                    Toast.makeText(getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        customPages.add(newValue.toString() + ID_NAME_SEPARATOR + newValue.toString());
                        setSelectedPagesPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getActivity(), "This page is on the list", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    private void clearPagesPreferences() {
        prefs.edit().remove(KEY_PREF_SELECTED_PAGES).apply();
        prefs.edit().remove(KEY_PREF_LIKED_PAGES_IDS_AND_NAMES).apply();
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
            pagesIdsAndNames = prefs.getStringSet(KEY_PREF_LIKED_PAGES_IDS_AND_NAMES, new HashSet<String>());
            List<String> pagesNames = getPageNamesList(pagesIdsAndNames);
            CharSequence[] entries = pagesNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = pagesIdsAndNames.toArray(new CharSequence[0]);
            selectedPagesPreference.setEntries(entries);
            selectedPagesPreference.setEntryValues(entriesValues);
        }
    }

    private void navigateToMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    public List<String> getPageNamesList(Set<String> idsAndNames) {
        ArrayList<String> names = new ArrayList<>(idsAndNames.size());
        for (String pageIdAndName : idsAndNames) {
            String[] split = pageIdAndName.split(ID_NAME_SEPARATOR);
            names.add(split[1]);
        }
        return names;
    }
}
