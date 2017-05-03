package pl.nikowis.focus.ui.instagram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

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

public class InstagramSettings {

    private Set<String> usersIdsAndNames;
    private Set<String> customUsers;
    private MultiSelectListPreference selectedUsersPreference;
    private MultiSelectListPreference selectedCustomUsersPreference;
    private Preference addUserPreference;
    private CheckBoxPreference usingCustomPreference;
    private Preference reloadInstagramFollows;
    private Preference instagramLogout;
    private boolean usingCustom;
    private SharedPreferences prefs;
    private boolean userLoggedIn;
    private Context context;
    private SettingsFragment fragment;
    private String authorizationToken = null;

    public InstagramSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        addUserPreference = fragment.findPreference(context.getString(R.string.key_pref_instagram_add_user));
        selectedUsersPreference = (MultiSelectListPreference) fragment.findPreference(context.getString(R.string.key_pref_instagram_selected_users));
        selectedCustomUsersPreference = (MultiSelectListPreference) fragment.findPreference(context.getString(R.string.key_pref_instagram_selected_custom_users));
        usingCustomPreference = (CheckBoxPreference) fragment.findPreference(context.getString(R.string.key_pref_instagram_using_custom_users));
        reloadInstagramFollows = fragment.findPreference(context.getString(R.string.key_pref_instagram_reload_follows));
        reloadInstagramFollows.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearUsersPreferences();
                new InstagramFollowsLoader(context, createFollowsLoadedCallback()).loadAllFollows();
                setSelectedUsersPreferenceData();
                return true;
            }
        });
        instagramLogout = settingsFragment.findPreference(context.getString(R.string.key_pref_instagram_logout));
        authorizationToken = prefs.getString(context.getString(R.string.key_pref_instagram_auth_token), null);
        userLoggedIn = authorizationToken != null;

        Preference pageCount = settingsFragment.findPreference(context.getString(R.string.key_pref_instagram_page_count));
        pageCount.setEnabled(userLoggedIn);

        instagramLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearUsersPreferences();
                prefs.edit().remove(context.getString(R.string.key_pref_instagram_selected_custom_users)).apply();
                prefs.edit().remove(context.getString(R.string.key_pref_instagram_auth_token)).apply();
                navigateToMainActivity();
                return true;
            }
        });

        customUsers = prefs.getStringSet(context.getString(R.string.key_pref_instagram_selected_custom_users), new HashSet<String>());

        usingCustom = usingCustomPreference.isChecked();
        setupEnabledPreferences();
        usingCustomPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                usingCustom = (Boolean) newValue;
                setupEnabledPreferences();
                setSelectedUsersPreferenceData();
                return true;
            }
        });
        setSelectedUsersPreferenceData();

        addUserPreference.setPersistent(false);
        addUserPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((String) newValue).isEmpty()) {
                    Toast.makeText(fragment.getActivity(), R.string.instagram_add_empty_user, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String user = newValue.toString() + context.getString(R.string.instagram_id_name_separator) + newValue.toString();
                        customUsers.add(user);
                        selectedCustomUsersPreference.getValues().add(user);
                        setSelectedUsersPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(fragment.getActivity(), R.string.instagram_add_user_on_the_list, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    private void clearUsersPreferences() {
        prefs.edit().remove(context.getString(R.string.key_pref_instagram_selected_users)).apply();
        prefs.edit().remove(context.getString(R.string.key_pref_instagram_followed_user_ids_and_names)).apply();
    }

    private InstagramFollowsLoader.FinishedLoadingListener createFollowsLoadedCallback() {
        return new InstagramFollowsLoader.FinishedLoadingListener() {
            @Override
            public void finished() {
                navigateToMainActivity();
            }
        };
    }

    private void setupEnabledPreferences() {
        addUserPreference.setEnabled(userLoggedIn && usingCustom);
        selectedCustomUsersPreference.setEnabled(userLoggedIn && usingCustom);
        selectedUsersPreference.setEnabled(userLoggedIn && !usingCustom);
        reloadInstagramFollows.setEnabled(userLoggedIn && !usingCustom);
        instagramLogout.setEnabled(userLoggedIn);
        usingCustomPreference.setEnabled(userLoggedIn);
    }

    private void setSelectedUsersPreferenceData() {
        if (usingCustom) {
            List<String> usersNames = getUserNamesList(customUsers);
            CharSequence[] entries = usersNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = customUsers.toArray(new CharSequence[0]);
            selectedCustomUsersPreference.setEntries(entries);
            selectedCustomUsersPreference.setEntryValues(entriesValues);
        } else {
            usersIdsAndNames = prefs.getStringSet(context.getString(R.string.key_pref_instagram_followed_user_ids_and_names), new HashSet<String>());
            List<String> usersNames = getUserNamesList(usersIdsAndNames);
            CharSequence[] entries = usersNames.toArray(new CharSequence[0]);
            CharSequence[] entriesValues = usersIdsAndNames.toArray(new CharSequence[0]);
            selectedUsersPreference.setEntries(entries);
            selectedUsersPreference.setEntryValues(entriesValues);
        }
    }

    private void navigateToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        fragment.startActivity(i);
        fragment.getActivity().finish();
    }

    public List<String> getUserNamesList(Set<String> idsAndNames) {
        ArrayList<String> names = new ArrayList<>(idsAndNames.size());
        for (String userIdAndName : idsAndNames) {
            String[] split = userIdAndName.split(context.getString(R.string.instagram_id_name_separator));
            names.add(split[1]);
        }
        return names;
    }
}
