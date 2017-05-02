package pl.nikowis.focus.ui.twitter;

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

import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class TwitterSettings {

    /**
     * Preference key for storing user authorization token.
     */
    public static final String KEY_PREF_TWITTER_AUTH_TOKEN = "pref_twitter_auth_token";
    /**
     * Preference key for storing user id.
     */
    public static final String KEY_PREF_TWITTER_USER_ID = "pref_twitter_user_id";

    /**
     * Preference key for adding new custom users.
     */
    public static final String KEY_PREF_ADD_USER = "pref_twitter_add_users";
    /**
     * Preference key for selected regular users.
     */
    public static final String KEY_PREF_SELECTED_USERS = "pref_twitter_select_users";
    /**
     * Preference key for selected custom users.
     */
    public static final String KEY_PREF_SELECTED_CUSTOM_USERS = "pref_twitter_select_custom_users";
    /**
     * Preference key for boolean describing if custom users are being used.
     */
    public static final String KEY_PREF_USING_CUSTOM_USERS = "pref_twitter_using_custom_users";
    /**
     * Preference key for liked users ids concatenated with their names.
     */
    public static final String KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES = "pref_twitter_followed_users_ids_and_names";

    /**
     * Id - name separator.
     */
    public static final String ID_NAME_SEPARATOR = ";;;;";

    /**
     * Preference key for reload twitter follows.
     */
    public static final String KEY_PREF_RELOAD_FOLLOWS = "pref_twitter_reload_follows";

    /**
     * Preference key for twitter logout button.
     */
    public static final String KEY_PREF_LOGOUT = "pref_twitter_logout";

    private Set<String> usersIdsAndNames;
    private Set<String> customUsers;
    private MultiSelectListPreference selectedUsersPreference;
    private MultiSelectListPreference selectedCustomUsersPreference;
    private Preference addUserPreference;
    private CheckBoxPreference usingCustomPreference;
    private Preference reloadTwitterFollows;
    private Preference twitterLogout;
    private boolean usingCustom;
    private SharedPreferences prefs;
    private boolean userLoggedIn;
    private Context context;
    private SettingsFragment fragment;
    private String authorizationToken = null;

    public TwitterSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        addUserPreference = fragment.findPreference(KEY_PREF_ADD_USER);
        selectedUsersPreference = (MultiSelectListPreference) fragment.findPreference(KEY_PREF_SELECTED_USERS);
        selectedCustomUsersPreference = (MultiSelectListPreference) fragment.findPreference(KEY_PREF_SELECTED_CUSTOM_USERS);
        usingCustomPreference = (CheckBoxPreference) fragment.findPreference(KEY_PREF_USING_CUSTOM_USERS);
        reloadTwitterFollows = fragment.findPreference(KEY_PREF_RELOAD_FOLLOWS);
        reloadTwitterFollows.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearUsersPreferences();
                new TwitterFollowsLoader(context, createFollowsLoadedCallback()).loadAllFollows();
                setSelectedUsersPreferenceData();
                return true;
            }
        });
        twitterLogout = settingsFragment.findPreference(KEY_PREF_LOGOUT);
        authorizationToken = prefs.getString(KEY_PREF_TWITTER_AUTH_TOKEN, null);
        userLoggedIn = authorizationToken != null;

        twitterLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearUsersPreferences();
                prefs.edit().remove(KEY_PREF_SELECTED_CUSTOM_USERS).apply();
                prefs.edit().remove(KEY_PREF_TWITTER_AUTH_TOKEN).apply();
                prefs.edit().remove(KEY_PREF_TWITTER_USER_ID).apply();
                navigateToMainActivity();
                return true;
            }
        });

        customUsers = prefs.getStringSet(KEY_PREF_SELECTED_CUSTOM_USERS, new HashSet<String>());

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
                    Toast.makeText(fragment.getActivity(), "Empty text", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String user = newValue.toString() + ID_NAME_SEPARATOR + newValue.toString();
                        customUsers.add(user);
                        selectedCustomUsersPreference.getValues().add(user);
                        setSelectedUsersPreferenceData();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(fragment.getActivity(), "This user is on the list", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    private void clearUsersPreferences() {
        prefs.edit().remove(KEY_PREF_SELECTED_USERS).apply();
        prefs.edit().remove(KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES).apply();
    }

    private TwitterFollowsLoader.FinishedLoadingListener createFollowsLoadedCallback() {
        return new TwitterFollowsLoader.FinishedLoadingListener() {
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
        reloadTwitterFollows.setEnabled(userLoggedIn && !usingCustom);
        twitterLogout.setEnabled(userLoggedIn);
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
            usersIdsAndNames = prefs.getStringSet(KEY_PREF_FOLLOWED_USERS_IDS_AND_NAMES, new HashSet<String>());
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
            String[] split = userIdAndName.split(ID_NAME_SEPARATOR);
            names.add(split[1]);
        }
        return names;
    }
}
