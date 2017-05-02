package pl.nikowis.focus.ui.twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
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
     * Preference key for storing user authorization token.
     */
    public static final String KEY_PREF_TWITTER_AUTH_TOKEN_SECRET = "pref_twitter_auth_token_secret";

    /**
     * Preference key for twitter logout button.
     */
    public static final String KEY_PREF_LOGOUT = "pref_twitter_logout";


    public static final String KEY_PREF_PAGE_COUNT = "pref_twitter_page_count";



    private Context context;
    private SettingsFragment fragment;
    private String authorizationToken;

    public TwitterSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Preference twitterLogout = settingsFragment.findPreference(KEY_PREF_LOGOUT);
        authorizationToken = prefs.getString(KEY_PREF_TWITTER_AUTH_TOKEN, null);
        boolean userLoggedIn = authorizationToken != null;
        twitterLogout.setEnabled(userLoggedIn);
        twitterLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefs.edit().remove(KEY_PREF_TWITTER_AUTH_TOKEN).apply();
                prefs.edit().remove(KEY_PREF_TWITTER_AUTH_TOKEN_SECRET).apply();
                navigateToMainActivity();
                return true;
            }
        });

    }

    private void navigateToMainActivity() {
        Intent i = new Intent(context, MainActivity.class);
        fragment.startActivity(i);
        fragment.getActivity().finish();
    }

}
