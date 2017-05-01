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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class FacebookSettings {
    /**
     * Preference key for adding new custom pages.
     */
    public static final String KEY_PREF_ADD_PAGE = "pref_facebook_add_pages";
    /**
     * Preference key for selected regular pages.
     */
    public static final String KEY_PREF_SELECTED_PAGES = "pref_facebook_select_pages";
    /**
     * Preference key for selected custom pages.
     */
    public static final String KEY_PREF_SELECTED_CUSTOM_PAGES = "pref_facebook_select_custom_pages";
    /**
     * Preference key for boolean describing if custom pages are being used.
     */
    public static final String KEY_PREF_USING_CUSTOM_PAGES = "pref_facebook_using_custom_pages";
    /**
     * Preference key for liked pages ids concatenated with their names.
     */
    public static final String KEY_PREF_LIKED_PAGES_IDS_AND_NAMES = "pref_facebook_liked_pages_ids_and_names";

    /**
     * Id - name separator.
     */
    public static final String ID_NAME_SEPARATOR = ";;;;";

    /**
     * Preference key for reload facebook likes.
     */
    public static final String KEY_PREF_RELOAD_FACEBOOK_LIKES = "pref_facebook_reload_likes";

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
    private Context context;
    private SettingsFragment fragment;

    public FacebookSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        addPagePreference = fragment.findPreference(KEY_PREF_ADD_PAGE);
        selectedPagesPreference = (MultiSelectListPreference) fragment.findPreference(KEY_PREF_SELECTED_PAGES);
        selectedCustomPagesPreference = (MultiSelectListPreference) fragment.findPreference(KEY_PREF_SELECTED_CUSTOM_PAGES);
        usingCustomPreference = (CheckBoxPreference) fragment.findPreference(KEY_PREF_USING_CUSTOM_PAGES);
        reloadFacebookLikes = fragment.findPreference(KEY_PREF_RELOAD_FACEBOOK_LIKES);
        reloadFacebookLikes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPagesPreferences();
                new FacebookLikesLoader(context, createLikesLoadedCallback()).loadAllLikes();
                setSelectedPagesPreferenceData();
                return true;
            }
        });
        facebookLogout = settingsFragment.findPreference(KEY_PREF_FACEBOOK_LOGOUT);
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
                    Toast.makeText(fragment.getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String page = newValue.toString() + ID_NAME_SEPARATOR + newValue.toString();
                        customPages.add(page);
                        selectedCustomPagesPreference.getValues().add(page);
                        setSelectedPagesPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(fragment.getActivity(), "This page is on the list", Toast.LENGTH_SHORT).show();
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
        Intent i = new Intent(context, MainActivity.class);
        fragment.startActivity(i);
        fragment.getActivity().finish();
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
