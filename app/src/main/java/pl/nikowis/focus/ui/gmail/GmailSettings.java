package pl.nikowis.focus.ui.gmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import pl.nikowis.focus.ui.base.MainActivity;
import pl.nikowis.focus.ui.base.SettingsFragment;

/**
 * Created by Nikodem on 5/1/2017.
 */

public class GmailSettings {

    /**
     * Preference key for storing if user is logged in.
     */
    public static final String KEY_PREF_GMAIL_LOGGED_IN = "pref_gmail_logged_in";

    /**
     * Preference key for gmail logout button.
     */
    public static final String KEY_PREF_LOGOUT = "pref_gmail_logout";

    /**
     * Preference key for storing logged in username.
     */
    public static final String KEY_PREF_GMAIL_ACCOUNT_NAME = "pref_gmail_account_name";

    public static final String KEY_PREF_PAGE_COUNT = "pref_gmail_page_count";

    private Context context;
    private SettingsFragment fragment;
    private boolean loggedIn;

    public GmailSettings(SettingsFragment settingsFragment) {
        this.fragment = settingsFragment;
        this.context = fragment.getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Preference gmailLogout = settingsFragment.findPreference(KEY_PREF_LOGOUT);
        loggedIn = prefs.getBoolean(KEY_PREF_GMAIL_LOGGED_IN, false);
        Preference pageCount = settingsFragment.findPreference(KEY_PREF_PAGE_COUNT);
        pageCount.setEnabled(loggedIn);
        gmailLogout.setEnabled(loggedIn);
        gmailLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefs.edit().remove(KEY_PREF_GMAIL_LOGGED_IN).apply();
                prefs.edit().remove(KEY_PREF_GMAIL_ACCOUNT_NAME).apply();
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
